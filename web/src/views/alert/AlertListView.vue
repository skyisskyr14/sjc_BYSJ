<template>
  <div class="page">
    <div class="page-header">
      <div>
        <div class="page-title">预警中心</div>
        <div class="page-sub">Alert Center & Response</div>
      </div>
      <div class="page-tools">
        <el-button size="mini" class="ghost" @click="goDashboard">返回主页</el-button>
        <div class="identity" v-if="profile.username">
          <span class="name">账号 {{ profile.username }}</span>
          <span class="role">{{ profile.sjcRoleName || profile.sjcRoleCode }}</span>
        </div>
      </div>
    </div>

    <div class="panel-head">
      <div class="stat">
        <span class="label">待确认</span>
        <span class="value danger">{{ pendingCount }}</span>
      </div>
      <div class="stat">
        <span class="label">总预警</span>
        <span class="value">{{ list.length }}</span>
      </div>
      <el-button size="mini" class="ghost" @click="load">刷新</el-button>
    </div>

    <div class="panel">
      <el-table :data="list" :empty-text="'暂无预警'">
        <el-table-column prop="alertType" label="类型"/>
        <el-table-column prop="alertLevel" label="级别"/>
        <el-table-column prop="status" label="状态">
          <template slot-scope="s">
            <el-tag :type="statusTagType(s.row.status)">{{ statusLabel(s.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertMessage" label="消息"/>
        <el-table-column label="操作" width="120">
          <template slot-scope="s">
            <el-button v-if="canAck && s.row.status==='UNCONFIRMED'" size="mini" type="primary" @click="ack(s.row)">确认</el-button>
            <span v-if="!canAck" class="readonly-cell">只读</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import { getSjcProfile } from '@/api/auth'
import { alertPage, alertAck } from '@/api/alert'

export default {
  data: () => ({ list: [], profile: {} }),
  computed: {
    pendingCount() { return this.list.filter(i => i.status === 'UNCONFIRMED').length },
    canAck() {
      return !!(this.profile && this.profile.capabilities && this.profile.capabilities.canAlertAck)
    }
  },
  async mounted() {
    await this.loadProfile()
    await this.load()
  },
  methods: {
    goDashboard() {
      this.$router.push('/dashboard')
    },
    async loadProfile() {
      try {
        const res = await getSjcProfile()
        this.profile = res.data || {}
      } catch (e) {
        this.profile = {}
      }
    },
    statusLabel(v) { return v === 'UNCONFIRMED' ? '待确认' : '已确认' },
    statusTagType(v) { return v === 'UNCONFIRMED' ? 'danger' : 'success' },
    async load() {
      const r = await alertPage({ pageNum: 1, pageSize: 50 })
      this.list = r.data.list || []
    },
    async ack(r) {
      if (!this.canAck) return this.$message.warning('当前身份无操作权限')
      await alertAck({ id: r.id })
      await this.load()
      this.$message.success('已确认')
    }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700&family=Noto+Sans+SC:wght@400;500;700&display=swap');

.page{
  min-height:100vh;
  padding:18px 20px 24px;
  color:#eaf2ff;
  font-family:"Space Grotesk","Noto Sans SC",sans-serif;
  background:
    radial-gradient(900px 520px at 15% 8%, rgba(84,214,255,0.16), transparent 60%),
    radial-gradient(700px 460px at 85% 18%, rgba(255,155,183,0.16), transparent 60%),
    linear-gradient(180deg, #060b16 0%, #0b1526 100%);
}
.page-header{display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:12px;gap:12px;flex-wrap:wrap}
.page-title{font-size:20px;font-weight:700;letter-spacing:1px}
.page-sub{font-size:12px;color:#97b0d1;letter-spacing:2px;margin-top:4px}
.page-tools{display:flex;align-items:center;gap:10px}
.ghost{background:rgba(12,22,38,.6);border:1px solid rgba(88,120,165,.4);color:#cfe1ff}
.identity{display:flex;align-items:center;gap:8px;padding:6px 10px;border-radius:10px;border:1px solid rgba(84,214,255,.35);background:rgba(12,22,38,.75)}
.identity .name{font-size:12px;color:#d6e6ff}
.identity .role{font-size:11px;color:#0a1b30;background:#54d6ff;border-radius:999px;padding:2px 8px;font-weight:600}
.panel-head{display:flex;align-items:center;gap:12px;margin-bottom:12px}
.stat{display:flex;flex-direction:column;gap:2px;padding:6px 10px;border:1px solid rgba(88,120,165,.35);border-radius:10px;background:rgba(12,22,38,.6)}
.stat .label{font-size:11px;color:#97b0d1}
.stat .value{font-size:16px;font-weight:700;color:#eaf2ff}
.stat .value.danger{color:#ff9bb7}

.panel{
  background:linear-gradient(180deg, rgba(16,30,52,.98), rgba(12,22,38,.98));
  border:1px solid rgba(88,120,165,.35);
  border-radius:14px;
  padding:12px 14px 16px;
  box-shadow:0 12px 26px rgba(6,12,22,.55);
}

.readonly-cell{font-size:12px;color:#9fb8db}
:deep(.el-table){background:transparent;color:#eaf2ff}
:deep(.el-table th){background:rgba(14,26,46,.9);color:#cfe1ff;border-bottom:1px solid rgba(88,120,165,.3)}
:deep(.el-table td){background:rgba(12,22,38,.6);border-bottom:1px solid rgba(88,120,165,.2)}
:deep(.el-table::before){background-color:rgba(88,120,165,.25)}
:deep(.el-table--enable-row-hover .el-table__body tr:hover>td){background:rgba(20,38,64,.8)}
</style>
