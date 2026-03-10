import request from '@/utils/request'
export const taskList = () => request.get('/sjc/task/list')
export const updateTaskStatus = (data) => request.post('/sjc/dispatch/task/status', data)
