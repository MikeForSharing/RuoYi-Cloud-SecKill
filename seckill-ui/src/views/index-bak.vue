<template>
  <div class="app-container home">


    <el-container>
      <el-header>
        <!--logo图片-->
        <el-row>
          <el-col :span="24">
            <div class="grid-content bg-purple-dark"></div>
          </el-col>
        </el-row>
      </el-header>
      <el-main>
        <!--三个场次-->
        <el-row>
          <el-col :span="8">
            <div class="grid-content bg-purple"></div>
          </el-col>
          <el-col :span="8">
            <div class="grid-content bg-purple-light"></div>
          </el-col>
          <el-col :span="8">
            <div class="grid-content bg-purple"></div>
          </el-col>
        </el-row>


        <el-row>
          <el-col :span="8">
            <div class="grid-content bg-purple">商品1图片</div>
            <div class="grid-content bg-purple">秒杀按钮</div>
          </el-col>
          <el-col :span="8">
            <div class="grid-content bg-purple-light">商品2</div>
          </el-col>
          <el-col :span="8">
            <div class="grid-content bg-purple">商品3</div>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="8">
            <div class="grid-content bg-purple">商品4</div>
          </el-col>
          <el-col :span="8">
            <div class="grid-content bg-purple-light">商品5</div>
          </el-col>
          <el-col :span="8">
            <div class="grid-content bg-purple">商品6</div>
          </el-col>
        </el-row>

      </el-main>
    </el-container>


  </div>
</template>

<script>
  export default {
    name: "Index",
    data() {
      return {
        // 版本号
        version: "3.6.0",
      };
    },
    methods: {
      goTarget(href) {
        window.open(href, "_blank");
      },
    },
  };
</script>

<style scoped lang="scss">
  .home {

  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }

  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }

  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family:

  "open sans"
  ,
  "Helvetica Neue"
  ,
  Helvetica, Arial, sans-serif

  ;
  font-size:

  13
  px

  ;
  color: #676a6c

  ;
  overflow-x: hidden

  ;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

  b {
    font-weight: 700;
  }

  }

  .update-log {

  ol {
    display: block;
    list-style-type: decimal;
    margin-block-start: 1em;
    margin-block-end: 1em;
    margin-inline-start: 0;
    margin-inline-end: 0;
    padding-inline-start: 40px;
  }

  }
  }
</style>

