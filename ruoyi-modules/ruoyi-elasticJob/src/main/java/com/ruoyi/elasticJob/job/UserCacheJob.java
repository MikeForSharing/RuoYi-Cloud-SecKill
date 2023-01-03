package com.ruoyi.elasticJob.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.elasticJob.enums.JobRedisKey;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Set;

/**
 * 用于定时更新登录用户缓存的定时任务（保存7天内的用户登录数据，每天凌晨删除7天以前的数据）
 *
 * @Author: Mike
 */

@Component
@Getter
@Setter
@RefreshScope
@Slf4j
public class UserCacheJob implements SimpleJob {
    @Value("${jobCron.userCache}")
    private String cron;

    @Autowired
    private RedisService redisService;

    @Override
    public void execute(ShardingContext shardingContext) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-7);

        //获取7天前的最近日期的时间值
        Long maxScore = calendar.getTime().getTime();
        String userZSetKey = JobRedisKey.USER_ZSET.getRealKey("");
        String userLoginHashKey = JobRedisKey.USERLOGIN_HASH.getRealKey("");
        //查询7天前的用户ID集合
        Set<String> ids = redisService.getRangeByScore(userZSetKey, 0, maxScore);
        //删除7天前的用户缓存数据
        if(ids.size()>0){
            redisService.deleteCacheMapValue(userLoginHashKey,ids.toArray());
        }
        redisService.removeRangeByScore(userZSetKey,0,calendar.getTime().getTime());

    }
}
