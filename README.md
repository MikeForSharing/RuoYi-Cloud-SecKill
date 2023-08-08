# RuoYi-Cloud-SecKill
高并发秒杀系统

#### 介绍
本项目是基于若依RuoYi-Cloud的高并发秒杀微服务项目(RuoYi-Cloud-SecKill)，后端采用Spring Boot、Spring Cloud & Alibaba、MyBatis，前端采用Vue+ElementUI，通过创建基于Redis、RabbitMQ、WebSocket、ElasticJob的技术方案，设计实现高并发秒杀系统架构。

#### 扩展特性
1.  定时清理用户信息：在Redis中对对登录用户进行排序，删除一星期内未登录用户信息，从而节省内存。
2.  预处理秒杀请求：通过分布式任务定时更新Redis中秒杀商品信息，进而利用Redis原子性递减判断库存量，保证大多数请求提前被拦截。
3.  预处理重复下单：通过Redis的Set集合判断是否重复下单。
4.  数据库唯一索引处理重复下单：通过将数据库表的“用户id+秒杀id”字段设定唯一索引，完全避免用户重复下单操作。
5.  预防商品库存超卖：使用乐观锁方式，在数据库层面避免出现超卖情况。
6.  异步下单：通过集成RabbitMQ中间件，使用异步方式进行下单操作，在消费者端实现削峰限流。
7.  延迟取消订单：通过RabbitMQ的延迟队列和死信队列，实现用户规定时间未支付，订单自动取消功能。
8.  避免消息重复消费：通过在Redis中存储消息消费记录，校验并避免消息重复消费。
9.  消息可靠投递：分别从消息生产者、交换机、消息队列、消费者四方面，保证消息的可靠投递。
10.  秒杀结果反馈：通过集成websocket建立长连接，及时准确通知用户秒杀结果。
  
#### 秒杀功能流程图
  ![image](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/blob/main/systemResPic/%E7%A7%92%E6%9D%80%E6%B5%81%E7%A8%8B%E5%9B%BE.jpg)

  
#### 扩展功能：
  商品秒杀：实现高并发的秒杀校验、秒杀预处理、创建订单、库存回补等功能。  
  商品管理：实现商品信息查询等功能。  
  分布式任务管理：实现任务的分片执行和高可用。   
  通信管理：实现前后端通信功能。   
  后续功能持续更新中...   
    
#### 内置功能（详细介绍，请点击[此处](https://gitee.com/y_project/RuoYi-Cloud)）
  用户管理：用户是系统操作者，该功能主要完成系统用户配置。  
  部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。  
  岗位管理：配置系统用户所属担任职务。  
  菜单管理：配置系统菜单，操作权限，按钮权限标识等。  
  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。  
  字典管理：对系统中经常使用的一些较为固定的数据进行维护。  
  参数管理：对系统动态配置常用参数。  
  通知公告：系统通知公告信息发布维护。  
  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。  
  登录日志：系统登录日志记录查询包含登录异常。  
  在线用户：当前系统中活跃用户状态监控。  
  定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。  
  代码生成：前后端代码的生成（java、html、xml、sql）支持CRUD下载 。  
  系统接口：根据业务代码自动生成相关的api接口文档。  
  服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。  
  缓存监控：对系统的缓存查询，删除、清空等操作。  
  在线构建器：拖动表单元素生成相应的HTML代码。  
  连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。  
  
