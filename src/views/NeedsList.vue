<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { searchAllNeedsByKeys } from '@/api/order'
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
    const res: any = await searchAllNeedsByKeys({
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
      <h2 class="section-title">需求大厅</h2>
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="按内容关键词搜索" clearable style="width: 240px" @keyup.enter="onSearch" />
        <el-button type="primary" @click="onSearch">搜索</el-button>
      </div>
    </div>

    <div v-loading="loading">
      <div v-if="!loading && list.length === 0" class="empty-tip">暂无需求</div>
      <el-row :gutter="16">
        <el-col v-for="item in list" :key="item.orderId" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="need-card" shadow="hover" @click="goDetail(item)">
            <h3>{{ item.title }}</h3>
            <div class="price">¥ {{ item.price }}</div>
            <p class="desc">{{ item.content }}</p>
            <div class="meta">发布人：{{ item.ownName }}</div>
          </el-card>
        </el-col>
      </el-row>
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
.need-card {
  margin-bottom: var(--sp-4, 16px);
  cursor: pointer;
  height: calc(100% - 16px);
  display: flex;
  flex-direction: column;
}
.need-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  gap: var(--sp-1, 4px);
  flex: 1;
}
.need-card h3 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.need-card .price {
  color: #e8622f;
  font-weight: 800;
  font-size: 18px;
  margin: 2px 0;
}
.need-card .desc {
  color: #51615a;
  font-size: 12.5px;
  line-height: 1.55;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}
.need-card .meta {
  color: #8a9a90;
  font-size: 12.5px;
  padding-top: var(--sp-2, 8px);
  margin-top: auto;
  border-top: 1px dashed #dce8df;
}
</style>
