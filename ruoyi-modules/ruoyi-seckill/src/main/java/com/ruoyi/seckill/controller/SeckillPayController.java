package com.ruoyi.seckill.controller;


import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.seckill.api.RemoteSeckillAlipayService;
import com.ruoyi.seckill.api.model.OrderInfo;
import com.ruoyi.seckill.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


/**
 * 订单支付接口
 *
 * @Author: Mike
 */
@RestController
@RequestMapping("/seckillPay")
@RefreshScope
public class SeckillPayController {

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private RemoteSeckillAlipayService remoteSeckillAlipayService;

    @Value("${pay.errorUrl}")
    private String errorUrl;
    @Value("${pay.frontEndPayUrl}")
    private String frontEndPayUrl;

    @RequestMapping("/pay")
    public R<String> pay(String orderNo, Integer type) {
        if (OrderInfo.PAYTYPE_INTERGRAL.equals(type)) {
//            seckillOrderService.payIntergral(orderNo);
            return R.ok("");
        } else {
            String payHtml = seckillOrderService.alipay(orderNo);
            return R.ok(payHtml);
        }
    }

    @RequestMapping("/notify_url")
    public String notifyUrl(@RequestParam Map<String, String> params) {
        System.out.println("异步回调");
        R<Boolean> result = remoteSeckillAlipayService.rsaCheckV1(params);
        if (StringUtils.isNull(result) || StringUtils.isNull(result.getData())) {
            return "异步回调失败";
        }
        boolean signVerified = result.getData();
        if (signVerified) {
            String orderNo = params.get("out_trade_no");
            seckillOrderService.payDone(orderNo);
            return "success";
        } else {
            return "fail";
        }
    }

    @RequestMapping("/return_url")
    public void returnUrl(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        System.out.println("同步回调");
        R<Boolean> result = remoteSeckillAlipayService.rsaCheckV1(params);
        if (StringUtils.isNull(result) || StringUtils.isNull(result.getData())) {
            response.sendRedirect(errorUrl);
        }
        boolean signVerified = result.getData();
        if (signVerified) {
            String orderNo = params.get("out_trade_no");
            response.sendRedirect(frontEndPayUrl + orderNo);
        } else {
            response.sendRedirect(errorUrl);
        }
    }
}
