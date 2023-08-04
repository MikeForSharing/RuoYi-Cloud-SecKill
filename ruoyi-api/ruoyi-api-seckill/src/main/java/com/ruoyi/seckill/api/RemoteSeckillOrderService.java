package com.ruoyi.seckill.api;

import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.seckill.api.factory.RemoteSeckillOrderFallbackFactory;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: zhangJiang
 */
@FeignClient(contextId = "remoteOrderInfoService", value = ServiceNameConstants.SECKILL_SERVICE, fallbackFactory = RemoteSeckillOrderFallbackFactory.class)
public interface RemoteSeckillOrderService {

    /**
     * 取消订单信息
     *
     * @param orderNo 订单编号
     * @return
     */

    @GetMapping("/seckillOrder/cancelOrder")
    R<String> cancelOrder(@RequestParam("orderNo") String orderNo, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 秒杀商品操作
     * @param userId
     * @param vo
     * @return
     */
    @GetMapping("/seckillOrder/doSeckill")
    R<String> doSeckill(@RequestParam("userId") String userId, @RequestParam("seckillProductVo") SeckillProductVo vo, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
