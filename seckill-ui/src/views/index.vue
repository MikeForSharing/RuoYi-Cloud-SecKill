<template>
  <div class="app-container home">


    <el-container>
      <el-header>
        <!--logo图片-->
        <el-row>
          <el-col :span="24">
            <div style="width: 100%;height: 40px;">
              <img src="@/assets/images/seckill/seckill_title.jpg" style="display: block; width: 100%;height: 135px;"/>
            </div>
          </el-col>
        </el-row>
      </el-header>
      <el-main>
        <!--三个场次-->

        <div style="width: 100%;border: 1px solid red">
          <ul>

            <template v-for="item in productList">
              <li class="commodity-data">
                <div class="commodity-data-flex">
                  <div class="commodityImg">
                    <img :src="require ('@/assets/images/seckill'+item.productImg)" alt="" class="moreGood-img">
                  </div>
                  <div class="textParent">
                    <p class="commodity-text">{{item.productName}}</p>
                    <div class="seckill-tag">
                      <p class="seckill-tag-text">{{item.productDetail}}</p>
                    </div>
                  </div>
                  <div class="seckill-operation">
                    <div class="seckill-good-cur">
                      <div class="seckill-money-num">
                        <div class="seckill-money-status">
                          <h2 class="seckill-number"><span class="seckill-num">￥</span>{{item.seckillPrice}}</h2>
                          <span class="old-number">￥<del>{{item.productPrice}}</del></span>
                        </div>
                        <!--<div class="seckill-shop-number">-->
                          <!--<div class="seckill-start-status">-->
                            <!--<span class="comeback-num">已售({{(item.stockCount-item.currentCount)*100/item.stockCount}}%</span>-->
                            <!--<div class="shop-number-status"><i class="current-progress" :style="'width:'+((item.stockCount-item.currentCount)*100/item.stockCount)+'px'"></i></div>-->
                          <!--</div>-->
                        <!--</div>-->
                      </div>
                      <div>
                        <!--<el-button @click="goDetail()" :time="time" :seckillId="seckillId">立即抢购</el-button>-->
                        <router-link :to="{path:'/seckill/productDetail/', query:{item}}" class="link-type">
                          <span>立即抢购</span>
                        </router-link>
                      </div>
                    </div>
                  </div>
                </div>
              </li>

            </template>
          </ul>

        </div>


      </el-main>
    </el-container>


  </div>
</template>

<script>
  import {seckillProductList} from "@/api/seckill/seckillProduct";

  export default {
    name: "Index",
    data() {
      return {
        test:'aaa',
        queryParams: {
          time: 10 //秒杀场次
        },
        productList: []
      };
    },
    mounted() {
      this.getList();
    },
    methods: {

      //进入秒杀详情页面
      goDetail(){

      },

      // 秒杀方法
      secKillProduct() {
      },

      // 获取秒杀商品列表
      getList() {
        seckillProductList(this.queryParams).then(response => {
            this.productList = response.data;
            // console.log('getList', response);
          }
        );
      }
    }

  }
  ;
</script>

<style scoped lang="scss">

  .el-main{
    top: 70px;
    position: relative;
  }
  .commodity-data {
    width: 292px;
    margin-top: 10px;
    /* padding-bottom: 32px; */
    /* box-sizing: border-box; */
    position: relative;
    /* float: left; */
    /* display: flex; */
    /* flex-direction: column; */
    /* justify-content: center; */
    /* align-items: center; */
    display: inline-block;
    /* padding-right: 16px; */
    /* box-sizing: border-box; */
    /* background: #fff; */
    margin-right: 10px;
    cursor: pointer;
    transition: all .8s ease;
  }

  .commodity-data:nth-of-type(4n) {
    margin-right: 0;
  }

  .commodity-data-flex {
    display: flex;
    flex-direction: column;
    justify-content: start;
    /* align-items: center; */
    width: 284px;
    height: 440px;
    padding-bottom: 10px;
    box-sizing: border-box;
    background: #fff;
  }

  .commodityImg {
    position: relative;
    width: 100%;
    height: 270px;
    display: flex;
    flex-direction: column;
    -webkit-print-color-adjust: exact;
    justify-content: center;
  }

  .commodity-data-flex .moreGood-img {
    width: auto;
    height: 200px;
    margin: 0 auto;
    display: block;
    transition: all ease .8s;
  }

  .commodity-data-flex .moreGood-img:hover {
    transform: translateY(-20px);
  }

  .textParent {
    /*height: 88px;*/
    /*display: flex;*/
    /*flex-direction: row;*/
    /*align-items: center;*/
    padding: 0 15px 10px;
    box-sizing: border-box;
    border-bottom: 1px solid #ececec;
  }

  .commodity-text {
    font-size: 14px;
    font-weight: normal;
    font-stretch: normal;
    line-height: 23px;
    letter-spacing: 0px;
    color: #333333;
    padding: 10px 0 8px;
    box-sizing: border-box;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  .seckill-tag {
    display: flex;
    flex-direction: row;
    align-items: center;
  }

  .seckill-tag-text {
    font-size: 14px;
    height: 21px;
    line-height: 21px;
    color: #e01222;
    text-align: left;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    padding-left: 5px;
    box-sizing: border-box;
  }

  .seckill-operation {
    padding: 10px 0 10px 15px;
    box-sizing: border-box;
    height: 91px;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
  }

  .seckill-good-cur {
    display: flex;
    flex-direction: row;
    align-items: center;
    padding-top: 10px;
    box-sizing: border-box;
  }

  .seckill-money-num {
    flex: 1;
  }

  .seckill-money-status {

  }

  .seckill-number {
    font-size: 24px;
    color: #e01222;
    font-family: arial;
    margin-right: 2px;
    line-height: 1;
    display: inline-block;
    min-width: 50px;
  }

  .seckill-num {
    font-size: 14px;
  }

  .old-number {
    font-size: 12px;
    color: #999;
  }

  .seckill-shop-number {
    display: block;
    margin-top: 5px;
  }

  .comeback-num {
    font-size: 12px;
    color: #999;
  }

  .shop-number-status {
    width: 88px;
    height: 8px;
    background: #e6e6e6;
    display: inline-block;
    position: relative;
    overflow: hidden;
    margin-left: 5px;
    -moz-border-radius: 8px;
    border-radius: 8px;
  }

  .current-progress {
    background: #df0021;
    position: absolute;
    left: 0;
    top: 0;
    height: 8px;
    -moz-border-radius: 8px 0 0 8px;
    border-radius: 8px 0 0 8px;
  }

  .operation-button {
    color: #fff;
    height: 40px;
    line-height: 40px;
    font-size: 16px;
    display: block;
    margin: auto 0;
    width: 80px;
    text-align: center;
    background: #df0021;
    cursor: pointer;
  }

  .operation-button:hover, .operation-button:focus {
    color: #fff;
  }
</style>

