<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { questionlist, searchquestion } from '@/api/expert'
import type { Question } from '@/types'

const router = useRouter()
const list = ref<Question[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const keyword = ref('')
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const res: any = keyword.value
      ? await searchquestion(pageNum.value, { keyword: keyword.value })
      : await questionlist(pageNum.value)
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

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="flex-between">
      <h2 class="section-title">农技问答</h2>
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="关键词搜索" clearable style="width: 220px" @keyup.enter="onSearch" />
        <el-button type="primary" @click="onSearch">搜索</el-button>
      </div>
    </div>

    <div v-loading="loading">
      <div v-if="!loading && list.length === 0" class="empty-tip">暂无问答</div>
      <el-card
        v-for="q in list"
        :key="q.id"
        class="q-item"
        shadow="hover"
        style="cursor: pointer"
        @click="router.push(`/questions/${q.id}`)"
      >
        <h3>{{ q.title }}</h3>
        <div class="meta">
          提问者：{{ q.questioner }} · 专家：{{ q.expertName }}
          <span v-if="q.plantName"> · 作物：{{ q.plantName }}</span>
        </div>
        <p class="q-content">{{ q.question }}</p>
        <div v-if="q.answer" class="answer">
          <strong>专家回复：</strong>{{ q.answer }}
        </div>
        <div v-else class="answer pending">待回复</div>
      </el-card>
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
.q-item {
  margin-bottom: var(--sp-3, 12px);
}
.q-item h3 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.meta {
  color: #8a9a90;
  font-size: 12.5px;
  margin: var(--sp-2, 8px) 0;
}
.q-content {
  margin: var(--sp-2, 8px) 0 10px;
  color: #46574e;
  font-size: 13.5px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.answer {
  background: linear-gradient(90deg, #eef8f0, #f6fbf7);
  padding: var(--sp-2, 8px) var(--sp-3, 12px);
  border-radius: 8px;
  border-left: 3px solid #43a45c;
  font-size: 13px;
  line-height: 1.6;
  color: #2c4a36;
}
.answer strong {
  color: #2f8f46;
}
.answer.pending {
  color: #b9791f;
  background: linear-gradient(90deg, #fdf4e3, #fef9f1);
  border-left-color: #e9a13b;
  font-weight: 600;
}
</style>
