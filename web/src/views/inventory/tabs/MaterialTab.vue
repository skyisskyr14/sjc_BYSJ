<template>
  <div>
    <el-button v-if="canWrite" type="primary" size="mini" @click="open()">新增物资</el-button>
    <el-table :data="list" style="margin-top:10px">
      <el-table-column prop="materialName" label="名称"/>
      <el-table-column prop="materialType" label="类型"/>
      <el-table-column prop="warnThreshold" label="阈值"/>
      <el-table-column label="操作" width="140">
        <template slot-scope="s">
          <el-button v-if="canWrite" size="mini" @click="open(s.row)">编辑</el-button>
          <el-button v-if="canWrite" size="mini" type="danger" @click="del(s.row)">删除</el-button>
          <span v-if="!canWrite" class="readonly-cell">只读</span>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @current-change="p=>{query.pageNum=p;load()}"
      :current-page="query.pageNum"
      :page-size="query.pageSize"
      layout="total,prev,pager,next"
      :total="total"/>
    <el-dialog :visible.sync="visible" title="物资">
      <el-form :model="form">
        <el-form-item label="名称"><el-input v-model="form.materialName"/></el-form-item>
        <el-form-item label="类型"><el-input v-model="form.materialType"/></el-form-item>
        <el-form-item label="单位"><el-input v-model="form.unit"/></el-form-item>
        <el-form-item label="阈值"><el-input-number v-model="form.warnThreshold"/></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { materialPage, materialCreate, materialUpdate, materialDelete } from '@/api/material'

export default {
  props: {
    canWrite: { type: Boolean, default: false }
  },
  data: () => ({ query: { pageNum: 1, pageSize: 10 }, list: [], total: 0, visible: false, form: {} }),
  mounted() { this.load() },
  methods: {
    async load() {
      const r = await materialPage(this.query)
      this.list = r.data.list
      this.total = r.data.total
    },
    open(row) {
      if (!this.canWrite) return this.$message.warning('当前身份无操作权限')
      this.form = row ? { ...row } : { warnThreshold: 0 }
      this.visible = true
    },
    async save() {
      if (!this.canWrite) return this.$message.warning('当前身份无操作权限')
      if (this.form.id) await materialUpdate(this.form)
      else await materialCreate(this.form)
      this.visible = false
      this.load()
      this.$message.success('成功')
    },
    async del(r) {
      if (!this.canWrite) return this.$message.warning('当前身份无操作权限')
      await materialDelete(r.id)
      this.load()
      this.$message.success('已删除')
    }
  }
}
</script>

<style scoped>
.readonly-cell{font-size:12px;color:#9fb8db}
</style>
