package com.ruoyi.elasticJob.job;


import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.elasticJob.enums.JobRedisKey;
import com.ruoyi.seckill.api.RemoteSeckillProductService;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 执行定时任务（秒杀商品列表信息保存至Redis）
 *
 * @Author: Mike
 */
@Component
@Setter
@Getter
@RefreshScope
public class SeckillProductCacheJob implements SimpleJob {
    @Value("${jobCron.initSeckillProduct}" )
    private String cron;
    @Autowired
    private RemoteSeckillProductService remoteSeckillProductService;
    @Autowired
    private RedisService redisService;

    @Override
    public void execute(ShardingContext shardingContext) {
        String time = shardingContext.getShardingParameter();
        System.out.println("执行定时任务（秒杀商品列表信息保存至Redis），场次："+time );
        R<List<SeckillProductVo>> result = remoteSeckillProductService.queryByTimeForJob(Integer.parseInt(time), SecurityConstants.INNER);
        if (result == null || result.getCode() != 200) {
            System.out.println("定时任务执行异常！" );
            return;
        }
        List<SeckillProductVo> seckillProductVoList = result.getData();
        String key = JobRedisKey.SECKILL_PRODUCT_HASH.getRealKey(time);
        String countKey = JobRedisKey.SECKILL_STOCK_COUNT_HASH.getRealKey(time);
        //先删除前一天的数据
        redisService.deleteObject(key);
        redisService.deleteObject(countKey);
        for (SeckillProductVo vo : seckillProductVoList) {
            vo.setCurrentCount(vo.getStockCount());
            redisService.setCacheMapValue(key, String.valueOf(vo.getId()), JSON.toJSONString(vo));
            redisService.setCacheMapValue(countKey, String.valueOf(vo.getId()), String.valueOf(vo.getStockCount()));
        }
    }
}
