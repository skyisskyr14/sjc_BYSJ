import axios from 'axios'
import { Message } from 'element-ui'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true
})

service.interceptors.response.use((res) => {
  const result = res.data
  if (result && result.code !== 200) {
    Message.error(result.message || '请求失败')
    return Promise.reject(new Error(result.message || 'Error'))
  }
  return result
}, (error) => {
  Message.error(error.message || '网络异常')
  return Promise.reject(error)
})

export default service
