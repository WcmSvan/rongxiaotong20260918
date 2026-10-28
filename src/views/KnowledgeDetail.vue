<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getKnowledge, discusslist, addDiscuss } from '@/api/expert'
import { useUserStore } from '@/stores/user'
import type { Knowledge, Discuss } from '@/types'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const detail = ref<Knowledge | null>(null)
const comments = ref<Discuss[]>([])
const loading = ref(false)
const newComment = ref('')

const isLoggedIn = computed(() => !!userStore.token)

async function loadDetail() {
  const res: any = await getKnowledge(route.params.id as string)
  detail.value = res.data
}

async function loadComments() {
  const res: any = await discusslist()
  const all: Discuss[] = res.data || []
  comments.value = all.filter((c) => String(c.knowledgeId) === String(route.params.id))
}

async function submitComment() {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  try {
    await addDiscuss({ knowledgeId: route.params.id as string, content: newComment.value })
    ElMessage.success('评论成功')
    newComment.value = ''
    loadComments()
  } catch {
    /* */
  }
}

onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([loadDetail(), loadComments()])
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
      <div class="meta">作者：{{ detail.ownName }} · 发布时间：{{ detail.createTime }}</div>
      <div class="img-wrap" v-if="detail.picPath">
        <img :src="'http://localhost:8083' + detail.picPath" alt="" />
      </div>
      <p class="content">{{ detail.content }}</p>

      <el-divider />
      <h3>评论 ({{ comments.length }})</h3>
      <div class="comment-input">
        <el-input v-model="newComment" type="textarea" :rows="2" placeholder="发表评论" />
        <el-button type="primary" @click="submitComment" style="margin-top: 8px">发表</el-button>
      </div>
      <div class="comment-list">
        <div v-for="c in comments" :key="c.discussId" class="comment-item">
          <div class="c-user">{{ c.ownName }}</div>
          <div class="c-content">{{ c.content }}</div>
          <div class="c-time">{{ c.createTime }}</div>
        </div>
        <div v-if="comments.length === 0" class="empty-tip">暂无评论</div>
      </div>
    </div>
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
  line-height: 1.4;
}
.detail-wrap h3 {
  color: #1f5132;
  font-size: 16px;
}
.meta {
  color: #8a9a90;
  font-size: 12.5px;
  margin: var(--sp-2, 8px) 0 var(--sp-4, 16px);
}
.img-wrap {
  max-width: 100%;
  margin: var(--sp-4, 16px) 0;
  border-radius: 12px;
  overflow: hidden;
}
.img-wrap img {
  max-width: 100%;
  border-radius: 12px;
}
.content {
  line-height: 1.85;
  color: #46574e;
  font-size: 14px;
  white-space: pre-wrap;
}
.comment-input {
  margin: var(--sp-3, 12px) 0;
}
.comment-list {
  margin-top: var(--sp-3, 12px);
}
.comment-item {
  border-bottom: 1px solid #eaf3ec;
  padding: var(--sp-3, 12px) var(--sp-4, 16px);
  background: #f8fcf9;
  border-radius: 10px;
  margin-bottom: var(--sp-2, 8px);
}
.c-user {
  font-weight: 700;
  color: #2f8f46;
  font-size: 13.5px;
}
.c-content {
  margin: var(--sp-1, 4px) 0;
  color: #46574e;
  font-size: 13.5px;
  line-height: 1.6;
}
.c-time {
  color: #a6b8ac;
  font-size: 12px;
}
</style>
