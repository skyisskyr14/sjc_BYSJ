import request from '@/utils/request'
export const dashboardMetrics = () => request.get('/sjc/dashboard/metrics')
export const dashboardTrend = () => request.get('/sjc/dashboard/trend')
