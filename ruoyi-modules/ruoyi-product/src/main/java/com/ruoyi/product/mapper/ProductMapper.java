package com.ruoyi.product.mapper;

import com.ruoyi.product.api.model.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Mike
 */
public interface ProductMapper {
    /**
     * 根据用户传入的id集合查询商品对象信息
     *
     * @param ids
     * @return
     */
    List<Product> queryProductByIds(@Param("ids" ) List<Long> ids);
}
