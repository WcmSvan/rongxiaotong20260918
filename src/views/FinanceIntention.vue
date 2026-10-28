<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import {
  selectIntentionByName, insertIntentionByName, updateIntentionByName,
  deleteIntentionById, selectRecommendByName
} from '@/api/finance'
import { findByUser, updateSelf } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { phonePattern } from '@/utils/validate'
import type { Intention } from '@/types'

const userStore = useUserStore()
const list = ref<Intention[]>([])
const recommend = ref<Intention[]>([])
const loading = ref(false)

const dialog = ref(false)
const formRef = ref<FormInstance>()
const isEdit = ref(false)

// 个人信息里已有的真实姓名（用于决定表单 realName 是否可编辑）
const profileRealName = ref('')

const form = reactive<Intention>({
  id: undefined,
  realName: '',
  address: '',
  amount: 0,
  application: '',
  item: '',
  repaymentPeriod: 12,
  area: 0,
  phone: ''
})

// 个人信息已有姓名时禁用真实姓名输入框
const realNameDisabled = computed(() => !!profileRealName.value)

const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  application: [{ required: true, message: '请输入用途', trigger: 'blur' }],
  item: [{ required: true, message: '请输入作物', trigger: 'blur' }],
  repaymentPeriod: [{ required: true, message: '请输入还款期限', trigger: 'blur' }],
  area: [{ required: true, message: '请输入种植面积', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: phonePattern, message: '请输入正确的 11 位手机号', trigger: 'blur' }
  ]
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
    const res: any = await selectIntentionByName()
    list.value = res.data || []
    const r: any = await selectRecommendByName()
    recommend.value = r.data || []
  } finally {
    loading.value = false
  }
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, {
    id: undefined,
    // 有姓名就预填且不可改；没姓名留空让用户填
    realName: profileRealName.value || '',
    address: '', amount: 0,
    application: '', item: '', repaymentPeriod: 12, area: 0, phone: ''
  })
  dialog.value = true
}

function openEdit(i: Intention) {
  isEdit.value = true
  Object.assign(form, { ...i, realName: profileRealName.value || i.realName || '' })
  dialog.value = true
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      // 个人信息原无姓名 → 用户在表单里填了姓名 → 同步到个人信息
      if (!profileRealName.value && form.realName) {
        try {
          await updateSelf({ realName: form.realName })
          profileRealName.value = form.realName
          ElMessage.success('已将姓名同步到个人信息')
        } catch {
          /* 同步失败不阻断意向保存 */
        }
      }
      if (isEdit.value) {
        await updateIntentionByName({ ...form })
      } else {
        await insertIntentionByName({ ...form })
      }
      ElMessage.success('保存成功')
      dialog.value = false
      load()
    } catch {
      /* */
    }
  })
}

async function remove(i: Intention) {
  if (!i.id) return
  await ElMessageBox.confirm('确定删除该意向？', '提示', { type: 'warning' })
  try {
    await deleteIntentionById({ id: i.id })
    ElMessage.success('已删除')
    load()
  } catch {
    /* */
  }
}

onMounted(async () => {
  await Promise.all([load(), loadProfile()])
})
</script>

<template>
  <div class="page-container">
    <div class="flex-between">
      <h2 class="section-title">我的融资意向</h2>
      <el-button type="primary" @click="openAdd">新增意向</el-button>
    </div>

    <div v-loading="loading">
      <el-table :data="list" stripe>
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="item" label="作物" width="100" />
        <el-table-column prop="amount" label="金额(元)" width="120" />
        <el-table-column prop="area" label="面积(亩)" width="100" />
        <el-table-column prop="repaymentPeriod" label="还款期(月)" width="120" />
        <el-table-column prop="application" label="用途" show-overflow-tooltip />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <h3 style="margin-top: 24px">推荐联合贷款人（同城市 + 同作物）</h3>
    <el-table :data="recommend" stripe>
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="item" label="作物" width="100" />
      <el-table-column prop="address" label="地址" show-overflow-tooltip />
      <el-table-column prop="phone" label="电话" width="130" />
    </el-table>

    <el-dialog v-model="dialog" :title="isEdit ? '编辑意向' : '新增意向'" width="640px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" :disabled="realNameDisabled" :placeholder="realNameDisabled ? '已从个人信息带入' : '请输入真实姓名'" />
        </el-form-item>
        <el-form-item label="地址" prop="address"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="作物" prop="item"><el-input v-model="form.item" /></el-form-item>
        <el-form-item label="种植面积(亩)" prop="area"><el-input-number v-model="form.area" :min="0" /></el-form-item>
        <el-form-item label="申请金额(元)" prop="amount"><el-input-number v-model="form.amount" :min="0" /></el-form-item>
        <el-form-item label="还款期限(月)" prop="repaymentPeriod"><el-input-number v-model="form.repaymentPeriod" :min="1" /></el-form-item>
        <el-form-item label="用途" prop="application"><el-input v-model="form.application" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" maxlength="11" placeholder="请输入 11 位手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
