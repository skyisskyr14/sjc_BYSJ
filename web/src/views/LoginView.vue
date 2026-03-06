<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <h2>用户登录</h2>
      <el-form :model="form" label-width="90px" @submit.native.prevent>
        <el-form-item label="用户名">
          <el-input v-model.trim="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="大角色ID">
          <el-input-number v-model="form.role" :min="1" :step="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="项目ID">
          <el-input-number v-model="form.type" :min="1" :step="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="验证码">
          <div class="captcha-row">
            <el-input v-model.trim="form.captchaInput" placeholder="输入验证码" />
            <img v-if="captcha.data" :src="`data:image/png;base64,${captcha.data}`" class="captcha-img" @click="loadCaptcha" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="doLogin">登录</el-button>
          <el-button type="text" @click="$router.push('/register')">去注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getCaptcha, loginByUser } from '@/api/auth'

export default {
  data: () => ({
    loading: false,
    captcha: { uuid: '', data: '' },
    form: { username: '', password: '', captchaInput: '', captchaType: 'image', type: 1, role: 1 }
  }),
  async mounted() {
    await this.loadCaptcha()
  },
  methods: {
    async loadCaptcha() {
      const res = await getCaptcha('image')
      this.captcha = res.data || { uuid: '', data: '' }
    },
    async doLogin() {
      if (!this.form.username || !this.form.password || !this.form.captchaInput) return this.$message.error('请填写完整信息')
      this.loading = true
      try {
        await loginByUser({
          username: this.form.username,
          password: this.form.password,
          captchaUuid: this.captcha.uuid,
          captchaInput: this.form.captchaInput,
          captchaType: this.form.captchaType,
          type: this.form.type,
          role: this.form.role
        })
        this.$message.success('登录成功')
        this.$router.replace('/dashboard')
      } finally {
        this.loading = false
        this.form.captchaInput = ''
        await this.loadCaptcha()
      }
    }
  }
}
</script>

<style scoped>
.auth-page{min-height:100vh;display:flex;align-items:center;justify-content:center;background:#f2f6fc}
.auth-card{width:460px}
.captcha-row{display:flex;gap:8px;align-items:center}
.captcha-img{width:130px;height:40px;border:1px solid #dcdfe6;cursor:pointer}
</style>
