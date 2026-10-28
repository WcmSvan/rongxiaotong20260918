import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Role } from '@/types'

// 用户登录态：token / userName / role / avatar
export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userName = ref<string>(localStorage.getItem('userName') || '')
  const role = ref<Role>((localStorage.getItem('role') as Role) || 'user')
  const avatar = ref<string>(localStorage.getItem('avatar') || '')

  function setLogin(payload: { token: string; userName: string; role: Role }) {
    token.value = payload.token
    userName.value = payload.userName
    role.value = payload.role
    localStorage.setItem('token', payload.token)
    localStorage.setItem('userName', payload.userName)
    localStorage.setItem('role', payload.role)
  }

  function setAvatar(url: string) {
    avatar.value = url
    if (url) localStorage.setItem('avatar', url)
    else localStorage.removeItem('avatar')
  }

  function logout() {
    token.value = ''
    userName.value = ''
    role.value = 'user'
    avatar.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userName')
    localStorage.removeItem('role')
    localStorage.removeItem('avatar')
  }

  return { token, userName, role, avatar, setLogin, setAvatar, logout }
})
