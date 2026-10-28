<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { qaManagement, searchQuestionByKeyword, answerquestion, deletequestion } from '@/api/expert'
import type { Question } from '@/types'

const list = ref<Question[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const keyword = ref('')
const loading = ref(false)

const answerDialog = ref(false)
const current = ref<Question | null>(null)
const answerText = ref('')

async function load() {
  loading.value = true
  try {
    const res: any = keyword.value
      ? await searchQuestionByKeyword(pageNum.value, { keyword: keyword.value })
      : await qaManagement(pageNum.value)
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

function openAnswer(q: Question) {
  current.value = q
  answerText.value = q.answer || ''
  answerDialog.value = true
}

async function submitAnswer() {
  if (!current.value) return
  if (!answerText.value.trim()) {
    ElMessage.warning('请输入回答')
    return
  }
  try {
    await answerquestion({ id: current.value.id, answer: answerText.value })
    ElMessage.success('回答成功')
    answerDialog.value = false
    load()
  } catch {
    /* */
  }
}

async function remove(q: Question) {
  await ElMessageBox.confirm('确定删除该问题？', '提示', { type: 'warning' })
  try {
    await deletequestion({ id: q.id })
    ElMessage.success('已删除')
    load()
  } catch {
    /* */
  }
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="flex-between">
      <h2 class="section-title">问答管理</h2>
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="关键词搜索" clearable style="width: 220px" @keyup.enter="onSearch" />
        <el-button type="primary" @click="onSearch">搜索</el-button>
      </div>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="questioner" label="提问者" width="120" />
      <el-table-column prop="plantName" label="作物" width="100" />
      <el-table-column prop="question" label="问题" show-overflow-tooltip />
      <el-table-column label="回复状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.answer ? 'success' : 'warning'">
            {{ row.answer ? '已回复' : '待回复' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="openAnswer(row)">{{ row.answer ? '修改' : '回答' }}</el-button>
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

    <el-dialog v-model="answerDialog" title="回答问题" width="600px">
      <p v-if="current"><strong>问题：</strong>{{ current.question }}</p>
      <el-input v-model="answerText" type="textarea" :rows="5" placeholder="请输入回答" style="margin-top: 12px" />
      <template #footer>
        <el-button @click="answerDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAnswer">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.search-bar {
  display: flex;
  gap: 8px;
}
</style>
