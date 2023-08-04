package com.ruoyi.product.api.service.imp;


import com.ruoyi.product.api.service.IProductService;
import com.ruoyi.product.api.mapper.ProductMapper;
import com.ruoyi.product.api.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 *
 * @Author: Mike
 */
@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> selectProductListByIds(List<Long> ids) {
        return productMapper.queryProductByIds(ids);
    }
}
