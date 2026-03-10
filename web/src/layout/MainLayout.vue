<template>
  <el-container class="shell">
    <el-main class="main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script>
export default {
}
</script>

<style scoped>
.shell{
  min-height:100vh;
  background:#060b16;
}
.main{padding:0;background:#060b16}
</style>
