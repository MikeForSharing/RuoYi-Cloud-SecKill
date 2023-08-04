package com.ruoyi.product.api;

import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.product.api.factory.RemoteProductFallbackFactory;
import com.ruoyi.product.api.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;


/**
 * 商品服务
 *
 * @Author: Mike
 */
@FeignClient(contextId = "remoteProductService", value = ServiceNameConstants.PRODUCT_SERVICE, fallbackFactory = RemoteProductFallbackFactory.class)
public interface RemoteProductService {
    @GetMapping("/product/selectProductListByIds/{pids}")
    R<List<Product>> selectProductListByIds(@PathVariable("pids") List<Long> pids, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
