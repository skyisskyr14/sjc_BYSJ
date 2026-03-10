<template>
  <div class="auth-shell">
    <div class="auth-left">
      <div class="brand">
        <div class="brand-badge">SJC</div>
        <h1>应急物资调度平台</h1>
        <p>统一库存、预警与调度，构建可追踪的应急保障闭环。</p>
      </div>
      <div class="brand-points">
        <div class="point">
          <span class="dot"></span>
          <div>
            <strong>实时指标</strong>
            <div class="muted">Redis + WebSocket 直达大屏</div>
          </div>
        </div>
        <div class="point">
          <span class="dot"></span>
          <div>
            <strong>可靠投递</strong>
            <div class="muted">Outbox + Kafka + Flink</div>
          </div>
        </div>
        <div class="point">
          <span class="dot"></span>
          <div>
            <strong>全链路可审计</strong>
            <div class="muted">操作日志与轨迹追踪</div>
          </div>
        </div>
      </div>
      <div class="brand-footer">Emergency Logistics · SJC Platform</div>
    </div>
    <div class="auth-right">
      <el-card class="auth-card" shadow="never">
        <div class="auth-title">
          <div>
            <h2>用户登录</h2>
            <div class="sub">欢迎回来，请完成身份验证</div>
          </div>
          <div class="auth-tag">SECURE</div>
        </div>
        <el-form :model="form" label-width="90px" @submit.native.prevent>
          <el-form-item label="用户名">
            <el-input v-model.trim="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>
          <el-form-item label="验证码">
            <div class="captcha-row">
              <el-input v-model.trim="form.captchaInput" placeholder="输入验证码" />
              <img v-if="captcha.data" :src="`data:image/png;base64,${captcha.data}`" class="captcha-img" @click="loadCaptcha" />
            </div>
          </el-form-item>
          <el-form-item class="action-row">
            <el-button class="primary-btn" type="primary" :loading="loading" @click="doLogin">登录</el-button>
            <el-button class="ghost-btn" type="text" @click="$router.push('/register')">去注册</el-button>
          </el-form-item>
        </el-form>
        <div class="tips">如遇登录异常，请联系系统管理员。</div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { getCaptcha, loginByUser } from '@/api/auth'

export default {
  data: () => ({
    loading: false,
    captcha: { uuid: '', data: '' },
    form: { username: '', password: '', captchaInput: '', captchaType: 'image', type: 2, role: 2 }
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
@import url('https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700&family=Noto+Sans+SC:wght@400;500;700&display=swap');

.auth-shell{
  min-height:100vh;
  display:grid;
  grid-template-columns: 1.1fr 0.9fr;
  background:
    radial-gradient(1200px 600px at 10% 10%, rgba(11,31,58,0.18), transparent 60%),
    linear-gradient(120deg, #0b1f3a 0%, #0f2b4e 35%, #102b2b 100%);
  color:#eaf2ff;
  font-family:"Space Grotesk","Noto Sans SC",sans-serif;
}
.auth-left{
  padding:64px 72px;
  display:flex;
  flex-direction:column;
  justify-content:center;
  gap:28px;
  position:relative;
}
.brand-badge{
  width:64px;height:64px;border-radius:16px;
  background:linear-gradient(135deg,#7ef0ff,#6dd0ff);
  color:#0b1f3a;font-weight:700;display:flex;align-items:center;justify-content:center;
  letter-spacing:2px;
}
.brand h1{margin:16px 0 8px;font-size:36px;letter-spacing:1px}
.brand p{margin:0;color:rgba(234,242,255,.75);max-width:440px;line-height:1.7}
.brand-points{display:flex;flex-direction:column;gap:16px}
.point{display:flex;gap:12px;align-items:flex-start}
.dot{width:10px;height:10px;border-radius:50%;background:#67c23a;margin-top:7px;box-shadow:0 0 12px rgba(103,194,58,.6)}
.muted{color:rgba(234,242,255,.68);font-size:13px;margin-top:4px}
.brand-footer{margin-top:auto;color:rgba(234,242,255,.45);letter-spacing:2px;font-size:12px}

.auth-right{
  display:flex;align-items:center;justify-content:center;padding:40px;
  background:
    linear-gradient(180deg, rgba(255,255,255,.08), rgba(255,255,255,0)),
    radial-gradient(500px 400px at 80% 20%, rgba(255,255,255,.12), transparent 60%);
}
.auth-card{
  width:min(460px, 92vw);
  border-radius:20px;
  border:1px solid rgba(255,255,255,.2);
  background:rgba(255,255,255,.96);
  box-shadow:0 24px 60px rgba(10,22,45,.35);
  animation:fadeUp .6s ease-out;
}
.auth-title{display:flex;align-items:center;justify-content:space-between;margin-bottom:10px}
.auth-title h2{margin:0;color:#0b1f3a;font-size:26px}
.auth-title .sub{color:#6b7a90;font-size:13px;margin-top:6px}
.auth-tag{font-size:11px;letter-spacing:2px;color:#0f2b4e;background:#e7f0ff;padding:6px 10px;border-radius:999px}
.captcha-row{display:flex;gap:8px;align-items:center}
.captcha-img{width:130px;height:40px;border:1px solid #dcdfe6;cursor:pointer;border-radius:6px}
.action-row :deep(.el-form-item__content){display:flex;gap:8px;align-items:center}
.primary-btn{width:120px}
.ghost-btn{color:#0f2b4e}
.tips{margin-top:8px;color:#8a96a8;font-size:12px}

@keyframes fadeUp{from{opacity:0;transform:translateY(12px)}to{opacity:1;transform:translateY(0)}}

@media (max-width: 960px){
  .auth-shell{grid-template-columns:1fr}
  .auth-left{padding:40px 32px}
  .auth-right{padding:24px 16px}
}
</style>
