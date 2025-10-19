package com.ruoyi.common.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GrabOrderParam {

    @NotNull(message = "订单id不为空")
    private Long orderId;

}
