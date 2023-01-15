package com.ruoyi.seckill.controller;

import com.ruoyi.common.core.constant.RabbitConstants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.rabbit.api.RemoteRabbitService;
import com.ruoyi.seckill.api.model.OrderInfo;
import com.ruoyi.seckill.api.model.OrderMessage;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import com.ruoyi.seckill.enums.SeckillRedisKey;
import com.ruoyi.seckill.service.ISeckillOrderService;
import com.ruoyi.seckill.service.ISeckillProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import com.alibaba.fastjson.JSON;

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

    @Autowired
    private RedisService redisService;

    @Autowired
    private RemoteRabbitService remoteRabbitService;

    /**
     * 秒杀接口
     * @param time 场次
     * @param seckillId 秒杀id
     * @param request
     * @return
     */
    @RequestMapping("/doSeckill")
    public R<String> doSeckill(String time, Long seckillId, HttpServletRequest request){
        Date now = new Date();
        SeckillProductVo seckillProductVo = seckillProductService.find(time,seckillId);
        //判断时间
        if(now.getTime() < seckillProductVo.getStartDate().getTime()){
            return R.fail("秒杀活动未开始，异常操作！");
        }
//        String token = request.getHeader(CommonConstants.TOKEN_NAME);
//        String phone = UserUtil.getUserPhone(redisTemplate,token);
        Long userId = SecurityUtils.getUserId();
        String token = SecurityUtils.getToken();

        //判断是否重复下单
        String orderSetKey = SeckillRedisKey.SECKILL_ORDER_SET.getRealKey(String.valueOf(seckillId));
        if(redisService.isMemberSet(orderSetKey,String.valueOf(userId))){
            return R.fail("请不用重复下单！");
        }
        //判断库存
        String countKey = SeckillRedisKey.SECKILL_STOCK_COUNT_HASH.getRealKey(time);
        Long remainCount = redisService.incrementCacheMapValue(countKey, String.valueOf(seckillId), -1);
        if(remainCount<0){
            return R.fail("商品已抢光！");
        }
        OrderMessage message = new OrderMessage(Integer.parseInt(time),seckillId,token,userId);
        remoteRabbitService.triggerSend(JSON.toJSONString(message), RabbitConstants.NORMAL_TYPE, SecurityConstants.INNER);
        return R.ok("进入抢购队列,请等待结果");
    }

    @RequestMapping("/cancelOrder")
    public R<Boolean> cancelOrder(String orderNo){
        Boolean res = seckillOrderService.cancelOrder(orderNo);
        return R.ok(res);
    }

    //获取订单详情
    @RequestMapping("/find")
    public R<OrderInfo> find(String orderNo, HttpServletRequest request){
        String token = SecurityUtils.getToken();
        long userId = SecurityUtils.getUserId();
        OrderInfo orderInfo = seckillOrderService.getOrderInfoByOrderNo(orderNo);
        if(orderInfo==null){
            return R.fail("非法操作，订单信息为空！");
        }
        if(!String.valueOf(userId).equals(String.valueOf(orderInfo.getUserId()))){
            return R.fail("非法操作，用户与订单用户不符！");
        }
        return R.ok(orderInfo);
    }

}
