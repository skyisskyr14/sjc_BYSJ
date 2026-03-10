<template>
  <div>
    <el-table :data="list">
      <el-table-column prop="bizNo" label="业务号"/>
      <el-table-column prop="warehouseName" label="仓库"/>
      <el-table-column prop="materialName" label="物资"/>
      <el-table-column prop="flowType" label="类型"/>
      <el-table-column prop="qty" label="数量"/>
      <el-table-column prop="createTime" label="时间"/>
    </el-table>
  </div>
</template>
<script>
import { flowPage } from '@/api/inventory'
export default { data:()=>({list:[]}), async mounted(){const r=await flowPage({pageNum:1,pageSize:20}); this.list=r.data.list} }
</script>
