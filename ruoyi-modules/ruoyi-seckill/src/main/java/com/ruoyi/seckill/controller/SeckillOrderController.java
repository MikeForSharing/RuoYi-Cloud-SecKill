package com.ruoyi.seckill.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.rabbitmq.server.RabbitmqService;
import com.ruoyi.common.redis.enums.SeckillRedisKey;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.seckill.api.model.OrderInfo;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import com.ruoyi.seckill.mq.OrderMessage;
import com.ruoyi.seckill.service.ISeckillOrderService;
import com.ruoyi.seckill.service.ISeckillProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

import static com.ruoyi.common.rabbitmq.config.RabbitMQConfiguration.*;

/**
 * 秒杀订单接口
 *
 * @Author: Mike
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

    @Autowired
    private ISeckillProductService seckillProductService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

//    @Autowired
//    private RemoteRabbitService remoteRabbitService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RabbitmqService rabbitmqService;

    /**
     * 进入秒杀队列接口
     *
     * @param time      秒杀场次
     * @param seckillId 秒杀商品id
     * @return
     */
    @GetMapping("/intoSeckillQueue")
    public R<String> intoSeckillQueue(Integer time, Long seckillId) {
        Date now = new Date();
        SeckillProductVo seckillProductVo = seckillProductService.findFromCache(time, seckillId);
        //判断时间
        if (now.getTime() < seckillProductVo.getStartDate().getTime()) {
            return R.fail("秒杀活动未开始，异常操作！");
        }
//        String token = request.getHeader(CommonConstants.TOKEN_NAME);
//        String phone = UserUtil.getUserPhone(redisTemplate,token);
        Long userId = SecurityUtils.getUserId();
//        String token = SecurityUtils.getToken();
        //FIXME
        String token = "tmpToken";

        //判断是否重复下单
        String orderSetKey = SeckillRedisKey.SECKILL_ORDER_SET.getRealKey(String.valueOf(seckillId));
        if (redisService.isMemberSet(orderSetKey, String.valueOf(userId))) {
            return R.fail("请不要重复下单！");
        }

        //利用redis原子性递减判断库存，保证大多数请求提前被拦截(redis中库存是限制人数用的，从而减少对数据库访问，若有两个同样的请求通过redis，在后台拦截后，redis就再放一个请求)
        String countKey = SeckillRedisKey.SECKILL_STOCK_COUNT_HASH.getRealKey(String.valueOf(time));
        Long remainCount = redisService.incrementCacheMapValue(countKey, String.valueOf(seckillId), -1);
        if (remainCount < 0) {
            return R.fail("商品已抢光！");
        }

        OrderMessage message = new OrderMessage(Integer.parseInt(String.valueOf(time)),seckillId,token,userId);
        System.out.println("message. " + JSONObject.toJSONString(message));
        rabbitmqService.rabbitSend(SECKILL_PRE_ORDER_EXCHANGE_NAME,SECKILL_PRE_ORDER_QUEUE_ROUTING_KEY,JSONObject.toJSONString(message));
        //        remoteRabbitService.triggerSend(JSON.toJSONString(message), SecurityConstants.INNER);

        return R.ok("进入抢购队列,请等待结果" );
    }

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     * @return
     */
    @GetMapping("/cancelOrder")
    public R<String> cancelOrder(String orderNo, @RequestHeader(SecurityConstants.FROM_SOURCE) String source) {
        int res = seckillOrderService.cancelOrder(orderNo);
        if (res == -1) {
            return R.fail("取消订单失败");
        }
        return R.ok("取消订单成功");
    }




    /**
     * 查询订单信息
     *
     * @param orderNo 订单编号
     * @return
     */
    @GetMapping("/getOrderInfo")
    public R<OrderInfo> getOrderInfo(String orderNo) {
        OrderInfo orderInfo = seckillOrderService.selectOrderById(orderNo);
        if (orderInfo == null) {
            return R.fail("获取订单信息失败");
        }
        return R.ok(orderInfo);
    }



}
