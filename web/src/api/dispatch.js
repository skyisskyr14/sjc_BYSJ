import request from '@/utils/request'
export const taskList = () => request.get('/sjc/task/list')
