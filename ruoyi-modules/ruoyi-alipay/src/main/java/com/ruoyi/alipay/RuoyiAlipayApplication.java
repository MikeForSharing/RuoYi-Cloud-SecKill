package com.ruoyi.alipay;

import com.ruoyi.common.security.annotation.EnableCustomConfig;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;
import com.ruoyi.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication
public class RuoyiAlipayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuoyiAlipayApplication.class, args);
        System.out.println("启动成功" );
    }

}
