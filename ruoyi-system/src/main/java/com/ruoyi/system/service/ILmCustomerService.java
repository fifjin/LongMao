package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.common.core.domain.entity.LmCustomer;

public interface ILmCustomerService {
    /**
     * 查询客户列表
     * @param customer 客户信息
     * @return 客户列表
     */
    List<LmCustomer> selectCustomerList(LmCustomer customer);

    /**
     * 通过ID查询客户
     * @param customerId 客户ID
     * @return 客户信息
     */
    LmCustomer selectCustomerById(Long customerId);

    /**
     * 校验微信号是否唯一
     * @param customer 客户信息
     * @return 结果
     */
    boolean checkWxNumberUnique(LmCustomer customer);

    /**
     * 新增客户
     * @param customer 客户信息
     * @return 结果
     */
    int insertCustomer(LmCustomer customer);

    /**
     * 修改客户
     * @param customer 客户信息
     * @return 结果
     */
    int updateCustomer(LmCustomer customer);

    /**
     * 删除客户
     * @param customerId 客户ID
     * @return 结果
     */
    int deleteCustomerById(Long customerId);

    /**
     * 批量删除客户
     * @param customerIds 需要删除的客户ID
     * @return 结果
     */
    int deleteCustomerByIds(Long[] customerIds);
}