<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import {
  selectbank, selectFinaceUser, selectMyFinance, addFinanceSingle, addFinanceMulti,
  selectFinanceById
} from '@/api/finance'
import { uploadFinance } from '@/api/file'
import { findByUser, updateSelf } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { phonePattern, idCardValidator, isPhone, isIdCard } from '@/utils/validate'
import type { Bank, Finance } from '@/types'

const baseURL = 'http://localhost:8083'
const userStore = useUserStore()
const banks = ref<Bank[]>([])
const myLoans = ref<Finance[]>([])
const loading = ref(false)

const dialog = ref(false)
const detailDialog = ref(false)
const formRef = ref<FormInstance>()
const loanType = ref<'single' | 'multi'>('single')
const currentBank = ref<Bank | null>(null)

// 个人信息里已有的真实姓名（决定表单 realName 是否禁用）
const profileRealName = ref('')

const form = reactive<Finance>({
  bankId: 0,
  realName: '',
  phone: '',
  idNum: '',
  money: 0,
  rate: 0,
  repayment: 12,
  fileInfo: ''
})

// 个人信息已有姓名时禁用真实姓名输入框
const realNameDisabled = computed(() => !!profileRealName.value)

// 组合贷款第二、第三个人
const comb2 = reactive({ name: '', phone: '', idnum: '' })
const comb3 = reactive({ name: '', phone: '', idnum: '' })

// 待上传文件 & 已上传 URL 列表
const pendingFiles = ref<File[]>([])
const uploadedUrls = ref<string[]>([])

const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: phonePattern, message: '请输入正确的 11 位手机号', trigger: 'blur' }
  ],
  idNum: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { validator: idCardValidator, trigger: 'blur' }
  ],
  money: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  repayment: [{ required: true, message: '请输入还款期', trigger: 'blur' }]
}

async function loadProfile() {
  // 拉取当前用户的真实姓名，用于决定表单是否禁用 realName 字段
  try {
    const res: any = await findByUser({ userName: userStore.userName, pageNum: 1, pageSize: 100 })
    const user = (res.data?.list || []).find((u: any) => u.userName === userStore.userName)
    profileRealName.value = user?.realName || ''
  } catch {
    /* 拉不到不影响主流程 */
  }
}

async function load() {
  loading.value = true
  try {
    const res: any = await selectbank()
    banks.value = res.data || []
    const r: any = await selectMyFinance()
    myLoans.value = r.data || []
  } finally {
    loading.value = false
  }
}

async function openLoan(b: Bank) {
  // 拉取银行详情（确认利率等）
  try {
    const res: any = await selectFinaceUser(b.bankId)
    currentBank.value = res.data || b
  } catch {
    currentBank.value = b
  }
  Object.assign(form, {
    bankId: b.bankId,
    // 有姓名就预填且不可改；没姓名留空让用户填
    realName: profileRealName.value || '',
    phone: '', idNum: '',
    money: b.money, rate: b.rate, repayment: b.repayment,
    fileInfo: ''
  })
  Object.assign(comb2, { name: '', phone: '', idnum: '' })
  Object.assign(comb3, { name: '', phone: '', idnum: '' })
  pendingFiles.value = []
  uploadedUrls.value = []
  loanType.value = 'single'
  dialog.value = true
}

function onFileChange(_file: any, fileList: any[]) {
  // 仅允许 pdf/doc/docx，选到其他格式时直接剔除并提示，避免提交时后端报错
  const allowed = ['pdf', 'doc', 'docx']
  const valid = fileList.filter((f: any) => {
    const name: string = f.name || ''
    const ext = name.includes('.') ? name.slice(name.lastIndexOf('.') + 1).toLowerCase() : ''
    return allowed.includes(ext)
  })
  if (valid.length !== fileList.length) {
    ElMessage.warning('贷款材料仅支持 pdf/doc/docx 格式，已自动剔除不符合的文件')
    pendingFiles.value = valid
    return
  }
  pendingFiles.value = fileList
}

