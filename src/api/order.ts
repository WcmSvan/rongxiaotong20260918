import request from '@/utils/request'
import type { Order, Result, PageInfo } from '@/types'

// 全部上架商品（裸数组）
export function selectAllGoods() {
  return request.get<Order[]>('/order/All')
}

// 按内容关键词搜商品（裸数组）
export function selectAllcontents(params: { keys: string }) {
  return request.get<Order[]>('/order/selectAllcontents', { params })
}

// 全部需求（裸数组）
export function selectAllneeds() {
  return request.get<Order[]>('/order/selectAllneeds')
}

// 发布商品
export function addGoods(data: Order) {
  return request.post<Result<void>>('/order/addGoods', data)
}

// 修改商品
export function updateGoods(data: Order) {
  return request.post<Result<void>>('/order/updateGoods', data)
}

// 删除商品/需求
export function deleteOrder(id: number | string) {
  return request.delete<Result<void>>(`/order/${id}`)
}

// 商品详情
export function selectById(id: number | string) {
  return request.get<Result<Order>>(`/order/selectById/${id}`)
}

// 搜索商品（分页）
export function searchAllGoodsByKeys(params: { keys: string; pageNum: number }) {
  return request.get<Result<PageInfo<Order>>>('/order/searchAllGoodsByKeys', { params })
}

// 搜索需求（分页）
export function searchAllNeedsByKeys(params: { keys: string; pageNum: number }) {
  return request.get<Result<PageInfo<Order>>>('/order/searchAllNeedsByKeys', { params })
}

// 我发布的求购需求
export function selectMyNeeds() {
  return request.get<Result<Order[]>>('/order/selectMyNeeds')
}

// 发布求购（注意 AddtMyNeeds 路径）
export function addMyNeeds(data: Order) {
  return request.post<Result<void>>('/order/AddtMyNeeds', data)
}
