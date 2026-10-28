<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { login, findByUser } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({
  userName: '',
  password: ''
})

const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res: any = await login({ userName: form.userName, password: form.password })
      const data = res.data
      userStore.setLogin({
        token: data.token,
        userName: data.userName,
        role: data.role
      })
      // 登录接口不返回 avatar，主动调一次 findByUser 拉头像填进 store，
      // 让右上角头像在登录后立即显示，无需先进个人信息页
      try {
        const userRes: any = await findByUser({ userName: data.userName, pageNum: 1, pageSize: 100 })
        const user = (userRes.data?.list || []).find((u: any) => u.userName === data.userName)
        if (user?.avatar) userStore.setAvatar(user.avatar)
      } catch {
        /* 拉头像失败不影响登录主流程 */
      }
      ElMessage.success('登录成功')
      const redirect = (route.query.redirect as string) || '/home'
      router.push(redirect)
    } finally {
      loading.value = false
    }
  })
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2 class="title">登录融销通</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" @keyup.enter="submit" />
        </el-form-item>
        <el-button type="primary" :loading="loading" style="width: 100%" @click="submit">登录</el-button>
        <div class="footer-tip">
          还没账号？<router-link to="/register">立即注册</router-link>
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
  padding: 20px;
  overflow: hidden;
  background:
    radial-gradient(700px 360px at 82% 12%, rgba(244, 184, 96, 0.28), transparent 60%),
    radial-gradient(600px 340px at 12% 86%, rgba(108, 191, 130, 0.35), transparent 60%),
    linear-gradient(135deg, #2c6e44 0%, #173d2a 100%);
}
/* 背景田垄纹理与漂浮装饰 */
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
  padding: 32px 32px 26px;
  border-radius: 20px;
  width: 390px;
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
  padding: 4px 12px;
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
