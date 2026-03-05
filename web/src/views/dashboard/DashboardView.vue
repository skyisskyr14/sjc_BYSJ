<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="8"><el-card>库存总量：{{ metrics.totalInventory || 0 }}</el-card></el-col>
      <el-col :span="8"><el-card>低库存预警：{{ metrics.lowStockAlerts || 0 }}</el-card></el-col>
      <el-col :span="8"><el-card>今日出库次数：{{ metrics.todayOutboundCount || 0 }}</el-card></el-col>
    </el-row>
    <el-card style="margin-top:16px">
      <div ref="chart" style="height:300px"></div>
    </el-card>
    <el-card style="margin-top:16px">
      <div slot="header">实时预警</div>
      <el-table :data="alerts" size="mini"><el-table-column prop="alertMessage" label="消息" /></el-table>
    </el-card>
  </div>
</template>
<script>
import * as echarts from 'echarts'
import { dashboardMetrics, dashboardTrend } from '@/api/dashboard'
import { alertPage } from '@/api/alert'
export default {
  data: () => ({ metrics: {}, alerts: [], chart: null }),
  computed: { wsMetrics(){ return this.$store.state.realtime.metrics }, wsAlerts(){ return this.$store.state.realtime.alerts } },
  watch: { wsMetrics(v){ if(v&&Object.keys(v).length) this.metrics=v }, wsAlerts(v){ this.alerts=[...v,...this.alerts].slice(0,20) } },
  async mounted() {
    this.$store.dispatch('realtime/initWs')
    this.chart = echarts.init(this.$refs.chart)
    await this.load()
  },
  methods: {
    async load() {
      const m = await dashboardMetrics(); this.metrics = m.data
      const t = await dashboardTrend();
      this.chart.setOption({tooltip:{},xAxis:{type:'category',data:t.data.map(i=>i.day)},yAxis:{type:'value'},series:[{name:'入库',type:'line',data:t.data.map(i=>i.inboundQty)},{name:'出库',type:'line',data:t.data.map(i=>i.outboundQty)}]})
      const a = await alertPage({ pageNum: 1, pageSize: 20 }); this.alerts = a.data.list || []
    }
  }
}
</script>
