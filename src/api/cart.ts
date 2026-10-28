import request from '@/utils/request'
import type { Shoppingcart, Result } from '@/types'

// 加入购物车（query 参数）
export function addCart(params: { orderId: number | string }) {
  return request.post<Result<void>>('/cart/addCart', null, { params })
}

// 删除购物车项
export function deleteCart(id: number | string) {
  return request.delete<Result<void>>(`/cart/deletecart/${id}`)
}

// 修改购物车数量
export function updateCart(id: number | string, count: number) {
  return request.put<Result<void>>(`/cart/updateCart/${id}/${count}`)
}

// 我的购物车
export function showCart() {
  return request.get<Result<Shoppingcart[]>>('/cart/showCart')
}
