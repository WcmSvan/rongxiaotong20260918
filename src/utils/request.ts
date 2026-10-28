import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// 后端地址
const baseURL = 'http://localhost:8083'

const service = axios.create({
  baseURL,
  timeout: 30000
})

// 请求拦截器：自动注入 token
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['token'] = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：兼容裸数组、统一处理 Result、401 跳登录
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 兼容裸数组接口
    if (Array.isArray(res)) {
      return res
    }
    // Result 结构
    if (res && typeof res === 'object' && 'flag' in res) {
      if (res.flag === true) {
        return res
      } else {
        ElMessage.error(res.message || '操作失败')
        return Promise.reject(new Error(res.message || '操作失败'))
      }
    }
    // 其它情况直接返回
    return res
  },
  (error) => {
    const status = error.response?.status
    const respData = error.response?.data
    let msg = ''
    if (typeof respData === 'object' && respData) {
      msg = respData.message || respData.error || ''
    }
    if (status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      ElMessage.error('登录已失效，请重新登录')
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
    } else if (status === 400 || status === 500) {
      ElMessage.error(msg || '请求失败')
    } else if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('网络异常')
    }
    return Promise.reject(error)
  }
)

export default service
