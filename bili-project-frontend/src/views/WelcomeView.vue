<script setup>

</script>

<template>
  <div class="box">
    <div class="left-card">

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
</style>
