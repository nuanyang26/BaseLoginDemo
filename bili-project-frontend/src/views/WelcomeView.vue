<script setup>

</script>

<template>
  <div class="box">
    <div class="left-card">
      <el-image fit="cover" src="/src/assets/img/20230401_WelComeImg.jpg"
                style="width: 100%;height: 100%;"></el-image>
    </div>

    <div class="welcome-title">
      <div style="font-size: 30px; font-weight: bold;">欢迎来到我们的学习平台</div>
      <div style="margin-top: 10px">在这里你可以学习如何使用Java，如何搭建网站，并且与Java之父密切交流</div>
      <div style="margin-top: 5px">在这里你可以同性交友，因为都是男的，没有学Java的女生</div>
    </div>

    <div class="right-card">

    </div>
  </div>
</template>

<style scoped>
.box {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  display: flex;
}

.left-card {
  flex: 1;
  background-color: black
}

.right-card {
  width: 400px;
  z-index: 1;
  background-color: aliceblue;
}

.welcome-title {
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 10px black;
}
</style>
