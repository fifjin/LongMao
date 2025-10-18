package com.ruoyi.common.core.domain.entity;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class LmCustomer extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Excel(name = "客户ID")
    private Long customerId;

    @NotBlank(message = "客户微信号不能为空")
    @Excel(name = "客户微信号")
    private String customerWxNumber;

    @NotBlank(message = "客户昵称不能为空")
    @Excel(name = "客户昵称")
    private String customerName;

    @Excel(name = "客户手机")
    private String customerPhone;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Excel(name = "创建人")
    private Long createUserId;

    private String createUserName;

    // getter和setter方法
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerWxNumber() { return customerWxNumber; }
    public void setCustomerWxNumber(String customerWxNumber) { this.customerWxNumber = customerWxNumber; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    @Override
    public Date getCreateTime() { return createTime; }
    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Long getCreateUserId() { return createUserId; }
    public void setCreateUserId(Long createUserId) { this.createUserId = createUserId; }
    public String getCreateUserName() { return createUserName; }
    public void setCreateUserName(String createUserName) { this.createUserName = createUserName; }
}