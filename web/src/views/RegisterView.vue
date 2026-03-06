<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <h2>用户注册</h2>
      <el-form :model="form" label-width="96px" @submit.native.prevent>
        <el-form-item label="用户名"><el-input v-model.trim="form.username" /></el-form-item>
        <el-form-item label="登录密码"><el-input v-model="form.password" type="password" show-password /></el-form-item>
        <el-form-item label="安全密码"><el-input v-model="form.securePassword" type="password" show-password /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model.trim="form.email" /></el-form-item>
        <el-form-item label="大角色ID"><el-input-number v-model="form.systemRoleId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="项目ID"><el-input-number v-model="form.projectId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="SJC小角色">
          <el-select v-model="form.sjcRoleCode" style="width:100%">
            <el-option label="系统管理员 SYS_ADMIN" value="SYS_ADMIN" />
            <el-option label="调度员 DISPATCHER" value="DISPATCHER" />
            <el-option label="仓库管理员 WAREHOUSE_ADMIN" value="WAREHOUSE_ADMIN" />
            <el-option label="查看者 VIEWER" value="VIEWER" />
          </el-select>
        </el-form-item>
        <el-form-item label="验证码">
          <div class="captcha-row">
            <el-input v-model.trim="form.captchaInput" placeholder="输入验证码" />
            <img v-if="captcha.data" :src="`data:image/png;base64,${captcha.data}`" class="captcha-img" @click="loadCaptcha" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="doRegister">注册</el-button>
          <el-button type="text" @click="$router.push('/login')">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getCaptcha, registerBySjc } from '@/api/auth'

export default {
  data: () => ({
    loading: false,
    captcha: { uuid: '', data: '' },
    form: {
      username: '', password: '', securePassword: '', email: '',
      captchaInput: '', captchaType: 'image',
      systemRoleId: 1, projectId: 1, sjcRoleCode: 'VIEWER'
    }
  }),
  async mounted() {
    await this.loadCaptcha()
  },
  methods: {
    async loadCaptcha() {
      const res = await getCaptcha('image')
      this.captcha = res.data || { uuid: '', data: '' }
    },
    async doRegister() {
      if (!this.form.username || !this.form.password || !this.form.securePassword || !this.form.captchaInput) return this.$message.error('请填写完整信息')
      this.loading = true
      try {
        await registerBySjc({ ...this.form, captchaUuid: this.captcha.uuid })
        this.$message.success('注册成功，请登录')
        this.$router.replace('/login')
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
.auth-card{width:520px}
.captcha-row{display:flex;gap:8px;align-items:center}
.captcha-img{width:130px;height:40px;border:1px solid #dcdfe6;cursor:pointer}
</style>
