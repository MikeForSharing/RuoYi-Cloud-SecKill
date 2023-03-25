package com.ruoyi.seckill.api;

import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.seckill.api.factory.RemoteSeckillProductFallbackFactory;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * 秒杀服务
 *
 * @Author: Mike
 */
@FeignClient(contextId = "remoteSeckillProductService", value = ServiceNameConstants.SECKILL_SERVICE, fallbackFactory = RemoteSeckillProductFallbackFactory.class)
public interface RemoteSeckillProductService {

    /**
     * 根据场次查询商品信息列表(定时任务中将秒杀商品信息放入Redis)
     * @param time 场次
     * @return
     */
    @GetMapping("/seckillProduct/queryByTimeForJob")
    R<List<SeckillProductVo>> queryByTimeForJob(@RequestParam("time") Integer time);
}
