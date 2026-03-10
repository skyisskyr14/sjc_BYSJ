<template>
  <div class="auth-shell">
    <div class="auth-left">
      <div class="brand">
        <div class="brand-badge">SJC</div>
        <h1>创建你的安全账户</h1>
        <p>统一身份认证，确保调度链路与数据访问的可控性。</p>
      </div>
      <div class="brand-points">
        <div class="point">
          <span class="dot"></span>
          <div>
            <strong>分级权限</strong>
            <div class="muted">角色绑定与仓库数据范围</div>
          </div>
        </div>
        <div class="point">
          <span class="dot"></span>
          <div>
            <strong>风险控制</strong>
            <div class="muted">验证码 + 安全密码双重校验</div>
          </div>
        </div>
      </div>
      <div class="brand-footer">Account Provisioning · SJC Platform</div>
    </div>
    <div class="auth-right">
      <el-card class="auth-card" shadow="never">
        <div class="auth-title">
          <div>
            <h2>用户注册</h2>
            <div class="sub">请填写真实信息以便审核</div>
          </div>
          <div class="auth-tag">JOIN</div>
        </div>
        <el-form :model="form" label-width="96px" @submit.native.prevent>
          <el-form-item label="用户名"><el-input v-model.trim="form.username" placeholder="请输入用户名" /></el-form-item>
          <el-form-item label="登录密码"><el-input v-model="form.password" type="password" show-password placeholder="请输入登录密码" /></el-form-item>
          <el-form-item label="安全密码"><el-input v-model="form.securePassword" type="password" show-password placeholder="用于敏感操作校验" /></el-form-item>
          <el-form-item label="邮箱"><el-input v-model.trim="form.email" placeholder="可选" /></el-form-item>
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
          <el-form-item class="action-row">
            <el-button class="primary-btn" type="primary" :loading="loading" @click="doRegister">注册</el-button>
            <el-button class="ghost-btn" type="text" @click="$router.push('/login')">返回登录</el-button>
          </el-form-item>
        </el-form>
        <div class="tips">提交后请等待管理员审核。</div>
      </el-card>
    </div>
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
      systemRoleId: 2, projectId: 2, sjcRoleCode: 'VIEWER'
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
@import url('https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700&family=Noto+Sans+SC:wght@400;500;700&display=swap');

.auth-shell{
  min-height:100vh;
  display:grid;
  grid-template-columns: 1fr 1fr;
  background:
    radial-gradient(900px 500px at 90% 10%, rgba(58,16,16,0.2), transparent 60%),
    linear-gradient(135deg, #1a0f2b 0%, #2a0f1d 45%, #1c1a10 100%);
  color:#fff3f0;
  font-family:"Space Grotesk","Noto Sans SC",sans-serif;
}
.auth-left{
  padding:64px 72px;
  display:flex;
  flex-direction:column;
  justify-content:center;
  gap:24px;
}
.brand-badge{
  width:64px;height:64px;border-radius:16px;
  background:linear-gradient(135deg,#ffb37a,#ff7aa2);
  color:#1a0f2b;font-weight:700;display:flex;align-items:center;justify-content:center;
  letter-spacing:2px;
}
.brand h1{margin:16px 0 8px;font-size:34px;letter-spacing:1px}
.brand p{margin:0;color:rgba(255,243,240,.75);max-width:420px;line-height:1.7}
.brand-points{display:flex;flex-direction:column;gap:14px}
.point{display:flex;gap:12px;align-items:flex-start}
.dot{width:10px;height:10px;border-radius:50%;background:#ff7aa2;margin-top:7px;box-shadow:0 0 12px rgba(255,122,162,.6)}
.muted{color:rgba(255,243,240,.68);font-size:13px;margin-top:4px}
.brand-footer{margin-top:auto;color:rgba(255,243,240,.45);letter-spacing:2px;font-size:12px}

.auth-right{
  display:flex;align-items:center;justify-content:center;padding:40px;
  background:
    linear-gradient(180deg, rgba(255,255,255,.08), rgba(255,255,255,0)),
    radial-gradient(500px 400px at 20% 20%, rgba(255,255,255,.12), transparent 60%);
}
.auth-card{
  width:min(520px, 92vw);
  border-radius:20px;
  border:1px solid rgba(255,255,255,.2);
  background:rgba(255,255,255,.96);
  box-shadow:0 24px 60px rgba(20,6,18,.45);
  animation:fadeUp .6s ease-out;
}
.auth-title{display:flex;align-items:center;justify-content:space-between;margin-bottom:10px}
.auth-title h2{margin:0;color:#2a0f1d;font-size:26px}
.auth-title .sub{color:#7c6b7a;font-size:13px;margin-top:6px}
.auth-tag{font-size:11px;letter-spacing:2px;color:#2a0f1d;background:#ffe7ef;padding:6px 10px;border-radius:999px}
.captcha-row{display:flex;gap:8px;align-items:center}
.captcha-img{width:130px;height:40px;border:1px solid #dcdfe6;cursor:pointer;border-radius:6px}
.action-row :deep(.el-form-item__content){display:flex;gap:8px;align-items:center}
.primary-btn{width:120px}
.ghost-btn{color:#2a0f1d}
.tips{margin-top:8px;color:#8a96a8;font-size:12px}

@keyframes fadeUp{from{opacity:0;transform:translateY(12px)}to{opacity:1;transform:translateY(0)}}

@media (max-width: 960px){
  .auth-shell{grid-template-columns:1fr}
  .auth-left{padding:40px 32px}
  .auth-right{padding:24px 16px}
}
</style>
