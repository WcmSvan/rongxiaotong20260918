<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, type FormInstance, type UploadProps } from 'element-plus'
import { findByUser, updateSelf, updatePassword, fillExpertInfo } from '@/api/user'
import { expertinfo } from '@/api/expert'
import { useUserStore } from '@/stores/user'
import { phonePattern, idCardValidator } from '@/utils/validate'
import type { User } from '@/types'

const userStore = useUserStore()
const baseURL = 'http://localhost:8083'

const profile = ref<User | null>(null)
const loading = ref(false)
const formRef = ref<FormInstance>()
const pwdRef = ref<FormInstance>()

// 专家信息表单（仅 role=expert 可见）
const isExpert = computed(() => userStore.role === 'expert')
const expertFormRef = ref<FormInstance>()
const expertForm = reactive({
  profession: '',
  position: '',
  belong: ''
})

const form = reactive({
  nickName: '',
  phone: '',
  address: '',
  identityNum: '',
  realName: ''
})

// 手机号、身份证号非必填，但填写后必须符合格式
const profileRules = {
  phone: [{ pattern: phonePattern, message: '请输入正确的 11 位手机号', trigger: 'blur' }],
  identityNum: [{ validator: idCardValidator, trigger: 'blur' }]
}

const pwdForm = reactive({
  password: '',
  newPassword: '',
  confirmPassword: ''
})

const pwdRules = {
  password: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_r: any, v: string, cb: any) => {
        if (v !== pwdForm.newPassword) cb(new Error('两次密码不一致'))
        else cb()
      },
      trigger: 'blur'
    }
  ]
}

const avatarUrl = computed(() => {
  if (profile.value?.avatar) return baseURL + profile.value.avatar
  return ''
})

async function load() {
  loading.value = true
  try {
    // LIKE 模糊匹配可能返回多条（admin 会匹配到 admin2/xadmin），必须精确 find 自己
    const res: any = await findByUser({ userName: userStore.userName, pageNum: 1, pageSize: 100 })
    const arr = (res.data?.list || []) as any[]
    profile.value = arr.find(u => u.userName === userStore.userName) || null
    if (profile.value) {
      form.nickName = profile.value.nickName || ''
      form.phone = profile.value.phone || ''
      form.address = profile.value.address || ''
      form.identityNum = profile.value.identityNum || ''
      form.realName = profile.value.realName || ''
      if (profile.value.avatar) userStore.setAvatar(profile.value.avatar)
    }
  } finally {
    loading.value = false
  }
}

// 头像上传成功后调用 updateSelf
const onAvatarSuccess: UploadProps['onSuccess'] = async (res: any) => {
  if (res.flag) {
    try {
      await updateSelf({ avatar: res.data })
      ElMessage.success('头像已更新')
      userStore.setAvatar(res.data)
      load()
    } catch {
      /* */
    }
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isImg = /\.(jpg|jpeg|png|gif|webp)$/i.test(file.name)
  if (!isImg) {
    ElMessage.error('仅支持 jpg/jpeg/png/gif/webp')
    return false
  }
  return true
}

async function saveProfile() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await updateSelf({ ...form })
      ElMessage.success('保存成功')
      load()
    } catch {
      /* */
    }
  })
}

// 加载当前登录专家的已有信息（回填表单）
async function loadExpertInfo() {
  try {
    const res: any = await expertinfo(userStore.userName)
    const data = res.data
    if (data) {
      expertForm.profession = data.profession || ''
      expertForm.position = data.position || ''
      expertForm.belong = data.belong || ''
    }
  } catch {
    /* */
  }
}

// 保存专家信息（仅写 tb_expert）
async function saveExpertInfo() {
  if (!expertFormRef.value) return
  await expertFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await fillExpertInfo({ ...expertForm })
      ElMessage.success('专家信息保存成功')
      loadExpertInfo()
    } catch {
      /* */
    }
  })
}

async function savePwd() {
  if (!pwdRef.value) return
  await pwdRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await updatePassword({ ...pwdForm })
      ElMessage.success('密码修改成功，请重新登录')
      userStore.logout()
      window.location.href = '/login'
    } catch {
      /* */
    }
  })
}

// 页面挂载时拉取当前用户信息；专家额外加载专家资料
onMounted(() => {
  load()
  if (isExpert.value) loadExpertInfo()
})
</script>

<template>
  <div class="page-container" v-loading="loading">
    <h2 class="section-title">个人信息</h2>
    <el-row :gutter="24">
      <el-col :xs="24" :md="8">
        <el-card>
          <div class="avatar-area">
            <el-avatar :size="120" :src="avatarUrl" />
            <el-upload
              action="http://localhost:8083/file/uploadAvatar"
              :headers="{ token: userStore.token }"
              name="file"
              :show-file-list="false"
              :auto-upload="true"
              :before-upload="beforeUpload"
              :on-success="onAvatarSuccess"
            >
              <el-button type="primary" size="small" style="margin-top: 12px">更换头像</el-button>
            </el-upload>
            <div class="meta">
              <div>{{ userStore.userName }}</div>
              <div>角色：{{ userStore.role }}</div>
              <div v-if="profile?.integral !== undefined">积分：{{ profile.integral }}</div>
              <div v-if="profile?.credit !== undefined">信用：{{ profile.credit }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="16">
        <el-card>
          <h3>基本信息</h3>
          <el-form ref="formRef" :model="form" :rules="profileRules" label-width="100px" style="margin-top: 12px">
            <el-form-item label="昵称" prop="nickName"><el-input v-model="form.nickName" /></el-form-item>
            <el-form-item label="真实姓名" prop="realName"><el-input v-model="form.realName" /></el-form-item>
            <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" /></el-form-item>
            <el-form-item label="身份证号" prop="identityNum"><el-input v-model="form.identityNum" /></el-form-item>
            <el-form-item label="地址" prop="address"><el-input v-model="form.address" type="textarea" :rows="2" /></el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card v-if="isExpert" style="margin-top: 16px">
          <h3>专家信息</h3>
          <el-form ref="expertFormRef" :model="expertForm" label-width="100px" style="margin-top: 12px">
            <el-form-item label="职业"><el-input v-model="expertForm.profession" placeholder="如：农学、生物学" /></el-form-item>
            <el-form-item label="职务"><el-input v-model="expertForm.position" placeholder="如：高级工程师" /></el-form-item>
            <el-form-item label="所属单位"><el-input v-model="expertForm.belong" placeholder="如：山东省农科院" /></el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveExpertInfo">保存专家信息</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top: 16px">
          <h3>修改密码</h3>
          <el-form ref="pwdRef" :model="pwdForm" :rules="pwdRules" label-width="100px" style="margin-top: 12px">
            <el-form-item label="原密码" prop="password"><el-input v-model="pwdForm.password" type="password" show-password /></el-form-item>
            <el-form-item label="新密码" prop="newPassword"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword"><el-input v-model="pwdForm.confirmPassword" type="password" show-password /></el-form-item>
            <el-form-item>
              <el-button type="primary" @click="savePwd">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.avatar-area {
  text-align: center;
  padding: 20px 12px 8px;
}
.avatar-area :deep(.el-avatar) {
  border: 4px solid #d7eddd;
  box-shadow: 0 6px 18px rgba(47, 143, 70, 0.18);
  background: linear-gradient(135deg, #eef6ef, #d7eddd);
}
.meta {
  margin-top: 14px;
  color: #51615a;
  font-size: 14px;
  line-height: 1.9;
}
.meta div {
  margin: 4px 0;
}
</style>
