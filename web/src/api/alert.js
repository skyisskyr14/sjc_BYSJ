import request from '@/utils/request'
export const alertPage = (params) => request.get('/sjc/alert/page', { params })
export const alertAck = (data) => request.post('/sjc/alert/ack', data)
