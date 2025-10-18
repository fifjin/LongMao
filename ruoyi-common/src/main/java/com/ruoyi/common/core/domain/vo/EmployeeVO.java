package com.ruoyi.common.core.domain.vo;

import com.ruoyi.common.core.domain.entity.SysUser;

public class EmployeeVO extends SysUser {
    private static final long serialVersionUID = 1L;

    private String deptName;
    private Integer receivedOrderCount;
    private Integer completedOrderCount;
    private Integer abnormalOrderCount;

    // getter和setter方法
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Integer getReceivedOrderCount() { return receivedOrderCount; }
    public void setReceivedOrderCount(Integer receivedOrderCount) { this.receivedOrderCount = receivedOrderCount; }
    public Integer getCompletedOrderCount() { return completedOrderCount; }
    public void setCompletedOrderCount(Integer completedOrderCount) { this.completedOrderCount = completedOrderCount; }
    public Integer getAbnormalOrderCount() { return abnormalOrderCount; }
    public void setAbnormalOrderCount(Integer abnormalOrderCount) { this.abnormalOrderCount = abnormalOrderCount; }
}