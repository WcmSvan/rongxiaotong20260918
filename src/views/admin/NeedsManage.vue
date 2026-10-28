<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { selectAllNeeds, deleteNeeds } from '@/api/admin'
import type { Order } from '@/types'

const baseURL = 'http://localhost:8083'
const list = ref<Order[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const res: any = await selectAllNeeds({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  pageNum.value = p
  load()
}

async function remove(o: Order) {
  if (!o.orderId) return
  await ElMessageBox.confirm('确定删除该需求？', '提示', { type: 'warning' })
  try {
    await deleteNeeds({ id: o.orderId })
    ElMessage.success('已删除')
    load()
  } catch {
    /* */
  }
}

onMounted(load)
</script>

<template>
  <div>
    <h2 class="section-title">需求管理</h2>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="price" label="价格" width="120" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column prop="ownName" label="发布人" width="120" />
      <el-table-column prop="createTime" label="发布时间" width="180" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
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
