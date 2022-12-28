package com.ruoyi.rabbit.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.rabbit.enums.DelayTypeEnum;
import com.ruoyi.rabbit.producer.DelayMessageProducer;
import com.ruoyi.rabbit.service.RabbitService;
import com.ruoyi.system.api.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("rabbitmq")
public class RabbitController {

    @Resource
    private DelayMessageProducer producer;

    @Resource
    private RabbitService rabbitService;

    /**
     * 向消息队列发消息
     */
    @GetMapping("/sendMQ/{message}&&{delayType}")
    public void sendMQ(@PathVariable("message") String message,@PathVariable("delayType") int delayType) {
        log.info("当前时间：{}，消息：{}，延迟类型：{}", LocalDateTime.now(), message, delayType);
        producer.send(message, Objects.requireNonNull(DelayTypeEnum.getDelayTypeEnum(delayType)));
    }

    /**
     * 发送邮件
     */
    @PostMapping("/sendEmail")
    public R<String> sendEmail(@RequestBody SysUser user) {
        log.info("发送邮件。。。。");
        String activeCode = rabbitService.sendMailHtml(user);
        return R.ok(activeCode,"邮件发送成功");
    }

    /**
     * 激活账户
     */
    @RequestMapping(value = "/activeAccount",produces = MediaType.TEXT_HTML_VALUE)
    public String activeAccount(String activeCode,Long userId) throws Exception {
        log.info("激活邮件。。。。");
        String resStr = "";
        //将激活码存入用户表中
        if(rabbitService.updateEmailCode(activeCode,userId)>0){
            resStr  ="<h1 align='center' style='color:green'>激活成功!</h1>" ;
        }else {
            resStr  ="<h1 align='center' style='color:red'>激活失败!</h1>" ;
        }
        String htmlStr =  "<html>" +
                "<header><title>激活结果</title></header>" +
                "<body><br><br><br>" +
                resStr +
                "</body>" +
                "</html>";
        return htmlStr;
    }


//    @RequestMapping(value = "/activeAccount",produces = MediaType.IMAGE_PNG_VALUE)
//    public byte[] activeAccount(String activeCode,Long userId) throws Exception {
//        log.info("激活邮件。。。。");
//        ClassPathResource classPathResource;
//        //将激活码存入用户表中
//        if(rabbitService.updateEmailCode(activeCode,userId)>0){
//            classPathResource  = new ClassPathResource("/static/act_success.png");
//        }else {
//            classPathResource  = new ClassPathResource("/static/act_error.png");
//        }
//        BufferedImage bufferedImage = ImageIO.read(classPathResource.getInputStream());
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        ImageIO.write(bufferedImage, "png", out);
//        return out.toByteArray();
//    }
}