#### 项目结构
~~~
com.ruoyi     
├── seckill-ui              // 前端框架 [80]
├── ruoyi-gateway         // 网关模块 [8080]
├── ruoyi-auth            // 认证中心 [9200]
├── ruoyi-api             // 接口模块
│       └── ruoyi-api-system                          // 系统接口
│       └── ruoyi-api-product                         // 商品接口
│       └── ruoyi-api-seckill                         // 秒杀接口
├── ruoyi-common          // 通用模块
│       └── ruoyi-common-core                         // 核心模块
│       └── ruoyi-common-datascope                    // 权限范围
│       └── ruoyi-common-datasource                   // 多数据源
│       └── ruoyi-common-log                          // 日志记录
│       └── ruoyi-common-redis                        // 缓存服务
│       └── ruoyi-common-security                     // 安全模块
│       └── ruoyi-common-swagger                      // 系统接口
│       └── ruoyi-common-rabbitmq                     // 消息中间接口
├── ruoyi-modules         // 业务模块
│       └── ruoyi-system                              // 系统模块 [9201]
│       └── ruoyi-gen                                 // 代码生成 [9202]
│       └── ruoyi-job                                 // 定时任务 [9203]
│       └── ruoyi-file                                // 文件服务 [9300]
│       └── ruoyi-elasticJob                          // 分布式任务 [9204]
│       └── ruoyi-product                             // 商品服务 [9206]
│       └── ruoyi-seckill                             // 秒杀服务 [9205]
│       └── ruoyi-websocket                           // 通信服务 [9207]  
├── ruoyi-visual          // 图形化管理模块
│       └── ruoyi-visual-monitor                      // 监控中心 [9100]
├──pom.xml                // 公共依赖
~~~
  
#### 演示  
商品列表界面：  
  
 ![image](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/blob/main/systemResPic/%E5%89%8D%E7%AB%AF-%E5%95%86%E5%93%81%E5%88%97%E8%A1%A8%E7%95%8C%E9%9D%A2.png)
   
商品详情界面：  
  
 ![image](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/blob/main/systemResPic/%E5%89%8D%E7%AB%AF-%E5%95%86%E5%93%81%E8%AF%A6%E6%83%85%E7%95%8C%E9%9D%A2.png)  
秒杀结果界面：  
  
 ![image](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/blob/main/systemResPic/%E5%89%8D%E7%AB%AF-%E7%A7%92%E6%9D%80%E7%BB%93%E6%9E%9C%E7%95%8C%E9%9D%A2.png)  
    
#### 性能测试

1.  秒杀操作前，数据库秒杀商品表中库存数量为10:
  
    ![image](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/blob/main/systemResPic/%E5%95%86%E5%93%81%E5%88%97%E8%A1%A8%E7%95%8C%E9%9D%A2.png)  

2.  机器环境：
     
    ![image](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/blob/main/systemResPic/%E6%9C%BA%E5%99%A8%E7%8E%AF%E5%A2%83.png)  

3.  模拟并发量：  
  
     ![image](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/blob/main/systemResPic/%E6%A8%A1%E6%8B%9F%E5%B9%B6%E5%8F%91%E9%87%8F.png)  
    
5.  测试结果:
      
    ![image](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/blob/main/systemResPic/%E6%B5%8B%E8%AF%95%E7%BB%93%E6%9E%9C.png)  
  
6.  秒杀操作完成后，数据库秒杀商品表中库存数量为0，没有出现超卖的情况：
     
    ![image](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/blob/main/systemResPic/%E7%A7%92%E6%9D%80%E6%93%8D%E4%BD%9C%E5%AE%8C%E6%88%90%E5%90%8E%E5%BA%93%E5%AD%98%E9%87%8F.png)  

7.  秒杀操作完成后，数据库秒杀订单表中新增订单数量为10：
      
    ![image](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/blob/main/systemResPic/%E7%A7%92%E6%9D%80%E6%93%8D%E4%BD%9C%E5%AE%8C%E6%88%90%E5%90%8E%E6%96%B0%E5%A2%9E%E8%AE%A2%E5%8D%95%E6%95%B0%E9%87%8F.png)  
    
#### 交流
若发现bug，请issues，并提供复现路径。  
  
#### 致谢  
[感谢若依开源的RuoYi-Cloud项目](https://gitee.com/y_project/RuoYi-Cloud)  
[感谢陈天狼老师的系统高并发架构资料](https://space.bilibili.com/98307693)


