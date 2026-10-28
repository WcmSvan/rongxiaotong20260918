<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { selectLogPage, clearLogs } from '@/api/log'
import type { OperationLog } from '@/api/log'

// ===== 查询与分页 =====
const loading = ref(false)
const list = ref<OperationLog[]>([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: '',
  module: '',
  status: undefined as number | undefined
})

// 模块下拉选项（与后端 @Log 注解的 module 对应）
const moduleOptions = ['登录认证', '用户管理', '商品管理', '需求管理', '知识管理', '问题管理', '贷款管理']

async function load() {
  loading.value = true
  try {
    const res = await selectLogPage({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      userName: query.userName || undefined,
      module: query.module || undefined,
      status: query.status
    })
    list.value = res.data.list || []
    total.value = Number(res.data.total) || 0
  } catch {
    // 失败时拦截器已统一弹出错误提示
  } finally {
    loading.value = false
  }
}

function search() {
  query.pageNum = 1
  load()
}

function reset() {
  query.userName = ''
  query.module = ''
  query.status = undefined
  search()
}

// ===== 详情弹窗 =====
const detail = ref<OperationLog | null>(null)
const detailVisible = ref(false)

function showDetail(row: OperationLog) {
  detail.value = row
  detailVisible.value = true
}

// ===== 清空日志 =====
async function clearAll() {
  try {
    await ElMessageBox.confirm('确定要清空全部操作日志吗？清空后不可恢复', '警告', {
      type: 'warning',
      confirmButtonText: '清空',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  try {
    await clearLogs()
    ElMessage.success('操作日志已清空')
    load()
  } catch {
    // 失败时拦截器已统一弹出错误提示
  }
}

// 状态展示
function statusText(s: number) {
  return s === 1 ? '成功' : '失败'
}
function statusTag(s: number) {
  return s === 1 ? 'success' : 'danger'
}

// 时间格式化（后端返回 ISO 或数组，统一处理）
function fmtTime(t?: string) {
  if (!t) return '-'
  return String(t).replace('T', ' ').slice(0, 19)
}

onMounted(load)
</script>

<template>
  <div>
    <div class="flex-between" style="margin-bottom: 12px">
      <h2 class="section-title">操作日志</h2>
      <div class="flex" style="display: flex; gap: 8px">
        <el-input
          v-model="query.userName"
          placeholder="按操作人搜索"
          clearable
          style="width: 180px"
          @keyup.enter="search"
        />
        <el-select v-model="query.module" placeholder="业务模块" clearable style="width: 150px">
          <el-option v-for="m in moduleOptions" :key="m" :label="m" :value="m" />
        </el-select>
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 110px">
          <el-option label="成功" :value="1" />
          <el-option label="失败" :value="0" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="danger" @click="clearAll">清空日志</el-button>
      </div>
    </div>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="userName" label="操作人" width="120" show-overflow-tooltip />
      <el-table-column prop="role" label="角色" width="90" />
      <el-table-column prop="module" label="模块" width="100" />
      <el-table-column prop="operation" label="操作" width="150" show-overflow-tooltip />
      <el-table-column prop="httpMethod" label="方式" width="80" />
      <el-table-column prop="url" label="请求地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP" width="130" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="耗时" width="90">
        <template #default="{ row }">{{ row.costTime }} ms</template>
      </el-table-column>
      <el-table-column label="操作时间" width="170">
        <template #default="{ row }">{{ fmtTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="详情" width="80" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showDetail(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pagination-wrap"
      background
      layout="prev, pager, next, total"
      :total="total"
      :page-size="query.pageSize"
      :current-page="query.pageNum"
      @current-change="(p: number) => { query.pageNum = p; load() }"
    />

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="日志详情" width="640px">
      <el-descriptions v-if="detail" :column="1" border>
        <el-descriptions-item label="操作人">{{ detail.userName }}（{{ detail.role }}）</el-descriptions-item>
        <el-descriptions-item label="模块/操作">{{ detail.module }} / {{ detail.operation }}</el-descriptions-item>
        <el-descriptions-item label="请求">{{ detail.httpMethod }} {{ detail.url }}</el-descriptions-item>
        <el-descriptions-item label="IP">{{ detail.ip }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTag(detail.status)">{{ statusText(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="耗时">{{ detail.costTime }} ms</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ fmtTime(detail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="请求参数">
          <pre class="params-pre">{{ detail.params || '无' }}</pre>
        </el-descriptions-item>
        <el-descriptions-item v-if="detail.errorMsg" label="异常信息">
          <pre class="params-pre error">{{ detail.errorMsg }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<style scoped>
.pagination-wrap {
  margin-top: 14px;
  display: flex;
  justify-content: center;
}
.params-pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 12px;
  max-height: 200px;
  overflow: auto;
}
.params-pre.error {
  color: #d03050;
}
</style>
