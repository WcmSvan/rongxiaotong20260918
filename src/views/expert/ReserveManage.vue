<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { reservationManagement, searchReservationByKeyword, replyreservation } from '@/api/expert'
import type { Reserve } from '@/types'

const list = ref<Reserve[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const keyword = ref('')
const loading = ref(false)

const replyDialog = ref(false)
const current = ref<Reserve | null>(null)
const replyText = ref('')

async function load() {
  loading.value = true
  try {
    const res: any = keyword.value
      ? await searchReservationByKeyword(pageNum.value, { keyword: keyword.value })
      : await reservationManagement(pageNum.value)
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

function openReply(r: Reserve) {
  current.value = r
  replyText.value = r.answer || ''
  replyDialog.value = true
}

async function submitReply() {
  if (!current.value?.id) return
  if (!replyText.value.trim()) {
    ElMessage.warning('请输入回复')
    return
  }
  try {
    await replyreservation({ id: current.value.id, answer: replyText.value })
    ElMessage.success('回复成功')
    replyDialog.value = false
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
      <h2 class="section-title">预约管理</h2>
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="关键词搜索" clearable style="width: 220px" @keyup.enter="onSearch" />
        <el-button type="primary" @click="onSearch">搜索</el-button>
      </div>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="questioner" label="预约人" width="120" />
      <el-table-column prop="area" label="面积" width="120" />
      <el-table-column prop="address" label="地址" show-overflow-tooltip />
      <el-table-column prop="plantName" label="作物" width="100" />
      <el-table-column prop="phone" label="电话" width="130" />
      <el-table-column prop="message" label="备注" show-overflow-tooltip />
      <el-table-column label="回复状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.answer ? 'success' : 'warning'">
            {{ row.answer ? '已回复' : '待回复' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" @click="openReply(row)">{{ row.answer ? '修改' : '回复' }}</el-button>
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

    <el-dialog v-model="replyDialog" title="回复预约" width="600px">
      <div v-if="current">
        <p><strong>预约人：</strong>{{ current.questioner }}</p>
        <p><strong>联系电话：</strong>{{ current.phone }}</p>
        <p><strong>面积：</strong>{{ current.area }}</p>
        <p><strong>地址：</strong>{{ current.address }}</p>
        <p><strong>作物名称：</strong>{{ current.plantName }}</p>
        <p v-if="current.soilCondition"><strong>土壤条件：</strong>{{ current.soilCondition }}</p>
        <p v-if="current.plantCondition"><strong>作物条件：</strong>{{ current.plantCondition }}</p>
        <p v-if="current.plantDetail"><strong>种植详情：</strong>{{ current.plantDetail }}</p>
        <p v-if="current.message"><strong>备注：</strong>{{ current.message }}</p>
        <p><strong>状态：</strong>{{ current.answer ? '已回复' : '待回复' }}</p>
        <p v-if="current.answer"><strong>原回复：</strong>{{ current.answer }}</p>
      </div>
      <el-input v-model="replyText" type="textarea" :rows="4" placeholder="请输入回复" style="margin-top: 12px" />
      <template #footer>
        <el-button @click="replyDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReply">提交</el-button>
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
