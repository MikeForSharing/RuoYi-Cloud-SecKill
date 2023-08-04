package com.ruoyi.seckill.api;

import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.seckill.api.factory.RemoteSeckillProductFallbackFactory;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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
    R<List<SeckillProductVo>> queryByTimeForJob(@RequestParam("time") Integer time, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 从Redis中查询商品信息
     * @param time 秒杀场次
     * @param seckillId 秒杀id
     * @return
     */
    @GetMapping("/seckillProduct/find")
    R<SeckillProductVo> find(@RequestParam("time") String time, @RequestParam("seckillId") Long seckillId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 同步Redis中秒杀商品库存数量
     * @param time 秒杀场次
     * @param seckillId
     * @param source
     * @return
     */
    @GetMapping("/seckillProduct/syncRedisStock")
    R<String> syncRedisStock(@RequestParam("time") Integer time, @RequestParam("seckillId") Long seckillId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

}
