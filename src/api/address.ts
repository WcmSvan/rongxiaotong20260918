import request from '@/utils/request'
import type { Address, Result } from '@/types'

// 添加地址
export function addAddress(data: Address) {
  return request.post<Result<void>>('/address/addAddress', data)
}

// 修改地址
export function updateAddress(data: Address) {
  return request.post<Result<void>>('/address/updateAddress', data)
}

// 删除地址（POST + query id）
export function deleteAddress(id: number | string) {
  return request.post<Result<void>>('/address/deleteAddress', null, { params: { id } })
}

// 我的地址列表
export function selectAddress() {
  return request.get<Result<Address[]>>('/address/selectAddress')
}
