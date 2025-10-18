package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.LmCustomerMapper;
import com.ruoyi.common.core.domain.entity.LmCustomer;
import com.ruoyi.system.service.ILmCustomerService;

@Service
public class LmCustomerServiceImpl implements ILmCustomerService {
    @Autowired
    private LmCustomerMapper customerMapper;

    @Override
    public List<LmCustomer> selectCustomerList(LmCustomer customer) {
        return customerMapper.selectCustomerList(customer);
    }

    @Override
    public LmCustomer selectCustomerById(Long customerId) {
        return customerMapper.selectCustomerById(customerId);
    }

    @Override
    public boolean checkWxNumberUnique(LmCustomer customer) {
        Long customerId = StringUtils.isNull(customer.getCustomerId()) ? -1L : customer.getCustomerId();
        LmCustomer info = customerMapper.checkWxNumberUnique(customer.getCustomerWxNumber());
        if (StringUtils.isNotNull(info) && info.getCustomerId().longValue() != customerId.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public int insertCustomer(LmCustomer customer) {
        // 设置创建人
        customer.setCreateUserId(SecurityUtils.getUserId());
        customer.setCreateTime(new Date());
        return customerMapper.insertCustomer(customer);
    }

    @Override
    public int updateCustomer(LmCustomer customer) {
        return customerMapper.updateCustomer(customer);
    }

    @Override
    public int deleteCustomerById(Long customerId) {
        return customerMapper.deleteCustomerById(customerId);
    }

    @Override
    public int deleteCustomerByIds(Long[] customerIds) {
        return customerMapper.deleteCustomerByIds(customerIds);
    }
}