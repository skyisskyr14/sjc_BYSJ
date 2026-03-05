<template><el-card><div slot="header">调度任务（MVP最小页）</div><el-empty v-if="!list.length" description="暂无任务"/><el-table v-else :data="list"><el-table-column prop="taskNo" label="任务号"/><el-table-column prop="status" label="状态"/><el-table-column prop="demandPoint" label="需求点"/></el-table></el-card></template>
<script>
import { taskList } from '@/api/dispatch'
export default { data:()=>({list:[]}), async mounted(){const r=await taskList(); this.list=r.data || []} }
</script>
