package com.ruoyi.seckill.service.imp;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.seckill.SeckillException;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.product.api.RemoteProductService;
import com.ruoyi.product.api.model.Product;
import com.ruoyi.seckill.api.model.SeckillProduct;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import com.ruoyi.seckill.enums.SeckillRedisKey;
import com.ruoyi.seckill.mapper.SeckillProductMapper;
import com.ruoyi.seckill.service.ISeckillProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *
 * @Author: Mike
 */
@Service
public class SeckillProductServiceImpl implements ISeckillProductService {
    @Autowired
    private SeckillProductMapper seckillProductMapper;

    @Autowired
    private RemoteProductService remoteProductService;

    @Autowired
    private RedisService redisService;

    @Override
    public List<SeckillProductVo> querySeckillProductListByTime(Integer time) {
        //查询出秒杀商品列表信息
        List<SeckillProduct> seckillProductList = seckillProductMapper.queryCurrentlySeckillProduct(time);
        if (seckillProductList.size() ==0){
            return Collections.emptyList();
        }
        List<Long> pids = new ArrayList<>();
        for(SeckillProduct seckillProduct:seckillProductList){
            pids.add(seckillProduct.getProductId());
        }
        R<List<Product>> result = remoteProductService.selectProductListByIds(pids, SecurityConstants.INNER);
        if(result==null || result.getCode()!=200){
            throw new SeckillException("重复抢购!");
        }
        List<Product> productList = result.getData();

        Map<Long,Product> productMap = new HashMap<>();
        for(Product product:productList){
            productMap.put(product.getId(),product);
        }
        List<SeckillProductVo> seckillProductVoList = new ArrayList<>();
        for(SeckillProduct seckillProduct:seckillProductList){
            SeckillProductVo vo = new SeckillProductVo();
            Product product = productMap.get(seckillProduct.getProductId());
            BeanUtils.copyProperties(product,vo);
            BeanUtils.copyProperties(seckillProduct,vo);

            //最开始当前库存量等于商品库存量，用于控制前端库存进度显示
            vo.setCurrentCount(seckillProduct.getStockCount());
            seckillProductVoList.add(vo);
        }
        return seckillProductVoList;
    }

    @Override
    public List<SeckillProductVo> querySeckillProductListByTimeFromCache(Integer time) {
        String key = SeckillRedisKey.SECKILL_PRODUCT_HASH.getRealKey(String.valueOf(time));
        List<Object> values = redisService.getCacheHashList(key);
        List<SeckillProductVo> seckillProductVoList = new ArrayList<>();
        for(Object object:values){
            seckillProductVoList.add(JSON.parseObject((String)object,SeckillProductVo.class));
        }
        return seckillProductVoList;
    }

    @Override
    public SeckillProductVo find(String time,Long seckillId) {
        String key = SeckillRedisKey.SECKILL_PRODUCT_HASH.getRealKey(String.valueOf(time));
        String objStr = redisService.getCacheMapValue(key, String.valueOf(seckillId));
        return JSON.parseObject(objStr,SeckillProductVo.class);
    }
}
