package com.ruoyi.product.api.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.product.api.service.IProductService;
import com.ruoyi.product.api.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * 秒杀商品
 *
 * @Author: Mike
 */
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    private IProductService productService;
    @GetMapping("/selectProductListByIds")
    public R<List<Product>> selectProductListByIds(@RequestParam("ids") List<Long> ids){
        if(ids==null || ids.size()==0){
            return R.ok(Collections.emptyList());
        }
        return R.ok(productService.selectProductListByIds(ids));
    }
}
