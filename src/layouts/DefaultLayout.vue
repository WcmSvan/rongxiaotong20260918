<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute, RouterView } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isLoggedIn = computed(() => !!userStore.token)
const isAdmin = computed(() => userStore.role === 'admin')
const isExpert = computed(() => userStore.role === 'expert')

// 头像地址（依赖 store 的响应式 avatar，更新后自动刷新）
const avatarUrl = computed(() => {
  return userStore.avatar ? `http://localhost:8083${userStore.avatar}` : ''
})

function go(path: string) {
  router.push(path)
}

function logout() {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}

// 当前激活菜单
const activeIndex = computed(() => '/' + (route.path.split('/')[1] || 'home'))
</script>

<template>
  <div class="layout">
    <header class="header">
      <div class="header-inner">
        <div class="logo" @click="go('/home')">🌾 融销通</div>
        <el-menu
          :default-active="activeIndex"
          mode="horizontal"
          background-color="transparent"
          text-color="#dcece0"
          active-text-color="#f4b860"
          :ellipsis="false"
          @select="go"
        >
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/goods">商品大厅</el-menu-item>
          <el-menu-item index="/needs">需求大厅</el-menu-item>
          <el-menu-item index="/knowledge">农业知识</el-menu-item>
          <el-menu-item index="/questions">农技问答</el-menu-item>
          <el-menu-item index="/experts">专家</el-menu-item>

          <template v-if="isLoggedIn">
            <el-menu-item index="/cart">购物车</el-menu-item>
            <el-sub-menu index="my">
              <template #title>我的</template>
              <el-menu-item index="/my/publish">我发布的</el-menu-item>
              <el-menu-item index="/my/orders">我的订单</el-menu-item>
              <el-menu-item index="/profile">个人信息</el-menu-item>
              <el-menu-item index="/address">收货地址</el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="finance">
              <template #title>融资</template>
              <el-menu-item index="/finance/intention">融资意向</el-menu-item>
              <el-menu-item index="/finance/loan">贷款申请</el-menu-item>
            </el-sub-menu>
            <el-sub-menu v-if="isExpert" index="expert-manage">
              <template #title>专家工作台</template>
              <el-menu-item index="/expert/manage/qa">问答管理</el-menu-item>
              <el-menu-item index="/expert/manage/reserve">预约管理</el-menu-item>
              <el-menu-item index="/expert/manage/knowledge">知识管理</el-menu-item>
            </el-sub-menu>
            <el-menu-item v-if="isAdmin" index="/admin/users">后台管理</el-menu-item>
          </template>
        </el-menu>

        <div class="user-area">
          <template v-if="isLoggedIn">
            <el-avatar :size="32" :src="avatarUrl" class="avatar" />
            <span class="user-name">{{ userStore.userName }} ({{ userStore.role }})</span>
            <el-button type="danger" size="small" @click="logout">退出</el-button>
          </template>
          <template v-else>
            <el-button type="primary" size="small" @click="go('/login')">登录</el-button>
            <el-button size="small" @click="go('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>
    <main class="main">
      <RouterView />
    </main>
    <footer class="footer">融销通 · 农业供销融资平台 © 2026</footer>
  </div>
</template>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
.header {
  background: linear-gradient(100deg, #173d2a 0%, #1f5132 45%, #2c6e44 100%);
  color: #eaf5ee;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 4px 18px rgba(23, 61, 42, 0.22);
  border-bottom: 2px solid rgba(244, 184, 96, 0.55);
}
.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 18px;
  min-height: 62px;
}
.logo {
  font-size: 21px;
  font-weight: 800;
  letter-spacing: 1px;
  color: #f4b860;
  cursor: pointer;
  white-space: nowrap;
  padding: 4px 12px 4px 0;
  text-shadow: 0 2px 6px rgba(0, 0, 0, 0.25);
  transition: transform 0.2s;
}
.logo:hover {
  transform: scale(1.04);
}
.header :deep(.el-menu) {
  flex: 1;
  border-bottom: none;
  background: transparent !important;
  height: 62px;
}
.header :deep(.el-menu--horizontal > .el-menu-item) {
  height: 62px;
  line-height: 62px;
  font-size: 14.5px;
  border-bottom: 3px solid transparent !important;
}
.header :deep(.el-menu--horizontal > .el-sub-menu .el-sub-menu__title) {
  height: 62px;
  line-height: 62px;
  font-size: 14.5px;
  border-bottom: 3px solid transparent !important;
}
.header :deep(.el-menu-item:hover),
.header :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
  color: #fff !important;
}
.header :deep(.el-menu-item.is-active) {
  color: #f4b860 !important;
  border-bottom-color: #f4b860 !important;
  background: rgba(244, 184, 96, 0.1) !important;
  font-weight: 700;
}
.header :deep(.el-menu--horizontal .el-sub-menu.is-active .el-sub-menu__title) {
  color: #f4b860 !important;
  border-bottom-color: #f4b860 !important;
}
.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.09);
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 999px;
}
.avatar {
  background: #dcece0;
  border: 2px solid rgba(244, 184, 96, 0.7);
}
.user-name {
  color: #eaf5ee;
  font-size: 13px;
}
.main {
  flex: 1;
}
.footer {
  background: linear-gradient(100deg, #173d2a, #1f5132);
  color: #9fbdab;
  text-align: center;
  padding: 16px;
  font-size: 13px;
  letter-spacing: 0.5px;
}
</style>
