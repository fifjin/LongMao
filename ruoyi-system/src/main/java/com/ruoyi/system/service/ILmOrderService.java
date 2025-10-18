package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.entity.LmOrder;

import java.util.List;

/**
 * 订单Service接口
 */
public interface ILmOrderService {
    /**
     * 查询订单列表（支持时间范围）
     */
    List<LmOrder> selectOrderList(LmOrder order);

    /**
     * 查询订单详情
     */
    LmOrder selectOrderById(Long orderId);

    /**
     * 新增订单
     */
    int insertOrder(LmOrder order);

    /**
     * 修改订单
     */
    int updateOrder(LmOrder order);

    /**
     * 删除订单
     */
    int deleteOrderById(Long orderId);

    /**
     * 分配订单接手人
     */
    int assignOrder(LmOrder order);
}