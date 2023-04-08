package com.ruoyi.seckill.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.seckill.api.model.SeckillProductVo;
import com.ruoyi.seckill.service.ISeckillProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 秒杀商品信息查询接口
 *
 * @Author: Mike
 */
@RestController
@RequestMapping("/seckillProduct" )
public class SeckillProductController extends BaseController {
    @Autowired
    private ISeckillProductService seckillProductService;

    /**
     * 获取秒杀商品列表（定时任务调用此接口，在定时任务中将信息存入Redis）
     *
     * @param time 秒杀场次
     * @return
     */
    @GetMapping("/queryByTimeForJob" )
    public R<List<SeckillProductVo>> queryByTimeForJob(Integer time) {
        return R.ok(seckillProductService.querySeckillProductListByTime(time));
    }


    /**
     * 从Redis中获取秒杀商品列表
     *
     * @param time 秒杀场次
     * @return
     */
    @RequestMapping("/queryByTime" )
    public R<List<SeckillProductVo>> queryByTime(Integer time) {
        return R.ok(seckillProductService.querySeckillProductListByTimeFromCache(time));
    }

    /**
     * 从Redis获取秒杀商品详情
     *
     * @param time      秒杀场次
     * @param seckillId 秒杀商品Id
     * @return
     */
    @RequestMapping("/find" )
    public R<SeckillProductVo> find(Integer time, Long seckillId) {
        return R.ok(seckillProductService.findFromCache(time, seckillId));
    }

}
