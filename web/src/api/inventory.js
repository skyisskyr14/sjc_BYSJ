import request from '@/utils/request'
export const inventoryPage = (params) => request.get('/sjc/inventory/page', { params })
export const flowPage = (params) => request.get('/sjc/inventory/flow/page', { params })
export const inbound = (data) => request.post('/sjc/inventory/inbound', data)
export const outbound = (data) => request.post('/sjc/inventory/outbound', data)
