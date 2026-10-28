import request from '@/utils/request'
import type { Result } from '@/types'

// 头像上传
export function uploadAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<Result<string>>('/file/uploadAvatar', formData)
}

// 商品图上传
export function uploadOrder(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<Result<string>>('/file/uploadOrder', formData)
}

// 贷款材料多文件上传
export function uploadFinance(files: File[]) {
  const formData = new FormData()
  files.forEach((f) => formData.append('files', f))
  return request.post<Result<string[]>>('/file/uploadFinance', formData)
}
