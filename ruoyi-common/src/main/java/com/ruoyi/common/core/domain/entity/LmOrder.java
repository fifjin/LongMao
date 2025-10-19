package com.ruoyi.common.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单实体类
 */
@Data
public class LmOrder {
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long orderId;

    private List<Long> orderIdList;

    /** 订单号 */
    private String orderNumber;

    /** 订单类型（ZC:撞车, DL:代练, PW:陪玩） */
    private String orderType;

    /** 订单状态（0:待接单,1:已接手,2:进行中,3:异常,4:待验收,5:完成,6:已退款） */
    private Integer orderStatus;

    /** 订单标签ID（关联lm_order_label表） */
    private Long orderLabelId;

    /** 标签名称（冗余字段） */
    private String orderLabel;

    /** 客户ID（关联lm_customer表） */
    private Long customerId;

    /** 客户名称（冗余字段） */
    private String customerName;

    /** 客户手机（冗余字段） */
    private String customerPhone;

    /** 客户微信（冗余字段） */
    private String customerWxNumber;

    /** 接手人ID（关联sys_user表） */
    private Long employeeId;

    private Boolean employeeIdIsNullFlag;

    /** 接手人名称（冗余字段） */
    private String employeeName;

    /** 订单金额 */
    private BigDecimal orderPrice;

    /** 订单备注 */
    private String orderRemark;

    /** 创建人ID */
    private Long createUserId;

    /** 创建人名称 */
    private String createUserName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    // 新增：时间范围查询字段（用于接收前端传递的开始/结束时间）
    private String beginTime;
    private String endTime;

    private Boolean lock;

    private BigDecimal commission;
}