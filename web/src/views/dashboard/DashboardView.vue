<template>
  <div class="dash">
    <header class="dash-header">
      <div class="title-block">
        <div class="title">SJC 应急物资态势总览</div>
        <div class="subtitle">REALTIME SITUATION CENTER</div>
      </div>
      <div class="header-actions">
        <div class="identity-chip" v-if="profile.username">
          <span class="identity-name">账号 {{ profile.username }}</span>
          <span class="identity-role">{{ profile.sjcRoleName || profile.sjcRoleCode }}</span>
        </div>
        <div class="clock">{{ now }}</div>
        <el-button type="text" class="logout-btn" @click="logout">退出登录</el-button>
      </div>
    </header>

    <div class="dash-grid">
      <div class="col col-left">
        <div class="panel kpi-panel">
          <div class="panel-title">核心指标</div>
          <div class="kpi-row">
            <div class="kpi">
              <div class="kpi-label">库存总量</div>
              <div class="kpi-value">{{ metrics.totalInventory || 0 }}</div>
              <div class="kpi-sub">实时口径</div>
            </div>
            <div class="kpi">
              <div class="kpi-label">低库存预警</div>
              <div class="kpi-value warn">{{ metrics.lowStockAlerts || 0 }}</div>
              <div class="kpi-sub">未确认</div>
            </div>
            <div class="kpi">
              <div class="kpi-label">今日出库次数</div>
              <div class="kpi-value">{{ metrics.todayOutboundCount || 0 }}</div>
              <div class="kpi-sub">当日累计</div>
            </div>
          </div>
        </div>

        <div class="panel nav-panel">
          <div class="panel-title">业务入口</div>
          <div class="nav-grid">
            <router-link v-for="item in navTiles" :key="item.key" :to="item.to" class="nav-card" :class="item.tone">
              <div class="nav-top">
                <div class="nav-name">{{ item.name }}</div>
                <div class="nav-count">{{ item.count }}</div>
              </div>
              <div class="nav-desc">{{ item.desc }}</div>
              <div class="nav-action">进入</div>
            </router-link>
          </div>
        </div>

        <div class="panel chart-panel">
          <div class="panel-title">仓库出入库Top</div>
          <div ref="barChart" class="chart tall"></div>
        </div>

        <div class="panel chart-panel bottom-panel">
          <div class="panel-title">预警结构占比</div>
          <div ref="ringChart" class="chart"></div>
        </div>
      </div>

      <div class="col col-center">
        <div class="panel map-panel">
          <div class="panel-title">城市保障态势</div>
          <div ref="mapChart" class="chart map-chart"></div>
          <div class="map-note">点位数据来自仓库坐标与库存汇总</div>
        </div>

        <div class="panel chart-panel">
          <div class="panel-title">近7日出入库趋势</div>
          <div ref="lineChart" class="chart"></div>
        </div>

        <div class="panel list-panel bottom-panel">
          <div class="panel-title">实时预警</div>
          <div class="ticker">
            <div v-if="!alerts.length" class="empty">暂无预警</div>
            <div v-for="(a,i) in alerts.slice(0,6)" :key="i" class="ticker-item">
              <span class="dot"></span>
              <span class="msg">{{ a.alertMessage || '库存告警' }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="col col-right">
        <div class="panel chart-panel">
          <div class="panel-title">库存结构分布</div>
          <div ref="pieChart" class="chart"></div>
        </div>

        <div class="panel chart-panel">
          <div class="panel-title">调度任务完成率</div>
          <div ref="gaugeChart" class="chart"></div>
        </div>

        <div class="panel list-panel tall bottom-panel">
          <div class="panel-title">重点物资Top</div>
          <div class="rank-list">
            <div v-for="(r,i) in rankList" :key="r.name" class="rank-item">
              <span class="rank-no">{{ i + 1 }}</span>
              <span class="rank-name">{{ r.name }}</span>
              <span class="rank-val">{{ r.value }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { dashboardMetrics, dashboardTrend } from '@/api/dashboard'
import { alertPage } from '@/api/alert'
import { warehousePage } from '@/api/warehouse'
import { inventoryPage } from '@/api/inventory'
import { materialPage } from '@/api/material'
import { taskList } from '@/api/dispatch'
import { getSjcProfile, logoutByUser } from '@/api/auth'
import chinaJson from '@/assets/geo/china.json'

export default {
  data: () => ({
    metrics: {},
    alerts: [],
    trend: [],
    now: '',
    charts: {},
    warehouseList: [],
    inventoryList: [],
    materialList: [],
    taskList: [],
    rankList: [],
    mapRegistered: false,
    profile: {}
  }),
  computed: {
    wsMetrics() { return this.$store.state.realtime.metrics },
    wsAlerts() { return this.$store.state.realtime.alerts },
    navTiles() {
      const alertCount = this.metrics.lowStockAlerts != null ? this.metrics.lowStockAlerts : this.alerts.length
      return [
        { key: 'inventory', name: '物资台账', desc: '库存与出入库记录', count: this.inventoryList.length, to: '/inventory/list', tone: 'blue' },
        { key: 'alert', name: '预警中心', desc: '低库存与异常预警', count: alertCount || 0, to: '/alert/list', tone: 'pink' },
        { key: 'task', name: '调度任务', desc: '任务执行与进度', count: this.taskList.length, to: '/task/list', tone: 'green' }
      ]
    }
  },
  watch: {
    wsMetrics(v) {
      if (v && Object.keys(v).length) {
        this.metrics = v
        this.renderCharts()
      }
    },
    wsAlerts(v) {
      if (Array.isArray(v) && v.length) {
        this.alerts = [...v, ...this.alerts].slice(0, 20)
      }
    }
  },
  async mounted() {
    this.$store.dispatch('realtime/initWs')
    this.ensureMap()
    this.initCharts()
    this.tick()
    this.timer = setInterval(this.tick, 1000)
    await this.loadProfile()
    await this.load()
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    clearInterval(this.timer)
    window.removeEventListener('resize', this.resizeCharts)
    Object.values(this.charts).forEach(c => c && c.dispose && c.dispose())
  },
  methods: {
    async loadProfile() {
      try {
        const res = await getSjcProfile()
        this.profile = res.data || {}
      } catch (e) {
        this.profile = {}
      }
    },
    async logout() {
      await logoutByUser()
      this.$message.success('已退出')
      this.$router.replace('/login')
    },
    tick() {
      const d = new Date()
      const pad = (n) => String(n).padStart(2, '0')
      this.now = `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
    },
    ensureMap() {
      if (this.mapRegistered) return
      if (echarts && echarts.registerMap) {
        echarts.registerMap('china', chinaJson)
        this.mapRegistered = true
      }
    },
    initCharts() {
      this.charts.bar = echarts.init(this.$refs.barChart)
      this.charts.ring = echarts.init(this.$refs.ringChart)
      this.charts.line = echarts.init(this.$refs.lineChart)
      this.charts.pie = echarts.init(this.$refs.pieChart)
      this.charts.gauge = echarts.init(this.$refs.gaugeChart)
      if (this.$refs.mapChart) this.charts.map = echarts.init(this.$refs.mapChart)
      this.renderCharts()
    },
    resizeCharts() {
      Object.values(this.charts).forEach(c => c && c.resize())
    },
    extractList(res) {
      if (!res || !res.data) return []
      if (Array.isArray(res.data)) return res.data
      if (Array.isArray(res.data.list)) return res.data.list
      if (Array.isArray(res.data.records)) return res.data.records
      if (Array.isArray(res.data.rows)) return res.data.rows
      return []
    },
    toNum(v) {
      const n = Number(v)
      return Number.isFinite(n) ? n : 0
    },
    async load() {
      const safe = (promise) => promise
        .then(value => ({ ok: true, value }))
        .catch(error => ({ ok: false, error }))

      const results = await Promise.all([
        safe(dashboardMetrics()),
        safe(dashboardTrend()),
        safe(alertPage({ pageNum: 1, pageSize: 20 })),
        safe(warehousePage({ pageNum: 1, pageSize: 2000 })),
        safe(inventoryPage({ pageNum: 1, pageSize: 2000 })),
        safe(materialPage({ pageNum: 1, pageSize: 2000 })),
        safe(taskList())
      ])

      const [m, t, a, w, i, mat, task] = results
      if (m.ok) this.metrics = m.value.data || {}
      if (t.ok) this.trend = Array.isArray(t.value.data) ? t.value.data : []
      if (a.ok) this.alerts = this.extractList(a.value)
      if (w.ok) this.warehouseList = this.extractList(w.value)
      if (i.ok) this.inventoryList = this.extractList(i.value)
      if (mat.ok) this.materialList = this.extractList(mat.value)
      if (task.ok) this.taskList = this.extractList(task.value)

      if (results.some(r => !r.ok)) {
        this.$message.warning('部分数据加载失败')
      }
      this.renderCharts()
    },
    renderCharts() {
      const textColor = '#e3efff'
      const mutedColor = '#9bb2d4'
      const lineColor = '#2b4566'
      const splitLine = 'rgba(110,140,180,0.2)'
      const accent = '#4dd3ff'
      const accent2 = '#ff8fb1'

      const trend = this.trend.length ? this.trend : [
        { day: '周一', inboundQty: 120, outboundQty: 90 },
        { day: '周二', inboundQty: 160, outboundQty: 110 },
        { day: '周三', inboundQty: 140, outboundQty: 130 },
        { day: '周四', inboundQty: 180, outboundQty: 120 },
        { day: '周五', inboundQty: 150, outboundQty: 100 },
        { day: '周六', inboundQty: 190, outboundQty: 140 },
        { day: '周日', inboundQty: 170, outboundQty: 115 }
      ]

      const warehouseNameMap = new Map()
      this.warehouseList.forEach(w => {
        if (w && w.id != null) warehouseNameMap.set(w.id, w.warehouseName || `仓库${w.id}`)
      })
      this.inventoryList.forEach(row => {
        if (row && row.warehouseId != null && row.warehouseName && !warehouseNameMap.has(row.warehouseId)) {
          warehouseNameMap.set(row.warehouseId, row.warehouseName)
        }
      })

      const warehouseTotals = {}
      this.inventoryList.forEach(row => {
        const id = row.warehouseId
        if (id == null) return
        warehouseTotals[id] = (warehouseTotals[id] || 0) + this.toNum(row.qtyAvailable)
      })
      this.warehouseList.forEach(w => {
        if (w && w.id != null && warehouseTotals[w.id] == null) warehouseTotals[w.id] = 0
      })

      const warehouseRank = Object.keys(warehouseTotals).map(id => ({
        id: Number(id),
        name: warehouseNameMap.get(Number(id)) || `仓库${id}`,
        total: warehouseTotals[id]
      })).sort((a, b) => b.total - a.total)

      const barItems = warehouseRank.slice(0, 6)
      const barCategories = barItems.length ? barItems.map(i => i.name) : ['市南主仓', '市北分仓', '崂山分仓', '黄岛分仓']
      const barData = barItems.length ? barItems.map(i => i.total) : [320, 280, 190, 160]

      const materialMap = new Map()
      this.materialList.forEach(m => {
        if (m && m.id != null) materialMap.set(m.id, { name: m.materialName, type: m.materialType })
      })

      const typeTotals = {}
      const materialTotals = {}
      this.inventoryList.forEach(row => {
        const qty = this.toNum(row.qtyAvailable)
        const materialId = row.materialId
        const materialName = row.materialName || (materialMap.get(materialId) || {}).name || (materialId ? `物资${materialId}` : '未命名')
        const materialType = (materialMap.get(materialId) || {}).type || '其他'
        typeTotals[materialType] = (typeTotals[materialType] || 0) + qty
        materialTotals[materialName] = (materialTotals[materialName] || 0) + qty
      })

      const pieData = Object.keys(typeTotals).map(key => ({ name: key, value: typeTotals[key] }))
      const fallbackPie = [
        { value: 420, name: '生活保障' },
        { value: 260, name: '医疗防护' },
        { value: 160, name: '安置救援' },
        { value: 90, name: '其他' }
      ]

      const rankList = Object.keys(materialTotals)
        .map(name => ({ name, value: materialTotals[name] }))
        .sort((a, b) => b.value - a.value)
        .slice(0, 6)
      this.rankList = rankList.length ? rankList : [
        { name: '矿泉水', value: 480 },
        { name: '医用口罩', value: 365 },
        { name: '应急帐篷', value: 210 },
        { name: '方便面', value: 180 },
        { name: '急救包', value: 120 }
      ]

      let lowStockCount = 0
      let normalStockCount = 0
      this.inventoryList.forEach(row => {
        if (row.warnThreshold == null) return
        const available = this.toNum(row.qtyAvailable)
        const threshold = this.toNum(row.warnThreshold)
        if (available <= threshold) lowStockCount += 1
        else normalStockCount += 1
      })
      const ringHasData = (lowStockCount + normalStockCount) > 0
      const ringData = ringHasData ? [
        { value: lowStockCount, name: '低库存' },
        { value: normalStockCount, name: '正常' }
      ] : [{ value: 1, name: '暂无数据' }]
      const ringColors = ringHasData ? [accent2, accent] : ['#41566f']

      let taskProgress = 0
      if (this.taskList.length) {
        const statusMap = { NEW: 0, CREATED: 0, RUNNING: 30, ARRIVED: 80, DONE: 100 }
        const total = this.taskList.reduce((sum, t) => {
          const v = this.toNum(t.progressPercent)
          if (v > 0) return sum + v
          return sum + (statusMap[t.status] || 0)
        }, 0)
        taskProgress = Math.round(total / this.taskList.length)
      } else if (this.metrics.todayOutboundCount) {
        taskProgress = Math.min(100, Math.max(0, this.toNum(this.metrics.todayOutboundCount) * 10))
      }

      const mapData = this.warehouseList.map(w => {
        const lng = this.toNum(w.longitude)
        const lat = this.toNum(w.latitude)
        if (!Number.isFinite(lng) || !Number.isFinite(lat) || (lng === 0 && lat === 0)) return null
        const total = warehouseTotals[w.id] || 0
        return { name: w.warehouseName || `仓库${w.id}`, value: [lng, lat, total] }
      }).filter(Boolean)

      const mapMax = mapData.reduce((max, item) => Math.max(max, item.value[2] || 0), 1)

      this.charts.line.setOption({
        backgroundColor: 'transparent',
        tooltip: { trigger: 'axis', backgroundColor: 'rgba(14,25,45,0.9)', borderColor: lineColor, textStyle: { color: textColor } },
        grid: { left: 40, right: 20, top: 30, bottom: 30 },
        legend: { textStyle: { color: textColor } },
        xAxis: {
          type: 'category',
          data: trend.map(i => i.day),
          axisLine: { lineStyle: { color: lineColor } },
          axisLabel: { color: mutedColor }
        },
        yAxis: {
          type: 'value',
          axisLine: { lineStyle: { color: lineColor } },
          axisLabel: { color: mutedColor },
          splitLine: { lineStyle: { color: splitLine } }
        },
        series: [
          { name: '入库', type: 'line', smooth: true, data: trend.map(i => this.toNum(i.inboundQty)), lineStyle: { color: accent }, itemStyle: { color: accent } },
          { name: '出库', type: 'line', smooth: true, data: trend.map(i => this.toNum(i.outboundQty)), lineStyle: { color: accent2 }, itemStyle: { color: accent2 } }
        ]
      })

      this.charts.bar.setOption({
        backgroundColor: 'transparent',
        tooltip: { trigger: 'axis', backgroundColor: 'rgba(14,25,45,0.9)', borderColor: lineColor, textStyle: { color: textColor } },
        grid: { left: 40, right: 20, top: 20, bottom: 30 },
        xAxis: {
          type: 'category',
          data: barCategories,
          axisLine: { lineStyle: { color: lineColor } },
          axisLabel: { color: mutedColor }
        },
        yAxis: {
          type: 'value',
          axisLine: { lineStyle: { color: lineColor } },
          axisLabel: { color: mutedColor },
          splitLine: { lineStyle: { color: splitLine } }
        },
        series: [{
          type: 'bar',
          data: barData,
          barWidth: 18,
          itemStyle: { color: accent }
        }]
      })

      this.charts.ring.setOption({
        backgroundColor: 'transparent',
        tooltip: { trigger: 'item', backgroundColor: 'rgba(14,25,45,0.9)', borderColor: lineColor, textStyle: { color: textColor } },
        series: [
          {
            type: 'pie',
            radius: ['55%', '75%'],
            label: ringHasData ? { color: textColor } : { show: false },
            data: ringData,
            itemStyle: { borderColor: '#0b1f3a', borderWidth: 2 },
            color: ringColors
          }
        ]
      })

      this.charts.pie.setOption({
        backgroundColor: 'transparent',
        tooltip: { trigger: 'item', backgroundColor: 'rgba(14,25,45,0.9)', borderColor: lineColor, textStyle: { color: textColor } },
        series: [
          {
            type: 'pie',
            radius: '70%',
            label: { color: textColor },
            data: pieData.length ? pieData : fallbackPie,
            color: [accent, '#7ef0ff', '#ffb37a', accent2]
          }
        ]
      })

      this.charts.gauge.setOption({
        tooltip: { backgroundColor: 'rgba(14,25,45,0.9)', borderColor: lineColor, textStyle: { color: textColor } },
        series: [
          {
            type: 'gauge',
            startAngle: 210,
            endAngle: -30,
            progress: { show: true, width: 10 },
            axisLine: { lineStyle: { width: 10, color: [[1, accent]] } },
            pointer: { width: 3 },
            axisTick: { show: false },
            splitLine: { show: false },
            axisLabel: { color: mutedColor },
            title: { color: textColor, offsetCenter: [0, '78%'], fontSize: 12 },
            detail: { valueAnimation: true, formatter: '{value}%', color: accent, fontSize: 18, offsetCenter: [0, '40%'] },
            data: [{ value: taskProgress, name: '任务完成率' }]
          }
        ]
      })

      if (this.charts.map) {
        this.charts.map.setOption({
          backgroundColor: 'transparent',
          tooltip: {
            trigger: 'item',
            backgroundColor: 'rgba(14,25,45,0.9)',
            borderColor: lineColor,
            textStyle: { color: textColor },
            formatter: (p) => {
              if (!p || !p.value || !Array.isArray(p.value)) return p.name
              return `${p.name}<br/>库存: ${p.value[2]}`
            }
          },
          geo: {
            map: 'china',
            roam: true,
            zoom: 1.1,
            itemStyle: { areaColor: '#0f2a45', borderColor: '#3e5d86', borderWidth: 1 },
            emphasis: { itemStyle: { areaColor: '#164d80' } },
            label: { show: false }
          },
          series: [
            { type: 'map', map: 'china', geoIndex: 0, data: [] },
            {
              type: 'effectScatter',
              coordinateSystem: 'geo',
              data: mapData,
              symbolSize: (val) => {
                const v = Array.isArray(val) ? val[2] : 0
                return 6 + Math.sqrt(v / mapMax) * 18
              },
              rippleEffect: { scale: 2.6, brushType: 'stroke' },
              itemStyle: { color: accent, shadowBlur: 10, shadowColor: 'rgba(77,211,255,0.6)' },
              label: {
                show: mapData.length > 0,
                formatter: (p) => `${p.name} ${p.value[2]}`,
                color: textColor,
                fontSize: 10,
                position: 'right'
              }
            }
          ],
          graphic: mapData.length ? [] : [{
            type: 'text',
            left: 'center',
            top: 'middle',
            style: { text: '暂无仓库坐标数据', fill: mutedColor, fontSize: 12 }
          }]
        })
      }
    }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700&family=Noto+Sans+SC:wght@400;500;700&display=swap');

.dash{
  min-height:100vh;
  background:
    radial-gradient(900px 520px at 15% 8%, rgba(84,214,255,0.18), transparent 60%),
    radial-gradient(700px 460px at 85% 18%, rgba(255,155,183,0.18), transparent 60%),
    radial-gradient(500px 380px at 50% 90%, rgba(110,231,161,0.12), transparent 60%),
    linear-gradient(180deg, #060b16 0%, #0b1526 100%);
  --bg:#060b16;
  --panel:#0f223b;
  --panel-strong:#0f1c31;
  --line:rgba(88,120,165,.45);
  --text:#eaf2ff;
  --muted:#97b0d1;
  --accent:#54d6ff;
  --accent2:#ff9bb7;
  --accent3:#6ee7a1;
  --shadow:rgba(6,12,22,.55);
  color:var(--text);
  font-family:"Space Grotesk","Noto Sans SC",sans-serif;
  -webkit-font-smoothing:antialiased;
  text-rendering:optimizeLegibility;
  font-size:14px;
  line-height:1.4;
}

.dash-header{
  display:flex;align-items:center;justify-content:space-between;
  padding:18px 26px;
  border-bottom:1px solid rgba(88,120,165,.35);
  background:linear-gradient(180deg, rgba(10,18,34,.8), rgba(10,18,34,0));
}
.title{font-size:22px;font-weight:700;letter-spacing:1px}
.subtitle{font-size:12px;color:var(--muted);letter-spacing:3px;margin-top:4px}
.header-actions{display:flex;align-items:center;gap:10px}
.identity-chip{
  display:flex;
  align-items:center;
  gap:8px;
  padding:6px 10px;
  border-radius:10px;
  border:1px solid rgba(84,214,255,.35);
  background:rgba(13,26,44,.75);
}
.identity-name{font-size:12px;color:#d6e6ff}
.identity-role{
  font-size:11px;
  color:#0a1b30;
  background:#54d6ff;
  border-radius:999px;
  padding:2px 8px;
  font-weight:600;
}
.clock{font-size:13px;color:#d9e7ff;background:rgba(13,26,44,.85);padding:8px 12px;border-radius:10px;border:1px solid var(--line);box-shadow:0 6px 16px rgba(9,16,30,.35);font-variant-numeric:tabular-nums}
.logout-btn{color:#eaf2ff;padding:8px 10px}
.logout-btn:hover{color:#54d6ff}

.dash-grid{
  display:grid;
  grid-template-columns: minmax(320px, 1.1fr) minmax(420px, 1.5fr) minmax(300px, 1fr);
  gap:16px;
  padding:16px;
  max-width:1680px;
  margin:0 auto;
  align-items:stretch;
  min-height:calc(100vh - 86px);
}
.col{display:flex;flex-direction:column;gap:14px;height:100%}
.panel{
  background:linear-gradient(180deg, rgba(16,30,52,.98), rgba(12,22,38,.98));
  border:1px solid rgba(88,120,165,.35);
  border-radius:14px;
  padding:14px;
  box-shadow:0 12px 26px var(--shadow);
  backdrop-filter: blur(8px);
}
.bottom-panel{margin-top:auto;min-height:260px}
.panel-title{font-size:13px;color:#cfe1ff;letter-spacing:2px;margin-bottom:10px;position:relative;padding-left:12px}
.panel-title::before{content:'';position:absolute;left:0;top:50%;transform:translateY(-50%);width:6px;height:6px;border-radius:50%;background:var(--accent)}
.chart{width:100%;height:230px}
.chart.tall{height:270px}

.kpi-row{display:grid;grid-template-columns:repeat(3,1fr);gap:10px}
.kpi{background:linear-gradient(135deg, rgba(16,34,58,.95), rgba(12,24,44,.95));border:1px solid var(--line);border-radius:12px;padding:12px;box-shadow:inset 0 0 0 1px rgba(84,214,255,.05)}
.kpi-label{font-size:12px;color:var(--muted)}
.kpi-value{font-size:24px;font-weight:700;margin:6px 0;color:#f4f9ff;font-variant-numeric:tabular-nums}
.kpi-sub{font-size:11px;color:#8da5c8}
.kpi-value.warn{color:var(--accent2)}

.nav-panel{padding-bottom:14px}
.nav-grid{
  display:grid;
  grid-template-columns:repeat(3, minmax(0, 1fr));
  gap:10px;
}
.nav-card{
  display:flex;
  flex-direction:column;
  gap:6px;
  padding:14px;
  border-radius:12px;
  min-height:88px;
  border:1px solid rgba(84,214,255,.22);
  background:linear-gradient(145deg, rgba(18,38,68,.95), rgba(11,22,42,.95));
  color:#eaf2ff;
  text-decoration:none;
  transition:transform .2s ease, border-color .2s ease, box-shadow .2s ease;
}
.nav-card:hover{
  transform:translateY(-2px);
  border-color:rgba(84,214,255,.75);
  box-shadow:0 8px 20px rgba(12,25,45,.45);
}
.nav-card.blue{border-color:rgba(84,214,255,.5)}
.nav-card.pink{border-color:rgba(255,155,183,.5)}
.nav-card.green{border-color:rgba(110,231,161,.5)}
.nav-top{display:flex;align-items:center;justify-content:space-between}
.nav-name{font-size:13px;font-weight:600;letter-spacing:1px}
.nav-count{font-size:20px;font-weight:700;color:#f9fcff;font-variant-numeric:tabular-nums}
.nav-desc{font-size:12px;color:#bcd2f1}
.nav-action{font-size:11px;color:#a6d8ff;letter-spacing:2px}

.list-panel{height:210px;overflow:hidden}
.list-panel.tall{height:280px}
.ticker{display:flex;flex-direction:column;gap:8px}
.ticker-item{display:flex;gap:8px;align-items:center;font-size:12px;color:#e6f1ff}
.ticker .dot{width:8px;height:8px;border-radius:50%;background:var(--accent2);box-shadow:0 0 8px rgba(255,143,177,.7)}
.empty{color:var(--muted);font-size:12px}

.map-panel{height:400px}
.map-chart{height:320px}
.map-note{margin-top:6px;font-size:11px;color:#8fa7c7;letter-spacing:1px}

.rank-list{display:flex;flex-direction:column;gap:8px}
.rank-item{display:grid;grid-template-columns:24px 1fr auto;gap:8px;align-items:center;font-size:12px}
.rank-no{background:#102442;border-radius:6px;text-align:center;padding:2px 0;color:#b2c6e6}
.rank-name{color:#e1efff}
.rank-val{color:#7ef0ff}

@media (max-width: 1200px){
  .dash-grid{grid-template-columns:1fr}
  .map-panel{height:auto}
  .nav-grid{grid-template-columns:1fr}
  .kpi-row{grid-template-columns:1fr}
}
</style>
