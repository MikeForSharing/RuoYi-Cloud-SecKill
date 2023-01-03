package com.ruoyi.elasticJob.config;

import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Mike
 * @Date: 2022/12/28 0028
 * @Version： 1.0.0
 *
 * 配置zookeeper注册中心对象
 */
@Configuration
public class RegistryCenterConfig {
    @Bean(initMethod = "init")
    public CoordinatorRegistryCenter createRegistryCenter(@Value("${elasticjob.zk-url}") String zkUrl, @Value("${elasticjob.group-name}")String groupName){
        //zookeeper的配置
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(zkUrl,groupName);
        //设置zookeeper超时时间
        zookeeperConfiguration.setSessionTimeoutMilliseconds(100);
        //创建注册中心
        CoordinatorRegistryCenter zookeeperRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        return zookeeperRegistryCenter;
    }
}
