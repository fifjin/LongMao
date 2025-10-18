package com.ruoyi.common.core.domain.entity;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class LmOrderLabel extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Excel(name = "标签ID")
    private Long orderLabelId;

    @NotBlank(message = "标签名称不能为空")
    @Excel(name = "标签名称")
    private String labelName;

    @Excel(name = "状态", readConverterExp = "0=启用,1=停用")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // getter和setter方法
    public Long getOrderLabelId() { return orderLabelId; }
    public void setOrderLabelId(Long orderLabelId) { this.orderLabelId = orderLabelId; }
    public String getLabelName() { return labelName; }
    public void setLabelName(String labelName) { this.labelName = labelName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    @Override
    public Date getCreateTime() { return createTime; }
    @Override
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}