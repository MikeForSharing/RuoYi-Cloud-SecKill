package com.ruoyi.seckill.api.factory;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.seckill.api.RemoteSeckillProductService;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 秒杀服务降级处理
 * 
 * @author Mike
 */
@Component
public class RemoteSeckillProductFallbackFactory implements FallbackFactory<RemoteSeckillProductService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteSeckillProductFallbackFactory.class);

    @Override
    public RemoteSeckillProductService create(Throwable throwable)
    {
        log.error("同步秒杀产品列表信息失败:{}", throwable.getMessage());
        return new RemoteSeckillProductService()
        {
            @Override
            public R<List<SeckillProductVo>> queryByTimeForJob(Integer time, String source){
                return R.fail("同步秒杀产品列表信息失败:" + throwable.getMessage());
            }

            @Override
            public R<SeckillProductVo> find(String time, Long seckillId, String source) {
                return R.fail("查找秒杀商品信息失败:" + throwable.getMessage());
            }

            @Override
            public R<String> syncRedisStock(Integer time, Long seckillId, String source) {
                return R.fail("同步Redis中秒杀商品库存数量操作失败:" + throwable.getMessage());
            }
        };
    }
}
