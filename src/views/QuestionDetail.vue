<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { selectQuestion } from '@/api/expert'
import type { Question } from '@/types'

const route = useRoute()
const router = useRouter()

const detail = ref<Question | null>(null)
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res: any = await selectQuestion(route.params.id as string)
    detail.value = res.data
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page-container" v-loading="loading">
    <el-button @click="router.back()">返回</el-button>
    <div v-if="detail" class="detail-wrap">
      <h1>{{ detail.title }}</h1>
      <div class="meta">
        提问者：{{ detail.questioner }} · 专家：{{ detail.expertName }}
        <span v-if="detail.plantName"> · 作物：{{ detail.plantName }}</span>
      </div>
      <el-divider />
      <h3>问题描述</h3>
      <p class="content">{{ detail.question }}</p>
      <el-divider />
      <h3>专家回复</h3>
      <div v-if="detail.answer" class="answer">
        <p class="content">{{ detail.answer }}</p>
      </div>
      <el-empty v-else description="专家暂未回复" :image-size="80" />
    </div>
    <el-empty v-else-if="!loading" description="问题不存在或已被删除" />
  </div>
</template>

<style scoped>
.detail-wrap {
  margin-top: var(--sp-4, 16px);
  background: #fff;
  padding: 28px;
  border-radius: 18px;
  border: 1px solid #e3ede5;
  box-shadow: 0 8px 24px rgba(23, 61, 42, 0.08);
}
.detail-wrap h1 {
  color: #173d2a;
  font-size: 22px;
  font-weight: 800;
}
.detail-wrap h3 {
  color: #1f5132;
  font-size: 16px;
}
.meta {
  color: #8a9a90;
  font-size: 12.5px;
  margin: var(--sp-2, 8px) 0 0;
}
.content {
  line-height: 1.85;
  color: #46574e;
  font-size: 14px;
  white-space: pre-wrap;
}
.answer {
  background: linear-gradient(90deg, #eef8f0, #f6fbf7);
  padding: var(--sp-4, 16px) var(--sp-5, 20px);
  border-radius: 12px;
  border-left: 4px solid #43a45c;
}
</style>
