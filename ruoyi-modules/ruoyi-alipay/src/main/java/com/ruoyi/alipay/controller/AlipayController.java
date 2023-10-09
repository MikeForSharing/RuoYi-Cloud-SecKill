package com.ruoyi.alipay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.ruoyi.alipay.api.model.AlipayVo;
import com.ruoyi.alipay.api.model.RefundVo;
import com.ruoyi.alipay.config.AlipayProperties;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.security.annotation.InnerAuth;
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

    @RequestMapping("/refund")
    R<String> alirefund(@RequestBody RefundVo refundVo) throws AlipayApiException {
        //设置请求参数
        AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
        refundRequest.setBizContent("{\"out_trade_no\":\""+ refundVo.getOutTradeNo() +"\","
                + "\"trade_no\":\"\","
                + "\"refund_amount\":\""+ refundVo.getRefundAmount() +"\","
                + "\"refund_reason\":\""+ refundVo.getRefundReason() +"\","
                + "\"out_request_no\":\"\"}");
            AlipayTradeRefundResponse response = alipayClient.execute(refundRequest);
        return response.isSuccess() ? R.ok("退款成功") :R.ok("退款失败");
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
