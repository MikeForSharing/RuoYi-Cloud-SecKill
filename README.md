# RuoYi-Cloud-SecKill
高并发秒杀系统

#### 介绍
本项目基于若依RuoYi-Cloud的RuoYi-Cloud-SecKill微服务项目，后端采用Spring Boot、Spring Cloud & Alibaba、MyBatis，前端采用Vue+ElementUI，通过创建基于Redis、RabbitMQ、WebSocket、ElasticJob的技术方案，设计实现高并发秒杀系统架构。

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
![秒杀流程图](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/assets/20550776/35acb399-2d30-4ee9-bd28-37c263b477f9)
  
#### 扩展功能：
  商品秒杀：实现高并发的秒杀校验、秒杀预处理、创建订单、库存回补等功能。  
  商品管理：实现商品信息查询等功能。  
  分布式任务管理：实现任务的分片执行和高可用。   
  通信管理：实现前后端通信功能。   
  后续功能持续更新中...   
    
#### 内置功能
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
  
 ![企业微信截图_16908578775992](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/assets/20550776/2652d3b6-b169-4ddb-9300-168c0570bc9c)
   
商品详情界面：  
  
![企业微信截图_16908579112877](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/assets/20550776/72272ad2-4fb2-4449-8995-2f8f39d94caa)
  
秒杀结果界面：  
  
  ![企业微信截图_16908579007626](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/assets/20550776/471c0bcc-0020-4871-9222-fd6a18885e1b)
    
#### 性能测试

1.  秒杀操作前，数据库秒杀商品表中库存数量为10:
  
    ![企业微信截图_16908569879684](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/assets/20550776/13c6fc0e-cf98-401c-ab9e-36f4c53108ba)  

2.  机器环境：
     
    ![企业微信截图_16908570211309](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/assets/20550776/3d62af54-3321-4231-a57c-ce48a3984c25)  

3.  模拟并发量：
   
    ![image](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/assets/20550776/bb98753f-5f13-4de0-a8ea-91a3e03a528e)

4.  测试结果:
  
    ![image](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/assets/20550776/2e5f49a0-45e7-4205-a8a2-d9ceb8c59357)

5.  秒杀操作完成后，数据库秒杀商品表中库存数量为0，没有出现超卖的情况：
     
    ![image](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/assets/20550776/1d7b5960-3d8c-43a7-a71a-191e3101009e)

6.  秒杀操作完成后，数据库秒杀订单表中新增订单数量为10：
      
    ![企业微信截图_16908571818483](https://github.com/MikeForSharing/RuoYi-Cloud-SecKill/assets/20550776/cce437a8-1113-45f2-b318-66d751954df0)
    
#### 交流
若发现bug，请issues，并提供复现路径。  
  
#### 致谢  
[感谢若依开源的RuoYi-Cloud项目](https://gitee.com/y_project/RuoYi-Cloud)  
[感谢陈天狼老师的系统高并发架构资料](https://space.bilibili.com/98307693)


