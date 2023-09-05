package com.ruoyi.alipay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.ruoyi.alipay.config.AlipayProperties;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.security.annotation.InnerAuth;
import com.ruoyi.seckill.api.model.AlipayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 秒杀商品
 *
 * @Author: Mike
 */
@RestController
@RequestMapping("/alipay" )
public class AlipayController extends BaseController {

    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private AlipayProperties alipayProperties;

    @RequestMapping("/pay")
    R<String> pay(@RequestBody AlipayVo alipayVo) throws AlipayApiException {
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(alipayVo.getReturnUrl());
        alipayRequest.setNotifyUrl(alipayVo.getNotifyUrl());
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ alipayVo.getOutTradeNo() +"\","
                + "\"total_amount\":\""+ alipayVo.getTotalAmount() +"\","
                + "\"subject\":\""+ alipayVo.getSubject() +"\","
                + "\"body\":\""+ alipayVo.getBody() +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        return R.ok(result);
    }
    @RequestMapping("/rsaCheckV1")
    public R<Boolean> rsaCheckV1(@RequestParam Map<String, String> params) throws AlipayApiException {
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayProperties.getAlipayPublicKey(), alipayProperties.getCharset(), alipayProperties.getSignType()); //调用SDK验证签名
        return R.ok(signVerified);
    }

    @GetMapping("/test" )
    public R<List<String>> test() {
        return R.ok();
    }
}
