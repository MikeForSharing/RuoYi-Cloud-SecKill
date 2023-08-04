package com.ruoyi.seckill.mapper;

import com.ruoyi.seckill.api.model.OrderInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: zhangJiang
 */
public interface OrderInfoMapper {
    /**
     * 插入订单信息
     *
     * @param orderInfo
     * @return
     */
    int insert(OrderInfo orderInfo);


    /**
     * 根据订单编号查找订单
     *
     * @param orderNo
     * @return
     */
    OrderInfo find(String orderNo);

    /**
     * 将订单状态修改成取消状态
     *
     * @param orderNo
     * @param status
     * @return
     */
    int updateCancelStatus(@Param("orderNo" ) String orderNo, @Param("status" ) Integer status);


}
