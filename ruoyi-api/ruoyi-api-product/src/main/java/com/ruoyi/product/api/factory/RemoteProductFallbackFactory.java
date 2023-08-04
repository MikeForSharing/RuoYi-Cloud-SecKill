package com.ruoyi.product.api.factory;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.product.api.RemoteProductService;
import com.ruoyi.product.api.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 商品服务降级处理
 * 
 * @author ruoyi
 */
@Component
public class RemoteProductFallbackFactory implements FallbackFactory<RemoteProductService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteProductFallbackFactory.class);

    @Override
    public RemoteProductService create(Throwable throwable) {
        log.error("商品服务调用失败:{}", throwable.getMessage());
        return new RemoteProductService()
        {
            @Override
            public R<List<Product>> selectProductListByIds(List<Long> pids, String source) {
                return R.fail("商品信息失败:" + throwable.getMessage());
            }


        };
    }
}
