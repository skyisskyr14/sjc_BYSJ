import request from '@/utils/request'
export const warehousePage = (params) => request.get('/sjc/warehouse/page', { params })
export const warehouseCreate = (data) => request.post('/sjc/warehouse/create', data)
export const warehouseUpdate = (data) => request.post('/sjc/warehouse/update', data)
export const warehouseDelete = (id) => request.post('/sjc/warehouse/delete', null, { params: { id } })
