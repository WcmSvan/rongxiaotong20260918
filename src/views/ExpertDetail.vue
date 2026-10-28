<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { expertinfo, addconsultation, offlinequery } from '@/api/expert'
import { useUserStore } from '@/stores/user'
import type { Expert, QuestionDTO, Reserve } from '@/types'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const detail = ref<Expert | null>(null)
const loading = ref(false)
const isLoggedIn = computed(() => !!userStore.token)

const qDialog = ref(false)
const rDialog = ref(false)
const qFormRef = ref<FormInstance>()
const rFormRef = ref<FormInstance>()

const qForm = reactive<QuestionDTO>({
  title: '',
  question: '',
  plantName: '',
  phone: ''
})

const rForm = reactive<Reserve>({
  area: '',
  address: '',
  plantName: '',
  soilCondition: '',
  plantCondition: '',
  plantDetail: '',
  phone: '',
  message: ''
})

const qRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  question: [{ required: true, message: '请输入问题', trigger: 'blur' }],
  plantName: [{ required: true, message: '请输入作物名称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的 11 位手机号', trigger: 'blur' }
  ]
}

const rRules = {
  area: [{ required: true, message: '请输入面积', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  plantName: [{ required: true, message: '请输入作物名称', trigger: 'blur' }],
  soilCondition: [{ required: true, message: '请输入土壤条件', trigger: 'blur' }],
  plantCondition: [{ required: true, message: '请输入作物条件', trigger: 'blur' }],
  plantDetail: [{ required: true, message: '请输入种植详情', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的 11 位手机号', trigger: 'blur' }
  ]
}

async function load() {
  loading.value = true
  try {
    const res: any = await expertinfo(route.params.name as string)
    detail.value = res.data
  } finally {
    loading.value = false
  }
}

function ensureLogin(): boolean {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return false
  }
  return true
}

async function openQuestion() {
  if (!ensureLogin()) return
  qDialog.value = true
}

async function submitQuestion() {
  if (!detail.value || !qFormRef.value) return
  const expertUserName = detail.value.userName
  await qFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await addconsultation(expertUserName, { ...qForm })
      ElMessage.success('提问成功')
      qDialog.value = false
      Object.assign(qForm, { title: '', question: '', plantName: '', phone: '' })
    } catch {
      /* */
    }
  })
}

async function openReserve() {
  if (!ensureLogin()) return
  rDialog.value = true
}

async function submitReserve() {
  if (!detail.value || !rFormRef.value) return
  const expertUserName = detail.value.userName
  await rFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await offlinequery(expertUserName, { ...rForm })
      ElMessage.success('预约成功')
      rDialog.value = false
      Object.assign(rForm, {
        area: '', address: '', plantName: '', soilCondition: '',
        plantCondition: '', plantDetail: '', phone: '', message: ''
      })
    } catch {
      /* */
    }
  })
}

onMounted(load)
</script>

<template>
  <div class="page-container" v-loading="loading">
    <el-button @click="router.back()">返回</el-button>
    <div v-if="detail" class="detail-wrap">
      <div class="card expert-card">
        <el-avatar :size="120" :src="detail.avatar ? 'http://localhost:8083' + detail.avatar : undefined">
          <span class="avatar-fallback">{{ detail.realName ? detail.realName.slice(-1) : '' }}</span>
        </el-avatar>
        <h2>{{ detail.realName }}</h2>
        <div class="meta-list">
          <div class="meta">用户名：{{ detail.userName }}</div>
          <div class="meta">专业：{{ detail.profession }}</div>
          <div class="meta">职位：{{ detail.position }}</div>
          <div class="meta">单位：{{ detail.belong }}</div>
          <div class="meta">电话：{{ detail.phone }}</div>
        </div>
      </div>
      <div class="card consult-card">
        <h3>向专家咨询</h3>
        <p class="text-muted">如有农技问题，可在线提问或预约线下指导。</p>
        <div class="actions">
          <el-button type="primary" @click="openQuestion">在线提问</el-button>
          <el-button @click="openReserve">线下预约</el-button>
        </div>
      </div>
    </div>

    <!-- 在线提问 -->
    <el-dialog v-model="qDialog" title="在线提问" width="540px">
      <el-form ref="qFormRef" :model="qForm" :rules="qRules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="qForm.title" />
        </el-form-item>
        <el-form-item label="作物名称" prop="plantName">
          <el-input v-model="qForm.plantName" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="qForm.phone" />
        </el-form-item>
        <el-form-item label="问题详情" prop="question">
          <el-input v-model="qForm.question" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="qDialog = false">取消</el-button>
        <el-button type="primary" @click="submitQuestion">提交</el-button>
      </template>
    </el-dialog>

    <!-- 线下预约 -->
    <el-dialog v-model="rDialog" title="线下预约" width="640px">
      <el-form ref="rFormRef" :model="rForm" :rules="rRules" label-width="100px">
        <el-form-item label="面积" prop="area">
          <el-input v-model="rForm.area" />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="rForm.address" />
        </el-form-item>
        <el-form-item label="作物名称" prop="plantName">
          <el-input v-model="rForm.plantName" />
        </el-form-item>
        <el-form-item label="土壤条件" prop="soilCondition">
          <el-input v-model="rForm.soilCondition" />
        </el-form-item>
        <el-form-item label="作物条件" prop="plantCondition">
          <el-input v-model="rForm.plantCondition" />
        </el-form-item>
        <el-form-item label="种植详情" prop="plantDetail">
          <el-input v-model="rForm.plantDetail" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="rForm.phone" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="rForm.message" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReserve">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.detail-wrap {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: var(--sp-5, 20px);
}
.card {
  background: #fff;
  padding: 32px 28px;
  border-radius: 18px;
  text-align: center;
  border: 1px solid #e3ede5;
  box-shadow: 0 8px 24px rgba(23, 61, 42, 0.08);
}
.expert-card h2 {
  color: #173d2a;
  font-size: 22px;
  font-weight: 800;
  margin: var(--sp-3, 12px) 0 var(--sp-4, 16px);
}
.expert-card :deep(.el-avatar) {
  border: 4px solid #d7eddd;
  box-shadow: 0 6px 18px rgba(47, 143, 70, 0.18);
  background: linear-gradient(135deg, #eef6ef, #d7eddd);
}
.avatar-fallback {
  font-size: 48px;
  font-weight: 800;
  color: #2f8f46;
  line-height: 1;
}
.meta-list {
  max-width: 480px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: var(--sp-2, 8px);
}
.card .meta {
  text-align: left;
  color: #51615a;
  font-size: 13.5px;
  line-height: 1.5;
  padding: var(--sp-2, 8px) var(--sp-4, 16px);
  background: #f6faf7;
  border-radius: 10px;
}
.consult-card h3 {
  color: #173d2a;
  font-size: 18px;
  font-weight: 800;
  margin: 0 0 var(--sp-2, 8px);
}
.consult-card .text-muted {
  margin-bottom: var(--sp-4, 16px);
}
.actions {
  display: flex;
  gap: var(--sp-3, 12px);
  justify-content: center;
  flex-wrap: wrap;
}
</style>
