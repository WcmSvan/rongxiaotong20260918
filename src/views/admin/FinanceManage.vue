<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  selectAllFinances, searchFinanceByRealName, updateFinanceStatus, deleteFinance
} from '@/api/admin'
import type { Finance } from '@/types'

const baseURL = 'http://localhost:8083'
const list = ref<Finance[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const keyword = ref('')
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const res: any = keyword.value
      ? await searchFinanceByRealName({ realName: keyword.value, pageNum: pageNum.value })
      : await selectAllFinances(pageNum.value)
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

function statusText(s?: number) {
  if (s === 0) return '待审核'
  if (s === 1) return '已贷款'
  if (s === 2) return '已还款'
  return '-'
}

function statusType(s?: number) {
  if (s === 0) return 'warning'
  if (s === 1) return 'success'
  if (s === 2) return 'info'
  return ''
}

async function changeStatus(f: Finance, status: number) {
  if (!f.financeId) return
  try {
    await updateFinanceStatus({ financeId: f.financeId, status })
    ElMessage.success('状态已更新')
    load()
  } catch {
    /* */
  }
}

async function remove(f: Finance) {
  if (!f.financeId) return
  await ElMessageBox.confirm('确定删除该贷款记录？', '提示', { type: 'warning' })
  try {
    await deleteFinance(f.financeId)
    ElMessage.success('已删除')
    load()
  } catch {
    /* */
  }
}

function parseFileInfo(s?: string): string[] {
  if (!s) return []
  try {
    return JSON.parse(s)
  } catch {
    return []
  }
}

onMounted(load)
</script>

<template>
    <div class="flex-between" style="margin-bottom: 12px">
      <h2 class="section-title">贷款管理</h2>
      <div>
        <el-input v-model="keyword" placeholder="按真实姓名搜索" clearable style="width: 200px; margin-right: 8px" @keyup.enter="onSearch" />
        <el-button type="primary" @click="onSearch">搜索</el-button>
      </div>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="financeId" label="ID" width="80" />
      <el-table-column prop="bankName" label="贷款银行" width="140">
        <template #default="{ row }">{{ row.bankName || '-' }}</template>
      </el-table-column>
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="phone" label="电话" width="130" />
      <el-table-column prop="idNum" label="身份证" width="180" />
      <el-table-column prop="money" label="金额" width="100" />
      <el-table-column prop="rate" label="利率" width="80" />
      <el-table-column prop="repayment" label="期限" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="材料" width="160">
        <template #default="{ row }">
          <div v-for="(u, i) in parseFileInfo(row.fileInfo)" :key="i">
            <a :href="baseURL + u" target="_blank">材料 {{ i + 1 }}</a>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button size="small" :disabled="row.status === 1" @click="changeStatus(row, 1)">通过</el-button>
          <el-button size="small" type="success" :disabled="row.status === 2" @click="changeStatus(row, 2)">已还款</el-button>
          <el-button size="small" type="warning" :disabled="row.status === 0" @click="changeStatus(row, 0)">重置</el-button>
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
</template>
