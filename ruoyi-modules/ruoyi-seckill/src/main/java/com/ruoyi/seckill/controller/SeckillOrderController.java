package com.ruoyi.seckill.controller;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.constant.RabbitConstants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.rabbitmq.OrderMessage;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.rabbit.api.RemoteRabbitService;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import com.ruoyi.seckill.enums.SeckillRedisKey;
import com.ruoyi.seckill.service.ISeckillOrderService;
import com.ruoyi.seckill.service.ISeckillProductService;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 秒杀订单接口
 *
 * @Author: Mike
 */
@RestController
@RequestMapping("/seckillOrder" )
public class SeckillOrderController {

    @Autowired
    private ISeckillProductService seckillProductService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private RemoteRabbitService remoteRabbitService;

    @Autowired
    private RedisService redisService;

    /**
     * 秒杀接口
     *
     * @param time      场次
     * @param seckillId 秒杀商品id
     * @return
     */
    @GetMapping("/doSeckill" )
    public R<String> doSeckill(Integer time, Long seckillId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source) {
        Date now = new Date();
        SeckillProductVo seckillProductVo = seckillProductService.findFromCache(time, seckillId);
        //判断时间
        if (now.getTime() < seckillProductVo.getStartDate().getTime()) {
            return R.fail("秒杀活动未开始，异常操作！" );
        }
//        String token = request.getHeader(CommonConstants.TOKEN_NAME);
//        String phone = UserUtil.getUserPhone(redisTemplate,token);
        Long userId = SecurityUtils.getUserId();
        String token = SecurityUtils.getToken();

        //判断是否重复下单
        //
        String orderSetKey = SeckillRedisKey.SECKILL_ORDER_SET.getRealKey(String.valueOf(seckillId));
        if (redisService.isMemberSet(orderSetKey, String.valueOf(userId))) {
            return R.fail("请不用重复下单！" );
        }
        //利用redis原子性递减判断库存，保证大多数请求提前被拦截(redis中库存是限制人数用的，从而减少对数据库访问，若有两个同样的请求通过redis，在后台拦截后，redis就再放一个请求)
        String countKey = SeckillRedisKey.SECKILL_STOCK_COUNT_HASH.getRealKey(String.valueOf(time));
        Long remainCount = redisService.incrementCacheMapValue(countKey, String.valueOf(seckillId), -1);
        if (remainCount < 0) {
            return R.fail("商品已抢光！" );
        }

        OrderMessage message = new OrderMessage(Integer.parseInt(String.valueOf(time)),seckillId,token,userId);
        remoteRabbitService.triggerSend(JSON.toJSONString(message), SecurityConstants.INNER);
        return R.ok("进入抢购队列,请等待结果" );
    }

    /**
     * 取消订单
     * @param orderNo
     * @return
     */
    @GetMapping("/cancelOrder" )
    public R<String> cancelOrder(String orderNo, @RequestHeader(SecurityConstants.FROM_SOURCE) String source) {
        int res = seckillOrderService.cancelOrder(orderNo);
        if (res==-1){
            return R.fail("取消订单失败");
        }
        return R.ok("取消订单成功");
    }
}
