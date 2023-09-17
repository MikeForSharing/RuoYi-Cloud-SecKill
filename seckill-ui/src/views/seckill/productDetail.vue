<template>
  <div class="app-container ">

    <el-row>
      <el-col span=4>
        <div class="itemDiv">
          <img :src="require ('@/assets/images/seckill'+item.productImg)" alt="" class="moreGood-img">
        </div>
      </el-col>
      <el-col span=8 style="top:10px;position:relative;">
        <div class="itemDiv">商品名称：{{item.productName}}</div>
        <div class="itemDiv">商品描述：{{item.productDetail}}</div>
        <div class="itemDiv">商品原价：{{item.productPrice}}</div>
        <div class="itemDiv">秒杀价格：{{item.seckillPrice}}</div>
        <div class="buyButtonDiv">
          <el-button style="font-size: 18px;font-weight: bold;color: black;" @click="buy()">立即抢购</el-button>
        </div>
      </el-col>
    </el-row>


  </div>
</template>

<script>
  import {intoSeckillQueue} from "@/api/seckill/seckillOrder";

  export default {
    name: "productDetail",
    data() {
      return {
        curProductData: {},
        socket: '',
        queryParams: {
          time: 10,     //秒杀场次
          seckillId: 9,  //秒杀商品id
        },
        item: {}
      };
    },
    created() {
      this.item = this.$route.query && this.$route.query.item;
      this.queryParams.time = this.item.time;
      this.queryParams.seckillId = this.item.id;
    },
    methods: {
      buy() {
        intoSeckillQueue(this.queryParams).then(response => {
            this.curProductData = response.data;
            if (!this.socket) {
              this.createScoket('111');
            }
          }
        );
      },
      createScoket(token) {
        if (typeof (WebSocket) == "undefined") {
          console.log("浏览器不支持WebSocket");
        } else {
          var host = window.location.origin.replace("http:", "ws:")
          //实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接
          this.socket = new WebSocket(host + ":9207/websocket/" + token);
          //打开事件
          this.socket.onopen = function () {
            console.log("Socket 已打开");
            //this.socket.send("这是来自客户端的消息" + location.href + new Date());
          };
          //获得消息事件
          this.socket.onmessage = function (result) {
            // debugger
            var data = JSON.parse(result.data);
            if (data.orderNo) {
              alert("恭喜你，秒杀成功！");
              // this.$message({message: '喜你，秒杀成功！', type: 'success'})

              this.$router.push("/seckill/orderDetail/" + data.orderNo);
            } else {
              this.$message({message: '秒杀失败！', type: 'error'})
              // alert("秒杀失败！查看订单" + data.orderRes);
            }
          };

          //关闭事件
          this.socket.onclose = function () {
            console.log("Socket已关闭");
            this.socket.close();
          };
          //发生了错误事件
          this.socket.onerror = function () {
            console.log("Socket发生了错误");
          }
          //窗口关闭
          // $(window).unload(function (event) {
          //   this.socket.close();
          // });
        }
      }
    }

  }
  ;
</script>

<style scoped lang="scss">
  .app-container {
  }

  .buyButtonDiv {
    top: 10px;
    position: relative;
  }

  .itemDiv {
    font-family: "Microsoft YaHei";
    font-size: 20px;
    font-weight: bold;
    left: 5px;
    position: relative;
  }
</style>

