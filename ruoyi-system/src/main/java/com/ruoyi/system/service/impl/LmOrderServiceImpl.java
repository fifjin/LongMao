package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.entity.LmOrder;
import com.ruoyi.common.param.GrabOrderParam;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.mapper.LmOrderMapper;
import com.ruoyi.system.service.ILmOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 订单Service实现
 */
@Service
public class LmOrderServiceImpl implements ILmOrderService {
    @Autowired
    private LmOrderMapper orderMapper;

    @Override
    public List<LmOrder> selectOrderList(LmOrder order) {
        return orderMapper.selectOrderList(order);
    }

    @Override
    public List<LmOrder> selectGrabOrderList(LmOrder order) {
        return orderMapper.selectGrabOrderList(order);
    }

    @Override
    public LmOrder selectOrderById(Long orderId) {
        LmOrder order = new LmOrder();
        order.setOrderId(orderId);
        return orderMapper.selectOrderById(order);
    }

    @Override
    public LmOrder selectGrabOrderById(Long orderId) {
        LmOrder order = new LmOrder();
        order.setOrderId(orderId);
        return orderMapper.selectGrabOrderById(order);
    }

    @Override
    public int insertOrder(LmOrder order) {
        // 生成订单号：年月日+UUID前8位
        String dateStr = DateUtils.dateTimeNow("yyyyMMdd");
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        order.setOrderNumber(dateStr + uuid);

        order.setCreateTime(DateUtils.getNowDate());
        order.setUpdateTime(DateUtils.getNowDate());
        return orderMapper.insertOrder(order);
    }

    @Override
    public int updateOrder(LmOrder order) {
        order.setUpdateTime(DateUtils.getNowDate());
        return orderMapper.updateOrder(order);
    }

    @Override
    public int deleteOrderById(Long orderId) {
        return orderMapper.deleteOrderById(orderId);
    }

    @Override
    public int assignOrder(LmOrder order) {
        order.setUpdateTime(DateUtils.getNowDate());
        return orderMapper.assignOrder(order);
    }

    @Override
    public int grabOrder(GrabOrderParam grabOrderParam) {
        //查询订单信息
        LmOrder orderQuery = new LmOrder();
        orderQuery.setOrderId(grabOrderParam.getOrderId());
        orderQuery.setLock(true);
        orderQuery.setOrderStatus(0);
        LmOrder lmOrder = orderMapper.selectGrabOrderById(orderQuery);
        Assert.notNull(lmOrder,"该订单已被他人认领！");
        //抢单
        LmOrder updateOrder = new LmOrder();
        updateOrder.setOrderId(lmOrder.getOrderId());
        updateOrder.setEmployeeId(SecurityUtils.getUserId());
        updateOrder.setOrderStatus(1);
        return orderMapper.updateOrder(updateOrder);
    }
}