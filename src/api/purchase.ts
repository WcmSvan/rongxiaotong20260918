import request from '@/utils/request'
import type { PurchaseVO, Result } from '@/types'

// 从购物车生成订单
export function commitOrder(addId: number | string) {
  return request.post<Result<void>>(`/purchase/commitOrder/${addId}`)
}

// 我买入的订单
export function buyOrder() {
  return request.get<Result<any[]>>('/purchase/buyOrder')
}

// 我卖出的订单
export function sellOrder() {
  return request.get<Result<any[]>>('/purchase/sellOrder')
}

// 我买入的订单详情（PurchaseVO 列表）
export function buyOrderDetail() {
  return request.post<Result<PurchaseVO[]>>('/purchase/buyOrderDetail')
}