async function uploadFiles(): Promise<boolean> {
  if (pendingFiles.value.length === 0) {
    ElMessage.warning('请先选择文件')
    return false
  }
  // el-upload 的 fileList 元素是 UploadFile 包装对象，真实文件在 .raw 上
  const raws = pendingFiles.value.map((f: any) => f.raw ?? f).filter(Boolean)
  if (raws.length === 0) {
    ElMessage.warning('文件读取失败，请重新选择')
    return false
  }
  try {
    const res: any = await uploadFinance(raws)
    uploadedUrls.value = res.data || []
    ElMessage.success(`已上传 ${uploadedUrls.value.length} 个文件`)
    return uploadedUrls.value.length > 0
  } catch {
    return false
  }
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    // 已选文件但忘记点“上传”时，提交前自动上传一次
    if (uploadedUrls.value.length === 0 && pendingFiles.value.length > 0) {
      const ok = await uploadFiles()
      if (!ok) return
    }
    if (uploadedUrls.value.length === 0) {
      ElMessage.warning('请上传贷款材料')
      return
    }
    // 个人信息原无姓名 → 用户在表单里填了姓名 → 同步到个人信息
    if (!profileRealName.value && form.realName) {
      try {
        await updateSelf({ realName: form.realName })
        profileRealName.value = form.realName
        ElMessage.success('已将姓名同步到个人信息')
      } catch {
        /* 同步失败不阻断贷款申请 */
      }
    }
    const fileInfoStr = JSON.stringify(uploadedUrls.value)
    if (loanType.value === 'single') {
      try {
        await addFinanceSingle({ ...form, fileInfo: fileInfoStr })
        ElMessage.success('申请成功')
        dialog.value = false
        load()
      } catch {
        /* */
      }
    } else {
      // 组合贷款：请求体是数组，至少 2 个；联合贷款人 2 必填且格式合法
      if (!comb2.name || !isPhone(comb2.phone) || !isIdCard(comb2.idnum)) {
        ElMessage.warning('请完整填写联合贷款人 2 的姓名、正确手机号和身份证号')
        return
      }
      // 联合贷款人 3 可选：只要填了任意一项，就要求三项齐全且格式合法
      const comb3Filled = comb3.name || comb3.phone || comb3.idnum
      if (comb3Filled && (!comb3.name || !isPhone(comb3.phone) || !isIdCard(comb3.idnum))) {
        ElMessage.warning('联合贷款人 3 信息不完整或手机号/身份证格式不正确')
        return
      }
      const arr: Finance[] = [
        { ...form, fileInfo: fileInfoStr, combinationName1: comb2.name, combinationPhone1: comb2.phone, combinationIdnum1: comb2.idnum }
      ]
      if (comb3.name) {
        arr.push({
          ...form, fileInfo: fileInfoStr,
          combinationName1: comb2.name, combinationPhone1: comb2.phone, combinationIdnum1: comb2.idnum,
          combinationName2: comb3.name, combinationPhone2: comb3.phone, combinationIdnum2: comb3.idnum
        })
      }
      try {
        await addFinanceMulti(arr)
        ElMessage.success('组合贷款申请成功')
        dialog.value = false
        load()
      } catch {
        /* */
      }
    }
  })
}

function statusText(s?: number) {
  if (s === 0) return '待审核'
  if (s === 1) return '已贷款'
  if (s === 2) return '已还款'
  return '-'
}

function statusType(s?: number) {
  if (s === 0) return 'warning'
  if (s === 1) return 'success'
  if (s === 2) return 'info'
  return ''
}

const detail = ref<Finance | null>(null)
async function viewDetail(id: number | string) {
  try {
    const res: any = await selectFinanceById(id)
    detail.value = res.data
    detailDialog.value = true
  } catch {
    /* */
  }
}

function parseFileInfo(s?: string): string[] {
  if (!s) return []
  try {
    return JSON.parse(s)
  } catch {
    return []
  }
}

// 金额千分位：50000 -> ¥ 50,000
function formatMoney(v?: number | string) {
  if (v === null || v === undefined || v === '') return '-'
  const n = Number(v)
  return Number.isNaN(n) ? String(v) : '¥ ' + n.toLocaleString('zh-CN')
}

// 后端返回 ISO 时间（2026-09-29T18:24:02.000+08:00），格式化为 yyyy-MM-dd HH:mm:ss
function formatTime(t?: string) {
  if (!t) return '-'
  return t.replace('T', ' ').replace(/\.\d+.*$/, '')
}

onMounted(async () => {
  await Promise.all([load(), loadProfile()])
})
</script>

