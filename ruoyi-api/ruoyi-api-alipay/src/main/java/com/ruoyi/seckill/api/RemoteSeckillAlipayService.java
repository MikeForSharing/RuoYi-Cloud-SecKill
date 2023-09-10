package com.ruoyi.seckill.api;

import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.seckill.api.factory.RemoteSeckillAlipayFallbackFactory;
import com.ruoyi.seckill.api.model.AlipayVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Author: zhangJiang
 */
@FeignClient(contextId = "remoteAlipayService", value = ServiceNameConstants.ALIPAY_SERVICE, fallbackFactory = RemoteSeckillAlipayFallbackFactory.class)
public interface RemoteSeckillAlipayService {

    @GetMapping("/alipay/pay")
    R<String> pay(@RequestBody AlipayVo payVo);
    @GetMapping("/alipay/rsaCheckV1")
    R<Boolean> rsaCheckV1(@RequestParam Map<String, String> params);

}
