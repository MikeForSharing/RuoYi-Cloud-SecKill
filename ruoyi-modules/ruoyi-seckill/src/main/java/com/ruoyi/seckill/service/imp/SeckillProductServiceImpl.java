package com.ruoyi.seckill.service.imp;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.seckill.SeckillException;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.product.api.RemoteProductService;
import com.ruoyi.product.api.model.Product;
import com.ruoyi.seckill.api.model.SeckillProduct;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import com.ruoyi.seckill.enums.SeckillRedisKey;
import com.ruoyi.seckill.mapper.SeckillProductMapper;
import com.ruoyi.seckill.service.ISeckillOrderService;
import com.ruoyi.seckill.service.ISeckillProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private RedisService redisService;

    @Override
    public List<SeckillProductVo> querySeckillProductListByTime(Integer time) {
        //查询出秒杀商品列表信息
        List<SeckillProduct> seckillProductList = seckillProductMapper.queryCurrentlySeckillProduct(time);
        List<Long> pids = new ArrayList<>();
        for(SeckillProduct seckillProduct:seckillProductList){
            pids.add(seckillProduct.getProductId());
        }
        R<List<Product>> result = remoteProductService.selectProductListByIds(pids);
        if(result==null || result.hasError()){
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

    @Override
    @Transactional
    public String doSeckill(String userId, SeckillProductVo vo) {
        int count = seckillProductMapper.decrStock(vo.getId());
        if(count==0){
            throw new SeckillException("商品已卖完！");
        }
        String orderNo = seckillOrderService.createOrderInfo(userId,vo);
        return orderNo;
    }

    @Override
    public int incrStockCount(Long seckillId) {
        return seckillProductMapper.incrStock(seckillId);
    }

    @Override
    public void syncRedisStock(Integer time, Long seckillId) {
        int stockCount = seckillProductMapper.getStockCount(seckillId);
        if(stockCount>0){
            String key = SeckillRedisKey.SECKILL_STOCK_COUNT_HASH.getRealKey(String.valueOf(time));
            redisService.setCacheMapValue(key,String.valueOf(seckillId),String.valueOf(stockCount));
        }
    }
}
