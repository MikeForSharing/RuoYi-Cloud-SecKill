<template>
  <div class="app-container home">

    <el-row>
      <el-col :span="24">
        <div> 商品名称</div>
        <div></div>
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="24">
        <div>商品价格</div>
        <div></div>
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="24">
        <div>下单时间</div>
        <div></div>
      </el-col>
    </el-row>
  </div>
</template>

<script>

  export default {
    name: "orderDetail",
    data() {
      return {
        queryParams: {
          orderNo: '',     //订单编号
        },
      };
    },
    created() {
      this.queryParams.orderNo = this.$route.params && this.$route.params.orderNo;
    },
    methods: {

    }

  }
  ;
</script>

<style scoped lang="scss">
  .el-row {
    margin-bottom: 20px;
    &:last-child {
       margin-bottom: 0;
     }
  }
  .el-col {
    border-radius: 4px;
  }
</style>

