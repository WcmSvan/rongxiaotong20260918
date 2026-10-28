<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { selectAllQuestions, searchQuestionsByKeys, deleteQuestion } from '@/api/admin'
import type { Question } from '@/types'

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
      ? await searchQuestionsByKeys({ keyword: keyword.value, pageNum: pageNum.value })
      : await selectAllQuestions(pageNum.value)
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

async function remove(q: Question) {
  await ElMessageBox.confirm('确定删除该问题？', '提示', { type: 'warning' })
  try {
    await deleteQuestion(q.id)
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
    <div class="flex-between" style="margin-bottom: 12px">
      <h2 class="section-title">问题管理</h2>
      <div>
        <el-input v-model="keyword" placeholder="关键词搜索" clearable style="width: 200px; margin-right: 8px" @keyup.enter="onSearch" />
        <el-button type="primary" @click="onSearch">搜索</el-button>
      </div>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="questioner" label="提问者" width="120" />
      <el-table-column prop="expertName" label="专家" width="120" />
      <el-table-column prop="question" label="问题" show-overflow-tooltip />
      <el-table-column prop="answer" label="回答" show-overflow-tooltip />
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
