package com.ruoyi.product.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.security.annotation.InnerAuth;
import com.ruoyi.product.service.IProductService;
import com.ruoyi.product.api.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @InnerAuth
    @GetMapping("/selectProductListByIds/{pids}")
    public R<List<Product>> selectProductListByIds(@PathVariable("pids") List<Long> pids){
        if(pids==null || pids.size()==0){
            return R.ok(Collections.emptyList());
        }
        return R.ok(productService.selectProductListByIds(pids));
    }

    @GetMapping("/test")
    public R<List<String>> test(){
        return R.ok();
    }
}
