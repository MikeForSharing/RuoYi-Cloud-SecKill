package com.ruoyi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 网关启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class RuoYiGatewayApplication
{
    public static void main(String[] args)
    {


        System.setProperty("csp.sentinel.app.type" , "1"); //Sentinel整合Gateway控制台不显示API管理
        SpringApplication.run(RuoYiGatewayApplication.class, args);
        System.out.println("启动成功");
    }
}
