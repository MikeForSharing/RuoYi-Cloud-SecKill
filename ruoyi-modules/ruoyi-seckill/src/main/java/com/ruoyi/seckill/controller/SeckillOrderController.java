package com.ruoyi.seckill.controller;

import com.ruoyi.common.core.constant.RabbitConstants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.rabbitmq.OrderMessage;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import com.ruoyi.seckill.enums.SeckillRedisKey;
import com.ruoyi.seckill.service.ISeckillProductService;
import com.ruoyi.system.api.RemoteRabbitService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

    @Autowired
    private ISeckillProductService seckillProductService;


    @Autowired
    private RemoteRabbitService remoteRabbitService;

    @Autowired
    private RedisService redisService;

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
//        remoteRabbitService.triggerSend(JSON.toJSONString(message), RabbitConstants.NORMAL_TYPE, SecurityConstants.INNER);
        return R.ok("进入抢购队列,请等待结果");
    }

}
