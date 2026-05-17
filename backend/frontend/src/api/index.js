import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/', // 代理会处理 /api 前缀
  timeout: 100000
})

// 请求拦截器
api.interceptors.request.use(config => {
  // 可以在这里添加 token 等头部信息
  return config
}, error => {
  return Promise.reject(error)
})

// 响应拦截器
api.interceptors.response.use(response => {
  return response.data
}, error => {
  ElMessage.error(error.message || '网络请求失败')
  return Promise.reject(error)
})

export default api
