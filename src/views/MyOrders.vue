<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { buyOrder, sellOrder, buyOrderDetail } from '@/api/purchase'
import type { PurchaseVO } from '@/types'

const activeTab = ref('buy')
const buyList = ref<any[]>([])
const sellList = ref<any[]>([])
const buyDetailList = ref<PurchaseVO[]>([])
const loading = ref(false)
// 当前选中的订单号；点击订单行后，下方明细只显示该订单的明细
const selectedPurchaseId = ref<number | null>(null)

// 按选中订单号过滤明细
const currentDetails = computed(() =>
  selectedPurchaseId.value === null
    ? []
    : buyDetailList.value.filter(d => d.purchaseId === selectedPurchaseId.value)
)

// 点击订单行：选中该订单，下方联动显示其明细
function selectOrder(row: any) {
  selectedPurchaseId.value = row.purchaseId
}

async function loadBuy() {
  const res: any = await buyOrder()
  buyList.value = res.data || []
  // 拉取详情
  try {
    const r: any = await buyOrderDetail()
    buyDetailList.value = r.data || []
  } catch {
    buyDetailList.value = []
  }
  // 默认选中第一个订单，进入页面即展示其明细
  selectedPurchaseId.value = buyList.value.length ? buyList.value[0].purchaseId : null
}

async function loadSell() {
  const res: any = await sellOrder()
  sellList.value = res.data || []
}

async function load() {
  loading.value = true
  try {
    await Promise.all([loadBuy(), loadSell()])
  } finally {
    loading.value = false
  }
}

function statusText(s: number) {
  return ['待付款', '已付款', '已发货', '已完成', '已取消'][s] || `状态${s}`
}

// 后端返回 ISO 时间（2026-10-03T19:27:20.000+08:00），格式化为 yyyy-MM-dd HH:mm:ss
function formatTime(t?: string) {
  if (!t) return ''
  return t.replace('T', ' ').replace(/\.\d+.*$/, '')
}

// 商品图片补全后端地址
function imgUrl(picture?: string) {
  return picture ? `http://localhost:8083${picture}` : ''
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <h2 class="section-title">我的订单</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="我买入的" name="buy">
        <div v-loading="loading">
          <h3>订单列表<span class="tip">（点击任意订单，下方查看该订单明细）</span></h3>
          <el-table :data="buyList" stripe highlight-current-row class="clickable-rows" @row-click="selectOrder">
            <el-table-column prop="purchaseId" label="订单号" width="110" />
            <el-table-column prop="totalPrice" label="总价" min-width="180" />
            <el-table-column label="状态" width="110">
              <template #default="{ row }">{{ statusText(row.purchaseStatus) }}</template>
            </el-table-column>
            <el-table-column label="下单时间" min-width="240">
              <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
            </el-table-column>
          </el-table>
          <h3 style="margin-top: 20px">
            订单明细
            <span v-if="selectedPurchaseId !== null" class="tip">订单号：{{ selectedPurchaseId }}（共 {{ currentDetails.length }} 件商品）</span>
          </h3>
          <el-table :data="currentDetails" stripe>
            <el-table-column label="图片" width="90">
              <template #default="{ row }">
                <img v-if="row.picture" :src="imgUrl(row.picture)" class="detail-thumb" alt="商品图片" />
                <span v-else>无图</span>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="商品" width="110" />
            <el-table-column prop="content" label="商品描述" min-width="200" show-overflow-tooltip />
            <el-table-column prop="uninPrice" label="单价" width="90" />
            <el-table-column prop="count" label="数量" width="70" />
            <el-table-column prop="sumPrice" label="小计" width="90" />
            <el-table-column prop="purchaseId" label="订单号" width="80" />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">{{ statusText(row.purchaseStatus) }}</template>
            </el-table-column>
            <el-table-column label="下单时间" width="170">
              <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
            </el-table-column>
            <el-table-column prop="address" label="货源地" min-width="160" show-overflow-tooltip />
          </el-table>
        </div>
      </el-tab-pane>
      <el-tab-pane label="我卖出的" name="sell">
        <el-table :data="sellList" stripe>
          <el-table-column prop="purchaseId" label="订单号" width="100" />
          <el-table-column prop="sumPrice" label="总价" width="120" />
          <el-table-column prop="address" label="地址" show-overflow-tooltip />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">{{ statusText(row.purchaseStatus) }}</template>
          </el-table-column>
          <el-table-column label="下单时间" width="180">
            <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
            </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.detail-thumb {
  width: 56px;
  height: 56px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid #e3ede5;
  background: #eef6ef;
}

/* 订单列表行可点击：手型光标 */
.clickable-rows :deep(.el-table__row) {
  cursor: pointer;
}

.tip {
  font-size: 12.5px;
  font-weight: normal;
  color: #8a9a90;
  margin-left: var(--sp-2, 8px);
}
</style>
