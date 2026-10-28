<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { register, checkIsRegistered } from '@/api/user'
import type { UserDTO } from '@/types'

const router = useRouter()
const loading = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<UserDTO>({
  userName: '',
  password: '',
  confirmPassword: '',
  nickName: '',
  phone: ''
})

// 用户名同步检查
async function checkUserName(_rule: any, value: string, callback: any) {
  if (!value) return callback()
  try {
    const res: any = await checkIsRegistered({ userName: value })
    if (res.data === true) {
      callback(new Error('用户名已被注册'))
    } else {
      callback()
    }
  } catch {
    callback()
  }
}

async function checkPhone(_rule: any, value: string, callback: any) {
  if (!value) return callback()
  if (!/^1[3-9]\d{9}$/.test(value)) {
    return callback(new Error('手机号格式不正确'))
  }
  try {
    const res: any = await checkIsRegistered({ phone: value })
    if (res.data === true) {
      callback(new Error('手机号已被注册'))
    } else {
      callback()
    }
  } catch {
    callback()
  }
}

const rules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { validator: checkUserName, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: any) => {
        if (value !== form.password) callback(new Error('两次密码不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ],
  nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { validator: checkPhone, trigger: 'blur' }
  ]
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await register({
        userName: form.userName,
        password: form.password,
        confirmPassword: form.confirmPassword,
        nickName: form.nickName,
        phone: form.phone
      })
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } finally {
      loading.value = false
    }
  })
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2 class="title">注册融销通</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="登录用户名" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model="form.nickName" placeholder="显示昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="11 位手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="至少 6 位" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="再次输入密码" />
        </el-form-item>
        <el-button type="primary" :loading="loading" style="width: 100%" @click="submit">注册</el-button>
        <div class="footer-tip">
          已有账号？<router-link to="/login">去登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(700px 360px at 82% 12%, rgba(244, 184, 96, 0.28), transparent 60%),
    radial-gradient(600px 340px at 12% 86%, rgba(108, 191, 130, 0.35), transparent 60%),
    linear-gradient(135deg, #2c6e44 0%, #173d2a 100%);
  padding: 28px 20px;
  overflow: hidden;
}
.auth-page::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: repeating-linear-gradient(
    115deg,
    rgba(255, 255, 255, 0.04) 0 2px,
    transparent 2px 46px
  );
  pointer-events: none;
}
.auth-page::after {
  content: '🌱🌾🌽';
  position: absolute;
  bottom: 18px;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 26px;
  letter-spacing: 42px;
  opacity: 0.35;
  pointer-events: none;
}
.auth-card {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.98);
  padding: 30px 32px 26px;
  border-radius: 20px;
  width: 430px;
  box-shadow: 0 24px 60px rgba(10, 35, 20, 0.35);
  animation: card-in 0.5s ease both;
}
.auth-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 5px;
  background: linear-gradient(90deg, #43a45c, #f4b860);
}
@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(18px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
.title {
  text-align: center;
  margin-bottom: 22px;
  font-size: 22px;
  font-weight: 800;
  color: #173d2a;
  letter-spacing: 1px;
}
.title::before {
  content: '🌾 ';
  font-size: 22px;
}
.title::after {
  content: '';
  display: block;
  width: 48px;
  height: 3px;
  margin: 10px auto 0;
  border-radius: 3px;
  background: linear-gradient(90deg, #43a45c, #f4b860);
}
.auth-card :deep(.el-input__wrapper) {
  padding: 3px 12px;
}
.auth-card :deep(.el-form-item) {
  margin-bottom: var(--sp-4, 16px);
}
.auth-card :deep(.el-button) {
  height: 42px;
  font-size: 15px;
  letter-spacing: 6px;
  text-indent: 6px;
  margin-top: var(--sp-1, 4px);
}
.footer-tip {
  text-align: center;
  margin-top: var(--sp-4, 16px);
  font-size: 13px;
  color: #51615a;
}
.footer-tip :deep(a) {
  color: #2f8f46;
  font-weight: 600;
}
.footer-tip :deep(a:hover) {
  color: #f4b860;
}
</style>
