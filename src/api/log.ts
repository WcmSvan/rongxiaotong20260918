import request from '@/utils/request'
import type { Result, PageInfo } from '@/types'

// 操作日志实体（对应后端 OperationLog）
export interface OperationLog {
  id: number
  userName: string
  role: string
  module: string
  operation: string
  httpMethod: string
  url: string
  params: string
  ip: string
  costTime: number
  status: number
  errorMsg: string
  createTime: string
}

// 分页条件查询操作日志
export function selectLogPage(params: {
  pageNum?: number
  pageSize?: number
  userName?: string
  module?: string
  status?: number
}) {
  return request.get<Result<PageInfo<OperationLog>>>('/admin/log/page', { params })
}

// 清空全部操作日志
export function clearLogs() {
  return request.delete<Result<void>>('/admin/log/clear')
}
