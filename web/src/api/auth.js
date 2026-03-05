import request from '@/utils/request'
export const authPing = () => request.get('/sjc/dashboard/metrics')
