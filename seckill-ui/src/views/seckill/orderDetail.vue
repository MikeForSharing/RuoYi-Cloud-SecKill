<template>
  <div class="app-container home">

    <el-row>
      <el-col :span="24">
        <div>商品名称</div>
        <div>{{orderInfoData.productName}}</div>
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="8">
        <div>商品价格</div>
      </el-col>
      <el-col :span="8">
        <div>秒杀价格</div>
        <div>{{orderInfoData.seckillPrice}}</div>
      </el-col>
      <el-col :span="8">
        <div>商品积分</div>
        <div>{{orderInfoData.intergral}}</div>
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="24">
        <div>下单时间</div>
        <div>{{orderInfoData.createDate}}</div>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <div>订单状态</div>
        <div>{{orderInfoData.status}}</div>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="8">
        <div>支付类型</div>
      </el-col>
      <el-col :span="8">
        <el-radio v-model="payType" label="0">在线支付</el-radio>
      </el-col>
      <el-col :span="8">
        <el-radio v-model="payType" label="1">积分支付</el-radio>
      </el-col>
    </el-row>
    <el-row>
      <!--未付款-->
      <div v-show="orderInfoData.status==0">
        <el-col :span="8">
          <div @click="clickPayOrder">立刻支付</div>
        </el-col>
        <!--<el-col :span="8">-->
        <!--<div>取消订单</div>-->
        <!--</el-col>-->
      </div>
      <!--已付款-->
      <div v-show="orderInfoData.status==1">
        <el-col :span="8">
          <div @click="clickRefund">申请退款</div>
        </el-col>
      </div>

    </el-row>
  </div>
</template>

<script>
  import {getorderInfo} from "@/api/seckill/seckillOrder";
  import {payOrder} from "@/api/seckill/seckillPay";

  export default {
    name: "orderDetail",
    data() {
      return {
        orderNo: "",//订单编号
        payType: "",//支付类型
        orderInfoData: {},//订单信息
      };
    },
    created() {
      this.orderNo = this.$route.params && this.$route.params.orderNo;
      getorderInfo({"orderNo": orderNo}).then(response => {
        this.orderInfoData = response.data;
      });
    },
    methods: {
      clickPayOrder() {
        payOrder({"orderNo": orderNo, "payType": payType}).then(response => {
          this.orderInfoData = response.data;
        });
      },
      clickRefund() {

      }
    }

  }
  ;
</script>

<style scoped lang="scss">
  .el-row {
    margin-bottom: 20px;

  &
  :last-child {
    margin-bottom: 0;
  }

  }
  .el-col {
    border-radius: 4px;
  }
</style>

