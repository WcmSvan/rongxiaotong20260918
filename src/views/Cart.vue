<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { showCart, deleteCart, updateCart } from '@/api/cart'
import { selectAddress } from '@/api/address'
import { commitOrder } from '@/api/purchase'
import { selectById } from '@/api/order'
import { useUserStore } from '@/stores/user'
import type { Shoppingcart, Order, Address } from '@/types'

const router = useRouter()
const userStore = useUserStore()

interface CartItem extends Shoppingcart {
  order?: Order
  loading?: boolean
}

const cartList = ref<CartItem[]>([])
const addressList = ref<Address[]>([])
const loading = ref(false)

async function loadCart() {
  loading.value = true
  try {
    const res: any = await showCart()
    const arr: Shoppingcart[] = res.data || []
    // 拉取每个购物车项对应的商品详情
    const withDetail: CartItem[] = await Promise.all(
      arr.map(async (c) => {
        try {
          const r: any = await selectById(c.orderId)
          return { ...c, order: r.data }
        } catch {
          return { ...c, order: undefined }
        }
      })
    )
    cartList.value = withDetail
  } finally {
    loading.value = false
  }
}

async function loadAddress() {
  try {
    const res: any = await selectAddress()
    addressList.value = res.data || []
  } catch {
    /* */
  }
}

async function changeCount(item: CartItem, newCount: number) {
  if (newCount < 1) return
  try {
    await updateCart(item.shoppingId, newCount)
    item.count = newCount
    ElMessage.success('已更新')
  } catch {
    /* */
  }
}

async function remove(item: CartItem) {
  await ElMessageBox.confirm('确定从购物车移除该商品？', '提示', { type: 'warning' })
  try {
    await deleteCart(item.shoppingId)
    ElMessage.success('已移除')
    loadCart()
  } catch {
    /* */
  }
}

const totalPrice = computed(() => {
  return cartList.value.reduce((sum, c) => {
    const p = parseFloat(c.order?.price || '0') || 0
    return sum + p * c.count
  }, 0)
})

async function checkout() {
  if (cartList.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  if (addressList.value.length === 0) {
    ElMessage.warning('请先添加收货地址')
    return
  }
  // 选择地址
  let addId = addressList.value[0].id
  if (addressList.value.length > 1) {
    try {
      const { value } = await ElMessageBox.prompt('请输入地址ID（' + addressList.value.map(a => `${a.id}:${a.consignee}-${a.addressDetail}`).join(' / ') + '）', '选择收货地址', {
        inputPattern: /^\d+$/,
        inputErrorMessage: '请输入数字ID'
      })
      addId = Number(value)
    } catch {
      return
    }
  }
  try {
    await commitOrder(addId as number)
    ElMessage.success('下单成功')
    loadCart()
    router.push('/my/orders')
  } catch {
    /* */
  }
}

onMounted(() => {
  loadCart()
  loadAddress()
})
</script>

<template>
  <div class="page-container">
    <div class="flex-between">
      <h2 class="section-title">购物车</h2>
      <el-button type="primary" @click="checkout">下单</el-button>
    </div>
    <div v-loading="loading">
      <div v-if="!loading && cartList.length === 0" class="empty-tip">购物车空空如也</div>
      <el-table v-else :data="cartList" stripe>
        <el-table-column label="商品" min-width="200">
          <template #default="{ row }">
            <div class="goods-cell">
              <img v-if="row.order?.picture" :src="'http://localhost:8083' + row.order.picture" class="thumb" alt="" />
              <span>{{ row.order?.title || '已下架' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }">¥ {{ row.order?.price || '-' }}</template>
        </el-table-column>
        <el-table-column label="数量" width="180">
          <template #default="{ row }">
            <el-input-number v-model="row.count" :min="1" size="small" @change="(v: number) => changeCount(row, v)" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            ¥ {{ ((parseFloat(row.order?.price || '0') || 0) * row.count).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="remove(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div v-if="cartList.length" class="total">总计：¥ {{ totalPrice.toFixed(2) }}</div>
  </div>
</template>

<style scoped>
.goods-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}
.thumb {
  width: 54px;
  height: 54px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid #e3ede5;
  background: #eef6ef;
}
.total {
  text-align: right;
  margin-top: var(--sp-4, 16px);
  font-size: 18px;
  color: #e8622f;
  font-weight: 800;
  padding: var(--sp-3, 12px) var(--sp-4, 16px);
  background: #fff;
  border: 1px solid #e3ede5;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(23, 61, 42, 0.06);
}
</style>
