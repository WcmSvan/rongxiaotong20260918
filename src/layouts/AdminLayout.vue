<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute, RouterView } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeIndex = computed(() => route.path)

function go(path: string) {
  router.push(path)
}

function backHome() {
  router.push('/goods')
}

function logout() {
  userStore.logout()
  localStorage.removeItem('avatar')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<template>
  <div class="admin-layout">
    <el-container style="height: 100vh">
      <el-aside width="220px" class="aside">
        <div class="logo" @click="backHome">🌾 融销通后台</div>
        <el-menu
          :default-active="activeIndex"
          background-color="transparent"
          text-color="#c9ddd0"
          active-text-color="#f4b860"
          @select="go"
        >
          <el-menu-item index="/admin/users">用户管理</el-menu-item>
          <el-menu-item index="/admin/goods">商品管理</el-menu-item>
          <el-menu-item index="/admin/needs">需求管理</el-menu-item>
          <el-menu-item index="/admin/knowledge">知识管理</el-menu-item>
          <el-menu-item index="/admin/questions">问题管理</el-menu-item>
          <el-menu-item index="/admin/reservations">预约管理</el-menu-item>
          <el-menu-item index="/admin/finance">贷款管理</el-menu-item>
          <el-menu-item index="/admin/logs">操作日志</el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="admin-header">
          <span>欢迎，{{ userStore.userName }}</span>
          <div>
            <el-button size="small" @click="backHome">回前台</el-button>
            <el-button type="danger" size="small" @click="logout">退出</el-button>
          </div>
        </el-header>
        <el-main class="admin-main">
          <RouterView />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.aside {
  background: linear-gradient(180deg, #173d2a 0%, #1f5132 100%);
  overflow-x: hidden;
  border-right: 2px solid rgba(244, 184, 96, 0.35);
}
.logo {
  color: #f4b860;
  font-weight: 800;
  padding: 20px 16px;
  font-size: 17px;
  letter-spacing: 1px;
  cursor: pointer;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  text-shadow: 0 2px 6px rgba(0, 0, 0, 0.25);
  transition: background 0.2s;
}
.logo:hover {
  background: rgba(255, 255, 255, 0.06);
}
.aside :deep(.el-menu) {
  border-right: none;
  padding: 10px 10px;
}
.aside :deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  border-radius: 10px;
  margin-bottom: 4px;
  padding-left: 18px !important;
}
.aside :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
  color: #fff !important;
}
.aside :deep(.el-menu-item.is-active) {
  background: rgba(244, 184, 96, 0.16) !important;
  color: #f4b860 !important;
  font-weight: 700;
  box-shadow: inset 3px 0 0 #f4b860;
}
.admin-header {
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e3ede5;
  box-shadow: 0 2px 10px rgba(23, 61, 42, 0.06);
  font-weight: 600;
  color: #173d2a;
}
.admin-main {
  background:
    radial-gradient(1000px 400px at 85% -10%, rgba(108, 191, 130, 0.12), transparent 60%),
    linear-gradient(180deg, #f3f8f2 0%, #ecf4ed 100%);
  padding: 22px;
}
</style>
