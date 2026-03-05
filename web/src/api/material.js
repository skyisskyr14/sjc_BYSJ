import request from '@/utils/request'
export const materialPage = (params) => request.get('/sjc/material/page', { params })
export const materialCreate = (data) => request.post('/sjc/material/create', data)
export const materialUpdate = (data) => request.post('/sjc/material/update', data)
export const materialDelete = (id) => request.post('/sjc/material/delete', null, { params: { id } })
