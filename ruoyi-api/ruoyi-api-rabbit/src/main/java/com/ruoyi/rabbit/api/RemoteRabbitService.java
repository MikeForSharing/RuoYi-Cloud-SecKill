package com.ruoyi.rabbit.api;

import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;

import com.ruoyi.rabbit.api.factory.RemoteRabbitFallbackFactory;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 消息中间件服务
 *
 * @author ruoyi
 */
@FeignClient(contextId = "remoteRabbitService", value = ServiceNameConstants.RABBIT_SERVICE, fallbackFactory = RemoteRabbitFallbackFactory.class)
public interface RemoteRabbitService {
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @param source   请求来源
     * @return 结果
     */
    @GetMapping("/user/info/{username}")
    public R<LoginUser> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 通过用户名查询用户信息
     *
     * @param sysUser 用户
     * @param source  请求来源
     * @return 结果
     */
    @PostMapping("/rabbitmq/sendEmail")
    public R<String> triggerSendEmail(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 通过用户名查询用户信息
     *
     * @param message   消息内容
     * @param delayType 延迟类型
     * @return 结果
     */
    @GetMapping("/rabbitmq/sendMQ/{message}&&{delayType}")
//    @PostMapping("/rabbitmq/sendMQ")
    public R<String> triggerSendMQ(@PathVariable("message") String message, @PathVariable("delayType") int delayType, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 通过用户名查询用户信息
     *
     * @param message   消息内容
     * @return 结果
     */
    @GetMapping("/rabbitSeckill/send/{message}")
//    @PostMapping("/rabbitmq/sendMQ")
    public R<String> triggerSend(@PathVariable("message") String message, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
