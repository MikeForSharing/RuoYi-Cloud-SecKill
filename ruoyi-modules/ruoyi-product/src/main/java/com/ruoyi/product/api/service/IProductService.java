package com.ruoyi.product.api.service;



import com.ruoyi.product.api.model.Product;
import com.ruoyi.product.api.model.Product;

import java.util.List;

/**
 * @Author: Mike
 */
public interface IProductService {
    List<Product> selectProductListByIds(List<Long> ids);
}
