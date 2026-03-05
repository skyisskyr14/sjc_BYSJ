<template><div>
  <el-table :data="list"><el-table-column prop="alertType" label="类型"/><el-table-column prop="alertLevel" label="级别"/><el-table-column prop="status" label="状态"><template slot-scope="s"><el-tag :type="s.row.status==='UNCONFIRMED'?'danger':'success'">{{s.row.status}}</el-tag></template></el-table-column><el-table-column prop="alertMessage" label="消息"/><el-table-column label="操作"><template slot-scope="s"><el-button v-if="s.row.status==='UNCONFIRMED'" size="mini" @click="ack(s.row)">确认</el-button></template></el-table-column></el-table>
</div></template>
<script>
import { alertPage, alertAck } from '@/api/alert'
export default { data:()=>({list:[]}), mounted(){this.load()}, methods:{async load(){const r=await alertPage({pageNum:1,pageSize:50}); this.list=r.data.list}, async ack(r){await alertAck({id:r.id}); this.load(); this.$message.success('已确认')}} }
</script>
