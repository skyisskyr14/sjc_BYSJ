<template>
  <el-result icon="warning" title="403" subTitle="无权限访问该页面">
    <template slot="extra"><el-button type="primary" @click="$router.push('/dashboard')">返回首页</el-button></template>
  </el-result>
</template>
