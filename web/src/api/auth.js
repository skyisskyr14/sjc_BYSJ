import request from '@/utils/request'

export const getCaptcha = (type = 'image') => request.get('/auth/captcha', { params: { type } })
export const loginByUser = (payload) => request.post('/auth/user/login', payload)
export const registerBySjc = (payload) => request.post('/sjc/auth/register', payload)
export const getSjcProfile = () => request.get('/sjc/auth/me')
export const checkUserAuth = () => request.get('/auth/user/auth-token')
export const logoutByUser = () => request.get('/auth/user/logout')
