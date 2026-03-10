<template>
  <div class="page">
    <div class="page-header">
      <div>
        <div class="page-title">调度任务</div>
        <div class="page-sub">Dispatch Tasks & Progress</div>
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
        <span class="label">总任务</span>
        <span class="value">{{ list.length }}</span>
      </div>
      <div class="stat">
        <span class="label">已完成</span>
        <span class="value success">{{ doneCount }}</span>
      </div>
      <el-button size="mini" class="ghost" @click="load">刷新</el-button>
    </div>

    <div class="panel">
      <el-empty v-if="!list.length" description="暂无任务" />
      <el-table v-else :data="list">
        <el-table-column prop="taskNo" label="任务号"/>
        <el-table-column prop="status" label="状态">
          <template slot-scope="s">
            <el-tag :type="statusTagType(s.row.status)">{{ statusLabel(s.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="demandPointName" label="需求点"/>
        <el-table-column label="进度">
          <template slot-scope="s">{{ formatProgress(s.row.progressPercent) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间"/>
        <el-table-column label="操作" width="180">
          <template slot-scope="s">
            <el-button
              v-if="canDispatchWrite && nextAction(s.row).label"
              size="mini"
              type="primary"
              @click="changeStatus(s.row, nextAction(s.row).status)">
              {{ nextAction(s.row).label }}
            </el-button>
            <span v-if="!canDispatchWrite" class="readonly-cell">只读</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import { getSjcProfile } from '@/api/auth'
import { taskList, updateTaskStatus } from '@/api/dispatch'

export default {
  data: () => ({ list: [], profile: {} }),
  computed: {
    doneCount() { return this.list.filter(i => i.status === 'DONE').length },
    canDispatchWrite() {
      return !!(this.profile && this.profile.capabilities && this.profile.capabilities.canDispatchWrite)
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
    async load() {
      const r = await taskList()
      this.list = r.data || []
    },
    statusLabel(v) {
      const map = { NEW: '新建', CREATED: '已创建', RUNNING: '执行中', ARRIVED: '已到达', DONE: '已完成' }
      return map[v] || v
    },
    statusTagType(v) {
      if (v === 'DONE') return 'success'
      if (v === 'RUNNING') return 'warning'
      if (v === 'ARRIVED') return 'info'
      return ''
    },
    formatProgress(v) {
      const n = Number(v)
      return Number.isFinite(n) ? `${n}%` : '--'
    },
    nextAction(row) {
      if (row.status === 'NEW' || row.status === 'CREATED') return { status: 'RUNNING', label: '开始执行' }
      if (row.status === 'RUNNING') return { status: 'ARRIVED', label: '标记到达' }
      if (row.status === 'ARRIVED') return { status: 'DONE', label: '完成任务' }
      return { status: '', label: '' }
    },
    async changeStatus(row, toStatus) {
      if (!this.canDispatchWrite) return this.$message.warning('当前身份无操作权限')
      if (!toStatus) return
      await updateTaskStatus({ taskId: row.id, toStatus, remark: '前端状态推进' })
      this.$message.success('状态已更新')
      await this.load()
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
.stat .value.success{color:#6ee7a1}

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
