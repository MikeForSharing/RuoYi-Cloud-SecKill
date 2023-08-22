package com.ruoyi.rabbit.api.factory;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.rabbit.api.RemoteRabbitService;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 商品服务降级处理
 *
 * @author ruoyi
 */
@Component
public class RemoteRabbitFallbackFactory implements FallbackFactory<RemoteRabbitService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteRabbitFallbackFactory.class);

    @Override
    public RemoteRabbitService create(Throwable throwable) {
        log.error("商品服务调用失败:{}", throwable.getMessage());
        return new RemoteRabbitService() {

            @Override
            public R<LoginUser> getUserInfo(String username, String source)
            {
                return R.fail("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public R<String> triggerSendEmail(SysUser sysUser, String source) {
                return R.fail("发送邮件失败:" + throwable.getMessage());
            }

            @Override
            public R<String> triggerSendMQ(String message, int delayType, String source) {
                return R.fail("发送消息失败:" + throwable.getMessage());
            }

            @Override
            public R<String> triggerSend(String message,  String source) {
                return R.fail("发送消息失败:" + throwable.getMessage());
            }
        };
    }
}
