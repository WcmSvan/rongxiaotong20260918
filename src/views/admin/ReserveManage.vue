<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { selectAllReservations, selectReservationByExpert, deleteReservation } from '@/api/admin'
import type { Reserve } from '@/types'

const list = ref<Reserve[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const loading = ref(false)
const expertName = ref('')
const filterMode = ref<'all' | 'expert'>('all')

async function load() {
  loading.value = true
  try {
    const res: any = filterMode.value === 'expert' && expertName.value
      ? await selectReservationByExpert(expertName.value, pageNum.value)
      : await selectAllReservations(pageNum.value)
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

async function remove(r: Reserve) {
  if (!r.id) return
  await ElMessageBox.confirm('确定删除该预约？', '提示', { type: 'warning' })
  try {
    await deleteReservation(r.id)
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
      <h2 class="section-title">预约管理</h2>
      <div>
        <el-radio-group v-model="filterMode" @change="onSearch" style="margin-right: 8px">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="expert">按专家</el-radio-button>
        </el-radio-group>
        <el-input v-if="filterMode === 'expert'" v-model="expertName" placeholder="专家用户名" style="width: 160px; margin-right: 8px" @keyup.enter="onSearch" />
        <el-button type="primary" @click="onSearch">查询</el-button>
      </div>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="questioner" label="提问人" width="110" />
      <el-table-column prop="expertName" label="被提问专家" width="120" />
      <el-table-column prop="area" label="面积" width="90" />
      <el-table-column prop="address" label="地址" show-overflow-tooltip />
      <el-table-column prop="plantName" label="作物" width="90" />
      <el-table-column prop="phone" label="电话" width="120" />
      <el-table-column prop="soilCondition" label="土壤条件" show-overflow-tooltip />
      <el-table-column prop="plantCondition" label="作物条件" show-overflow-tooltip />
      <el-table-column prop="plantDetail" label="种植详情" show-overflow-tooltip />
      <el-table-column prop="message" label="备注" show-overflow-tooltip />
      <el-table-column prop="answer" label="回复" show-overflow-tooltip />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.answer ? 'success' : 'warning'" size="small">
            {{ row.answer ? '已回复' : '待回复' }}
          </el-tag>
        </template>
      </el-table-column>
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
