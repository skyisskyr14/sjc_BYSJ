<template><div>
  <el-tabs v-model="tab">
    <el-tab-pane label="物资字典" name="m"><MaterialTab/></el-tab-pane>
    <el-tab-pane label="库存" name="i"><InventoryTab/></el-tab-pane>
    <el-tab-pane label="流水" name="f"><FlowTab/></el-tab-pane>
  </el-tabs>
</div></template>
<script>
import MaterialTab from './tabs/MaterialTab.vue'
import InventoryTab from './tabs/InventoryTab.vue'
import FlowTab from './tabs/FlowTab.vue'
export default { components: { MaterialTab, InventoryTab, FlowTab }, data:()=>({tab:'m'}) }
</script>
