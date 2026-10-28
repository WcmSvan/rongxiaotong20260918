<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { expertlist } from '@/api/expert'
import type { Expert } from '@/types'

const router = useRouter()
const list = ref<Expert[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const res: any = await expertlist(pageNum.value)
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

function goDetail(e: Expert) {
  router.push(`/experts/${e.userName}`)
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <h2 class="section-title">农业专家</h2>
    <div v-loading="loading">
      <div v-if="!loading && list.length === 0" class="empty-tip">暂无专家</div>
      <el-row :gutter="16">
        <el-col v-for="e in list" :key="e.userName" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="e-card" shadow="hover" @click="goDetail(e)">
            <div class="avatar">
              <el-avatar :size="64" :src="e.avatar ? 'http://localhost:8083' + e.avatar : undefined">
                <span class="avatar-fallback">{{ e.realName ? e.realName.slice(-1) : '' }}</span>
              </el-avatar>
            </div>
            <h3>{{ e.realName }}</h3>
            <div class="meta">{{ e.profession }}</div>
            <div class="meta">{{ e.position }}</div>
            <div class="meta">所属：{{ e.belong }}</div>
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
.e-card {
  text-align: center;
  margin-bottom: var(--sp-4, 16px);
  cursor: pointer;
  height: calc(100% - 16px);
}
.e-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.avatar {
  display: flex;
  justify-content: center;
  margin: 6px 0 10px;
}
.avatar :deep(.el-avatar) {
  border: 3px solid #d7eddd;
  box-shadow: 0 4px 12px rgba(47, 143, 70, 0.15);
  background: linear-gradient(135deg, #eef6ef, #d7eddd);
}
.avatar-fallback {
  font-size: 26px;
  font-weight: 800;
  color: #2f8f46;
  line-height: 1;
}
.e-card:hover .avatar :deep(.el-avatar) {
  border-color: #f4b860;
}
.e-card h3 {
  color: #173d2a;
  font-size: 17px;
}
.meta {
  color: #8a9a90;
  font-size: 13px;
  margin-top: 5px;
}
.meta:first-of-type {
  color: #2f8f46;
  font-weight: 600;
}
</style>
