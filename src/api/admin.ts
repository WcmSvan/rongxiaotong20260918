import request from '@/utils/request'
import type { Order, Knowledge, Question, Reserve, Finance, Bank, Result, PageInfo } from '@/types'

// ===== 商品 =====
// 全部商品分页
export function selectAllGoods(params: { pageNum?: number; pageSize?: number }) {
  return request.get<Result<PageInfo<Order>>>('/admin/selectAllGoods', { params })
}

// 搜索商品（管理员后台专用：包含已下架商品）
export function searchGoodsByKeys(params: { keys: string; pageNum?: number }) {
  return request.get<Result<PageInfo<Order>>>('/admin/searchGoodsByKeysForAdmin', { params })
}

// 管理员添加商品
export function adminAddGoods(data: Order) {
  return request.post<Result<void>>('/admin/AddGoods', data)
}

// 管理员修改商品
export function adminUpdateGoods(data: Order) {
  return request.post<Result<void>>('/admin/updateGoods', data)
}

// 删除商品（query id）
export function deleteGoods(params: { id: number | string }) {
  return request.delete<Result<void>>('/admin/deleteGoods', { params })
}

// 下架
export function takeDownGoods(orderId: number | string) {
  return request.post<Result<void>>(`/admin/takeDownGoods/${orderId}`)
}

// 上架
export function takeUpGoods(orderId: number | string) {
  return request.post<Result<void>>(`/admin/takeUpGoods/${orderId}`)
}

// ===== 需求 =====
// 全部需求分页
export function selectAllNeeds(params: { pageNum?: number; pageSize?: number }) {
  return request.get<Result<PageInfo<Order>>>('/admin/selectAllNeeds', { params })
}

// 删除需求
export function deleteNeeds(params: { id: number | string }) {
  return request.delete<Result<void>>('/admin/deleteNeeds', { params })
}

// ===== 知识 =====
// 注意拼写 Knoledge
export function selectAllKnoledge(pageNum: number) {
  return request.get<Result<PageInfo<Knowledge>>>(`/admin/selectAllKnoledge/${pageNum}`)
}

export function searchKnoledgeByKeys(params: { keyword: string; pageNum: number }) {
  return request.get<Result<PageInfo<Knowledge>>>('/admin/searchKnoledgeByKeys', { params })
}

// 添加知识（query 参数）
export function addKnoledge(params: { title: string; content: string; picpath: string }) {
  return request.post<Result<void>>('/admin/addKnoledge', null, { params })
}

// 修改知识（path id + query 参数）
export function updateKnoledges(id: number | string, params: { title: string; content: string; picpath: string }) {
  return request.post<Result<void>>(`/admin/updateKnoledges/${id}`, null, { params })
}

export function deleteKnoledge(id: number | string) {
  return request.delete<Result<void>>(`/admin/DeleteKnoledge/${id}`)
}

// ===== 问题 =====
export function selectAllQuestions(pageNum: number) {
  return request.get<Result<PageInfo<Question>>>(`/admin/selectAllQuestions/${pageNum}`)
}

export function searchQuestionsByKeys(params: { keyword: string; pageNum: number }) {
  return request.get<Result<PageInfo<Question>>>('/admin/searchQuestionsByKeys', { params })
}

export function deleteQuestion(id: number | string) {
  return request.delete<Result<void>>(`/admin/deleteQuestion/${id}`)
}

// ===== 预约 =====
export function selectAllReservations(pageNum: number) {
  return request.get<Result<PageInfo<Reserve>>>(`/admin/selectAllReservations/${pageNum}`)
}

export function selectReservationByExpert(expertName: string, pageNum: number) {
  return request.get<Result<PageInfo<Reserve>>>(`/admin/selectReservationByExpert/${expertName}/${pageNum}`)
}

export function deleteReservation(id: number | string) {
  return request.delete<Result<void>>(`/admin/deleteReservation/${id}`)
}

// ===== 银行/贷款 =====
export function selectAllBanks() {
  return request.get<Result<Bank[]>>('/admin/selectAllBanks')
}

export function selectAllFinances(pageNum: number) {
  return request.get<Result<PageInfo<Finance>>>(`/admin/selectAllFinances/${pageNum}`)
}

export function searchFinanceByRealName(params: { realName: string; pageNum: number }) {
  return request.get<Result<PageInfo<Finance>>>('/admin/searchFinanceByRealName', { params })
}

export function updateFinanceStatus(params: { financeId: number | string; status: number }) {
  return request.put<Result<void>>('/admin/updateFinanceStatus', null, { params })
}

export function deleteFinance(financeId: number | string) {
  return request.delete<Result<void>>(`/admin/deleteFinance/${financeId}`)
}
