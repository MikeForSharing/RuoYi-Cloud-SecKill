package com.ruoyi.seckill.api.factory;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.seckill.api.RemoteSeckillOrderService;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 秒杀订单服务降级处理
 *
 * @author Mike
 */
@Component
public class RemoteSeckillOrderFallbackFactory implements FallbackFactory<RemoteSeckillOrderService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteSeckillOrderFallbackFactory.class);

    @Override
    public RemoteSeckillOrderService create(Throwable throwable) {
        return new RemoteSeckillOrderService() {
            @Override
            public R<String> cancelOrder(String orderNo, String source) {
                return R.fail("取消订单操作失败:" + throwable.getMessage());
            }

            @Override
            public R<String> doSeckill(String userId, SeckillProductVo vo, String source) {
                return R.fail("执行秒杀操作失败:" + throwable.getMessage());
            }
        };
    }
}
