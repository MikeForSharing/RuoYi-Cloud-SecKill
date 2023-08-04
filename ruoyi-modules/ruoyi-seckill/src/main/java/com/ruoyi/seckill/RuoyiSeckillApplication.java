package com.ruoyi.seckill;

import com.ruoyi.common.security.annotation.EnableCustomConfig;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;
import com.ruoyi.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication
@ComponentScan(basePackages = {"com.ruoyi.product.api.*" , "com.ruoyi.rabbit.api.*" , "com.ruoyi.system.api.*" , "com.ruoyi.seckill.*", "com.ruoyi.common.rabbitmq.*"})
public class RuoyiSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuoyiSeckillApplication.class, args);
        System.out.println("启动成功" );
    }

}
