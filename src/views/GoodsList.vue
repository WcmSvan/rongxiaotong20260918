<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { searchAllGoodsByKeys } from '@/api/order'
import type { Order } from '@/types'

const router = useRouter()
const list = ref<Order[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const keyword = ref('')
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const res: any = await searchAllGoodsByKeys({
      keys: keyword.value || '',
      pageNum: pageNum.value
    })
    list.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function onSearch() {
  pageNum.value = 1
  load()
}

function onPageChange(p: number) {
  pageNum.value = p
  load()
}

function goDetail(o: Order) {
  router.push(`/goods/${o.orderId}`)
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="flex-between">
      <h2 class="section-title">商品大厅</h2>
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="按内容关键词搜索" clearable style="width: 240px" @keyup.enter="onSearch" />
        <el-button type="primary" @click="onSearch">搜索</el-button>
      </div>
    </div>

    <div v-loading="loading">
      <div v-if="!loading && list.length === 0" class="empty-tip">暂无商品</div>
      <div class="card-list">
        <div v-for="item in list" :key="item.orderId" class="goods-card" @click="goDetail(item)">
          <div class="img-box">
            <img v-if="item.picture" :src="'http://localhost:8083' + item.picture" alt="" />
            <span v-else>无图</span>
          </div>
          <div class="info">
            <div class="title">{{ item.title }}</div>
            <div class="price">¥ {{ item.price }}</div>
            <div class="desc">{{ item.content }}</div>
            <div class="desc">发布人：{{ item.ownName }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-wrap">
      <el-pagination
        background
        layout="prev, pager, next, total"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        @current-change="onPageChange"
      />
    </div>
  </div>
</template>

<style scoped>
.search-bar {
  display: flex;
  gap: 8px;
}
</style>