<template>
  <div class="page-container">
    <h2 class="section-title">银行与贷款</h2>

    <h3>合作银行</h3>
    <el-row :gutter="16">
      <el-col v-for="b in banks" :key="b.bankId" :xs="24" :sm="12" :md="8">
        <el-card class="bank-card" shadow="hover">
          <h3>{{ b.bankName }}</h3>
          <div class="text-muted">{{ b.introduce }}</div>
          <div class="meta">额度：¥ {{ b.money }} · 利率：{{ b.rate }} · 期限：{{ b.repayment }} 月</div>
          <div class="meta">电话：{{ b.bankPhone }}</div>
          <el-button type="primary" size="small" @click="openLoan(b)">申请贷款</el-button>
        </el-card>
      </el-col>
    </el-row>

    <section class="loan-panel">
      <header class="loan-panel__head">
        <div class="loan-panel__heading">
          <span class="loan-panel__icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor"
                 stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="6" width="18" height="13" rx="2" />
              <path d="M3 10h18" />
              <path d="M8 15h3" />
              <path d="M8 6V5a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v1" />
            </svg>
          </span>
          <div>
            <h3 class="loan-panel__title">我的贷款</h3>
            <p class="loan-panel__sub">查看贷款申请进度、审批状态与还款记录</p>
          </div>
        </div>
        <el-tag type="success" effect="light" round class="loan-panel__count">
          共 {{ myLoans.length }} 笔
        </el-tag>
      </header>

      <el-table :data="myLoans" stripe v-loading="loading" class="loan-table">
        <el-table-column prop="financeId" label="ID" width="80" align="center" />
        <el-table-column label="贷款银行" min-width="130">
          <template #default="{ row }">
            <span class="cell-bank">{{ row.bankName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="姓名" min-width="110">
          <template #default="{ row }">
            <span class="cell-name">{{ row.realName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="金额" min-width="140" align="right" header-align="right">
          <template #default="{ row }">
            <span class="cell-money">{{ formatMoney(row.money) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="利率" width="100" align="center">
          <template #default="{ row }">{{ row.rate ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="期限" width="100" align="center">
          <template #default="{ row }">{{ row.repayment != null ? row.repayment + ' 个月' : '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="light" round>{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" min-width="200">
          <template #default="{ row }">
            <span class="cell-time">{{ formatTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" header-align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" plain size="small" class="detail-btn" @click="viewDetail(row.financeId)">详情</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无贷款记录，快向上方合作银行发起申请吧" :image-size="100" />
        </template>
      </el-table>
    </section>

    <!-- 申请贷款 -->
    <el-dialog v-model="dialog" title="申请贷款" width="720px">
      <el-radio-group v-model="loanType" style="margin-bottom: 12px">
        <el-radio-button label="single">单人贷款</el-radio-button>
        <el-radio-button label="multi">组合贷款</el-radio-button>
      </el-radio-group>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" :disabled="realNameDisabled" :placeholder="realNameDisabled ? '已从个人信息带入' : '请输入真实姓名'" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" maxlength="11" placeholder="请输入 11 位手机号" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idNum">
          <el-input v-model="form.idNum" maxlength="18" placeholder="请输入 18 位身份证号" />
        </el-form-item>
        <el-form-item label="申请金额" prop="money"><el-input-number v-model="form.money" :min="0" /></el-form-item>
        <el-form-item label="利率"><el-input v-model="form.rate" disabled /></el-form-item>
        <el-form-item label="还款期限(月)" prop="repayment"><el-input-number v-model="form.repayment" :min="1" /></el-form-item>

        <el-form-item label="贷款材料">
          <el-upload
            action="#"
            :auto-upload="false"
            :multiple="true"
            accept=".pdf,.doc,.docx"
            name="files"
            :on-change="onFileChange"
            :file-list="pendingFiles"
          >
            <el-button>选择文件 (pdf/doc/docx)</el-button>
          </el-upload>
          <div class="text-muted" style="font-size: 12.5px; margin: 4px 0">仅支持 pdf/doc/docx 格式</div>
          <el-button type="success" size="small" @click="uploadFiles">上传</el-button>
          <div v-if="uploadedUrls.length">
            <div v-for="(u, i) in uploadedUrls" :key="i">
              <a :href="baseURL + u" target="_blank">材料 {{ i + 1 }}</a>
            </div>
          </div>
        </el-form-item>

        <template v-if="loanType === 'multi'">
          <el-divider>联合贷款人 2</el-divider>
          <el-form-item label="姓名"><el-input v-model="comb2.name" /></el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="comb2.phone" maxlength="11" placeholder="请输入 11 位手机号" />
          </el-form-item>
          <el-form-item label="身份证号">
            <el-input v-model="comb2.idnum" maxlength="18" placeholder="请输入 18 位身份证号" />
          </el-form-item>

          <el-divider>联合贷款人 3（可选）</el-divider>
          <el-form-item label="姓名"><el-input v-model="comb3.name" /></el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="comb3.phone" maxlength="11" placeholder="选填，填写则须为正确手机号" />
          </el-form-item>
          <el-form-item label="身份证号">
            <el-input v-model="comb3.idnum" maxlength="18" placeholder="选填，填写则须为正确身份证号" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 贷款详情 -->
    <el-dialog v-model="detailDialog" title="贷款详情" width="600px">
      <div v-if="detail">
        <p>贷款银行：<span class="detail-bank">{{ detail.bankName || '-' }}</span></p>
        <p>姓名：{{ detail.realName }}</p>
        <p>手机：{{ detail.phone }}</p>
        <p>身份证：{{ detail.idNum }}</p>
        <p>金额：¥ {{ detail.money }} · 利率：{{ detail.rate }} · 期限：{{ detail.repayment }} 月</p>
        <p>状态：{{ statusText(detail.status) }}</p>
        <p v-if="detail.combinationName1">联合贷款人 1：{{ detail.combinationName1 }} / {{ detail.combinationPhone1 }}</p>
        <p v-if="detail.combinationName2">联合贷款人 2：{{ detail.combinationName2 }} / {{ detail.combinationPhone2 }}</p>
        <p>申请时间：{{ detail.createTime }}</p>
        <h4>贷款材料</h4>
        <div v-for="(u, i) in parseFileInfo(detail.fileInfo)" :key="i">
          <a :href="baseURL + u" target="_blank">材料 {{ i + 1 }}</a>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.bank-card {
  margin-bottom: 16px;
}
.bank-card h3 {
  color: #173d2a;
}
.bank-card .meta {
  color: #8a9a90;
  font-size: 13px;
  margin-top: 6px;
}

/* ---------- 我的贷款面板 ---------- */
.loan-panel {
  margin-top: var(--sp-6);
  padding: var(--sp-5) var(--sp-5) var(--sp-4);
  background: #fff;
  border: 1px solid var(--line);
  border-radius: 16px;
  box-shadow: var(--shadow-sm);
}
.loan-panel__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--sp-3);
  margin-bottom: var(--sp-4);
}
.loan-panel__heading {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
}
.loan-panel__icon {
  flex: none;
  width: 38px;
  height: 38px;
  border-radius: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: linear-gradient(135deg, var(--brand-500), var(--brand-700));
  box-shadow: 0 4px 10px rgba(47, 143, 70, 0.28);
}
.loan-panel__title {
  font-size: 16px;
  font-weight: 700;
  line-height: 1.3;
  color: var(--brand-900);
}
.loan-panel__sub {
  margin-top: 2px;
  font-size: var(--fs-meta);
  color: #8a9a90;
}
.loan-panel__count {
  font-weight: 600;
}

/* 表格融入白色面板，去掉全局表格自带外边框 */
.loan-table {
  border: none !important;
}
.loan-table :deep(.el-table__inner-wrapper::before),
.loan-table :deep(.el-table__border-left-patch) {
  display: none;
}

.cell-name {
  font-weight: 600;
  color: var(--brand-900);
}
.cell-bank {
  font-weight: 600;
  color: var(--brand-700);
}
.detail-bank {
  font-weight: 700;
  color: var(--brand-700);
}
.cell-money {
  font-weight: 700;
  color: var(--price);
  font-variant-numeric: tabular-nums;
}
.cell-time {
  color: var(--ink-soft);
  font-variant-numeric: tabular-nums;
}

/* 详情按钮：锁定为浅绿底 + 深绿字，避免全局主按钮渐变底导致文字看不清 */
.loan-panel :deep(.el-button.detail-btn) {
  color: var(--brand-700);
  background: var(--brand-50);
  border: 1px solid var(--brand-200);
  box-shadow: none;
  font-weight: 600;
  padding: 5px 14px;
}
.loan-panel :deep(.el-button.detail-btn:hover),
.loan-panel :deep(.el-button.detail-btn:focus) {
  color: #fff;
  background: linear-gradient(135deg, var(--brand-500), var(--brand-600));
  border-color: transparent;
  transform: none;
}
</style>
