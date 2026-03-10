<template>
  <div class="page">
    <div class="page-header">
      <div>
        <div class="page-title">物资台账</div>
        <div class="page-sub">Inventory Ledger & Flow Tracking</div>
      </div>
      <div class="page-tools">
        <el-button size="mini" class="ghost" @click="goDashboard">返回主页</el-button>
        <div class="identity" v-if="profile.username">
          <span class="name">账号 {{ profile.username }}</span>
          <span class="role">{{ profile.sjcRoleName || profile.sjcRoleCode }}</span>
        </div>
      </div>
    </div>

    <div class="panel">
      <el-tabs v-model="tab" class="dark-tabs">
        <el-tab-pane label="物资字典" name="m"><MaterialTab :can-write="canInventoryWrite"/></el-tab-pane>
        <el-tab-pane label="库存" name="i"><InventoryTab :can-write="canInventoryWrite"/></el-tab-pane>
        <el-tab-pane label="流水" name="f"><FlowTab/></el-tab-pane>
      </el-tabs>
      <div v-if="!canInventoryWrite" class="readonly-tip">当前身份仅可查看台账数据，无法执行新增/修改/出入库操作。</div>
    </div>
  </div>
</template>

<script>
import { getSjcProfile } from '@/api/auth'
import MaterialTab from './tabs/MaterialTab.vue'
import InventoryTab from './tabs/InventoryTab.vue'
import FlowTab from './tabs/FlowTab.vue'

export default {
  components: { MaterialTab, InventoryTab, FlowTab },
  data: () => ({
    tab: 'm',
    profile: {}
  }),
  computed: {
    canInventoryWrite() {
      return !!(this.profile && this.profile.capabilities && this.profile.capabilities.canInventoryWrite)
    }
  },
  async mounted() {
    await this.loadProfile()
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

.panel{
  background:linear-gradient(180deg, rgba(16,30,52,.98), rgba(12,22,38,.98));
  border:1px solid rgba(88,120,165,.35);
  border-radius:14px;
  padding:12px 14px 16px;
  box-shadow:0 12px 26px rgba(6,12,22,.55);
}
.readonly-tip{margin-top:10px;color:#9fb8db;font-size:12px}

:deep(.el-tabs__nav-wrap::after){background-color:rgba(88,120,165,.35)}
:deep(.el-tabs__item){color:#bcd2f1}
:deep(.el-tabs__item.is-active){color:#54d6ff}
:deep(.el-tabs__active-bar){background:#54d6ff}

:deep(.el-table){background:transparent;color:#eaf2ff}
:deep(.el-table th){background:rgba(14,26,46,.9);color:#cfe1ff;border-bottom:1px solid rgba(88,120,165,.3)}
:deep(.el-table td){background:rgba(12,22,38,.6);border-bottom:1px solid rgba(88,120,165,.2)}
:deep(.el-table::before){background-color:rgba(88,120,165,.25)}
:deep(.el-table--enable-row-hover .el-table__body tr:hover>td){background:rgba(20,38,64,.8)}
:deep(.el-pagination){color:#bcd2f1;margin-top:12px}
:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next),
:deep(.el-pagination button){background:rgba(14,26,46,.9);color:#bcd2f1;border-color:rgba(88,120,165,.35)}
:deep(.el-pagination .el-pager li){background:rgba(14,26,46,.6);color:#bcd2f1}
:deep(.el-pagination .el-pager li.active){background:#54d6ff;color:#0b1526}

:deep(.el-dialog){background:rgba(12,22,38,.98);border:1px solid rgba(88,120,165,.4)}
:deep(.el-dialog__title){color:#eaf2ff}
:deep(.el-form-item__label){color:#bcd2f1}
</style>
