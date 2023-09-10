package com.ruoyi.seckill.api.factory;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.seckill.api.RemoteSeckillAlipayService;
import com.ruoyi.seckill.api.model.AlipayVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * 阿里支付服务降级处理
 *
 * @author Mike
 */
@Component
public class RemoteSeckillAlipayFallbackFactory implements FallbackFactory<RemoteSeckillAlipayService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteSeckillAlipayFallbackFactory.class);

    @Override
    public RemoteSeckillAlipayService create(Throwable throwable) {
        return new RemoteSeckillAlipayService() {

            @Override
            public R<String> pay(AlipayVo payVo) {
                return R.fail("支付失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> rsaCheckV1(Map<String, String> params) {
                return R.fail("参数校验失败:" + throwable.getMessage());

            }
        };
    }
}
