<template>
  <el-container style="min-height:100vh">
    <el-aside width="220px" class="aside">
      <h3>应急物资系统</h3>
      <el-menu :router="true" background-color="#0b1f3a" text-color="#dbe8ff" active-text-color="#67c23a">
        <el-menu-item index="/dashboard">总览</el-menu-item>
        <el-menu-item index="/inventory/list">物资台账</el-menu-item>
        <el-menu-item index="/alert/list">预警中心</el-menu-item>
        <el-menu-item index="/task/list">调度任务</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">基于Flink的常规应急物资管理系统（MVP）</el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>
<style scoped>
.aside{background:#0b1f3a;color:#fff;padding:12px}
.header{background:#fff;font-weight:700;box-shadow:0 2px 8px rgba(0,0,0,.06)}
</style>
