package com.ruoyi.rabbit.controller;

import com.ruoyi.rabbit.producer.SeckilPreOrderMQlProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @Author: zhangJiang
 */

@Slf4j
@RestController
@RequestMapping("rabbitSeckill" )
public class RabbitSeckillController {
    @Resource
    private SeckilPreOrderMQlProducer preOrderproducer;


    /**
     * 向消息队列发消息
     */
    @GetMapping("/sendPreOrderMQ/{message}")
    public void sendPreOrderMQ(@PathVariable("message" ) String message) {
        log.info("当前时间：{}，消息：{}" , LocalDateTime.now(), message);
        preOrderproducer.sendPreOrder(message);
    }
}
