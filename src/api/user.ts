import request from '@/utils/request'
import type { Result, PageInfo, User, UserDTO, LoginData } from '@/types'

// 检查账号/手机是否已注册
export function checkIsRegistered(params: { userName?: string; phone?: string }) {
  return request.get<Result<boolean>>('/user/checkIsRegistered', { params })
}

// 注册
export function register(data: UserDTO) {
  return request.post<Result<void>>('/user/register', data)
}

// 登录
export function login(data: { userName: string; password: string }) {
  return request.post<Result<LoginData>>('/user/login', data)
}

// 添加用户（管理员）
export function addUser(data: UserDTO) {
  return request.post<Result<void>>('/user/addUser', data)
}

// 删除用户（注意是 GET）
export function deleteUser(params: { userName: string; confirm?: boolean }) {
  return request.get<Result<string>>('/user/deleteUser', { params })
}

// 管理员改任意用户
export function updateUser(data: UserDTO) {
  return request.post<Result<void>>('/user/updateUser', data)
}

// 改自己
export function updateSelf(data: UserDTO) {
  return request.post<Result<void>>('/user/updateSelf', data)
}

// 专家填写专家信息（仅专家可用，写 tb_expert 的 profession/position/belong）
export function fillExpertInfo(data: { profession?: string; position?: string; belong?: string }) {
  return request.post<Result<void>>('/user/fillExpertInfo', data)
}

// 按 userName/nickName 分页查询
export function findByUser(params: { userName?: string; nickName?: string; pageNum?: number; pageSize?: number }) {
  return request.get<Result<PageInfo<User>>>('/user/findByUser', { params })
}

// 全部用户分页
export function findAllUser(params: { pageNum?: number; pageSize?: number }) {
  return request.get<Result<PageInfo<User>>>('/user/findAllUser', { params })
}

// 按角色分页
export function findByRole(params: { role: string; pageNum?: number; pageSize?: number }) {
  return request.get<Result<PageInfo<User>>>('/user/findByRole', { params })
}

// 修改密码
export function updatePassword(data: { password: string; newPassword: string; confirmPassword: string }) {
  return request.post<Result<void>>('/user/updatePassword', data)
}
