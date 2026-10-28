<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { selectById, deleteOrder } from '@/api/order'
import { addCart } from '@/api/cart'
import { useUserStore } from '@/stores/user'
import type { Order } from '@/types'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const detail = ref<Order | null>(null)
const loading = ref(false)

const isLoggedIn = computed(() => !!userStore.token)
const isOwner = computed(
  () => !!detail.value && detail.value.ownName === userStore.userName
)

async function load() {
  loading.value = true
  try {
    const res: any = await selectById(route.params.id as string)
    detail.value = res.data
  } finally {
    loading.value = false
  }
}

async function addToCart() {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  if (!detail.value?.orderId) return
  try {
    await addCart({ orderId: detail.value.orderId })
    ElMessage.success('已加入购物车')
  } catch {
    /* 拦截器已提示 */
  }
}

async function onDelete() {
  if (!detail.value?.orderId) return
  await ElMessageBox.confirm('确定删除该商品/需求吗？', '提示', { type: 'warning' })
  try {
    await deleteOrder(detail.value.orderId)
    ElMessage.success('删除成功')
    router.back()
  } catch {
    /* */
  }
}

function onEdit() {
  // 跳到我发布的页面，在那里弹出编辑
  ElMessage.info('请在"我发布的"页面进行编辑')
  router.push('/my/publish')
}

onMounted(load)
</script>

<template>
  <div class="page-container" v-loading="loading">
    <el-button @click="router.back()">返回</el-button>
    <div v-if="detail" class="detail-wrap">
      <el-row :gutter="24">
        <el-col :xs="24" :md="10">
          <div class="img-box">
            <img v-if="detail.picture" :src="'http://localhost:8083' + detail.picture" alt="" />
            <span v-else>无图片</span>
          </div>
        </el-col>
        <el-col :xs="24" :md="14">
          <h2>{{ detail.title }}</h2>
          <div class="price">¥ {{ detail.price }}</div>
          <el-divider />
          <p class="desc">{{ detail.content }}</p>
          <div class="meta">类型：{{ detail.type === 'needs' ? '求购需求' : '供应商品' }}</div>
          <div class="meta">发布人：{{ detail.ownName }}</div>
          <div class="meta" v-if="detail.address">地址：{{ detail.address }}</div>
          <div class="meta">发布时间：{{ detail.createTime }}</div>

          <div class="actions">
            <el-button v-if="detail.type !== 'needs'" type="primary" @click="addToCart">加入购物车</el-button>
            <template v-if="isOwner">
              <el-button @click="onEdit">编辑</el-button>
              <el-button type="danger" @click="onDelete">删除</el-button>
            </template>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<style scoped>
.detail-wrap {
  margin-top: var(--sp-4, 16px);
  background: #fff;
  padding: 26px;
  border-radius: 18px;
  border: 1px solid #e3ede5;
  box-shadow: 0 8px 24px rgba(23, 61, 42, 0.08);
}
.detail-wrap h2 {
  color: #173d2a;
  font-size: 22px;
  font-weight: 800;
  line-height: 1.4;
}
.img-box {
  width: 100%;
  height: 300px;
  background: linear-gradient(135deg, #eef6ef, #e2f0e5);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  overflow: hidden;
}
.img-box img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.price {
  font-size: 26px;
  color: #e8622f;
  font-weight: 800;
  margin: var(--sp-3, 12px) 0;
}
.desc {
  color: #46574e;
  font-size: 14px;
  line-height: 1.8;
  margin-bottom: var(--sp-4, 16px);
}
.meta {
  color: #8a9a90;
  font-size: 12.5px;
  margin-top: var(--sp-1, 4px);
}
.actions {
  margin-top: var(--sp-6, 24px);
  display: flex;
  gap: var(--sp-3, 12px);
  flex-wrap: wrap;
}
.actions .el-button {
  padding: 0 26px;
  height: 40px;
}
</style>
