<template>
  <div>
    <el-button v-if="canWrite" size="mini" type="success" @click="open('in')">入库</el-button>
    <el-button v-if="canWrite" size="mini" type="warning" @click="open('out')">出库</el-button>
    <el-table :data="list" style="margin-top:10px">
      <el-table-column prop="warehouseName" label="仓库"/>
      <el-table-column prop="materialName" label="物资"/>
      <el-table-column prop="qtyAvailable" label="可用库存"/>
      <el-table-column prop="warnThreshold" label="阈值"/>
    </el-table>
    <el-dialog :visible.sync="visible" :title="mode==='in'?'入库':'出库'">
      <el-form :model="form">
        <el-form-item label="仓库ID"><el-input-number v-model="form.warehouseId"/></el-form-item>
        <el-form-item label="物资ID"><el-input-number v-model="form.materialId"/></el-form-item>
        <el-form-item label="数量"><el-input-number v-model="form.qty"/></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark"/></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="submit">提交</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { inventoryPage, inbound, outbound } from '@/api/inventory'

export default {
  props: {
    canWrite: { type: Boolean, default: false }
  },
  data: () => ({ query: { pageNum: 1, pageSize: 10 }, list: [], visible: false, mode: 'in', form: { qty: 1 } }),
  mounted() { this.load() },
  methods: {
    async load() {
      const r = await inventoryPage(this.query)
      this.list = r.data.list
    },
    open(m) {
      if (!this.canWrite) return this.$message.warning('当前身份无操作权限')
      this.mode = m
      this.visible = true
    },
    async submit() {
      if (!this.canWrite) return this.$message.warning('当前身份无操作权限')
      if (this.mode === 'in') await inbound(this.form)
      else await outbound(this.form)
      this.visible = false
      this.load()
      this.$message.success('操作成功')
    }
  }
}
</script>
