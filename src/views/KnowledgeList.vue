<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { knowledgelist, searchknowledge } from '@/api/expert'
import type { Knowledge } from '@/types'

const router = useRouter()
const list = ref<Knowledge[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const keyword = ref('')
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const res: any =
      keyword.value
        ? await searchknowledge(pageNum.value, { keyword: keyword.value })
        : await knowledgelist(pageNum.value)
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

function goDetail(k: Knowledge) {
  router.push(`/knowledge/${k.knowledgeId}`)
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="flex-between">
      <h2 class="section-title">农业知识</h2>
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="关键词搜索" clearable style="width: 220px" @keyup.enter="onSearch" />
        <el-button type="primary" @click="onSearch">搜索</el-button>
      </div>
    </div>

    <div v-loading="loading">
      <div v-if="!loading && list.length === 0" class="empty-tip">暂无内容</div>
      <el-row :gutter="16">
        <el-col v-for="k in list" :key="k.knowledgeId" :xs="24" :sm="12" :md="8">
          <el-card class="k-card" shadow="hover" @click="goDetail(k)">
            <div class="img-box" v-if="k.picPath">
              <img :src="'http://localhost:8083' + k.picPath" alt="" />
            </div>
            <h3>{{ k.title }}</h3>
            <p class="desc">{{ k.content }}</p>
            <div class="meta">作者：{{ k.ownName }}</div>
            <div class="meta">{{ k.createTime }}</div>
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
.k-card {
  margin-bottom: var(--sp-4, 16px);
  cursor: pointer;
  height: calc(100% - 16px);
}
.k-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.k-card .img-box {
  width: 100%;
  height: 150px;
  overflow: hidden;
  margin-bottom: var(--sp-2, 8px);
  border-radius: 10px;
  background: linear-gradient(135deg, #eef6ef, #e2f0e5);
}
.k-card .img-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.45s ease;
}
.k-card:hover .img-box img {
  transform: scale(1.07);
}
.k-card h3 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.k-card .desc {
  font-size: 12.5px;
  color: #51615a;
  line-height: 1.55;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin: var(--sp-2, 8px) 0;
  flex: 1;
}
.k-card .meta {
  color: #8a9a90;
  font-size: 12.5px;
  margin-top: var(--sp-1, 4px);
  padding-top: var(--sp-1, 4px);
  border-top: 1px dashed #dce8df;
}
.k-card .meta:last-child {
  border-top: none;
  padding-top: 0;
}
</style>
