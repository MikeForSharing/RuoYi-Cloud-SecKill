package com.ruoyi.websocket;

import com.ruoyi.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ruoyi.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


/**
 * @Author: zhangJiang
 * @Version： 1.0.0
 */
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RuoYiWebsocketApplication {
    public static void main(String[] args) {
        SpringApplication.run(RuoYiWebsocketApplication.class, args);
        System.out.println("websocket模块启动成功");
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
