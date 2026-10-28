import request from '@/utils/request'
import type { Bank, Intention, Finance, Result } from '@/types'

// 全部银行
export function selectbank() {
  return request.get<Result<Bank[]>>('/finance/selectbank')
}

// 银行详情（注意 selectFinaceUser 拼写）
export function selectFinaceUser(bankId: number | string) {
  return request.get<Result<Bank>>(`/finance/selectFinaceUser/${bankId}`)
}

// 我的融资意向
export function selectIntentionByName() {
  return request.get<Result<Intention[]>>('/finance/selectIntentionByName')
}

// 新增意向
export function insertIntentionByName(data: Intention) {
  return request.post<Result<void>>('/finance/insertIntentionByName', data)
}

// 修改意向
export function updateIntentionByName(data: Intention) {
  return request.put<Result<void>>('/finance/updateIntentionByName', data)
}

// 删除意向（query id）
export function deleteIntentionById(params: { id: number | string }) {
  return request.delete<Result<void>>('/finance/deleteIntentionById', { params })
}

// 推荐联合贷款人（同城市+同作物）
export function selectRecommendByName() {
  return request.get<Result<Intention[]>>('/finance/selectRecommendByName')
}

// 贷款详情
export function selectFinanceById(financeId: number | string) {
  return request.get<Result<Finance>>(`/finance/selectFinanceById/${financeId}`)
}

// 我的贷款
export function selectMyFinance() {
  return request.get<Result<Finance[]>>('/finance/selectMyFinance')
}

// 单人贷款
export function addFinanceSingle(data: Finance) {
  return request.post<Result<void>>('/finance/addFinanceSingle', data)
}

// 组合贷款（请求体是数组）
export function addFinanceMulti(data: Finance[]) {
  return request.post<Result<void>>('/finance/addFinanceMulti', data)
}
