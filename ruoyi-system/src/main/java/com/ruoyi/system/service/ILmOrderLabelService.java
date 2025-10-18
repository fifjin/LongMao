package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.common.core.domain.entity.LmOrderLabel;

public interface ILmOrderLabelService {
    /**
     * 查询订单标签列表
     * @param label 订单标签信息
     * @return 订单标签列表
     */
    List<LmOrderLabel> selectOrderLabelList(LmOrderLabel label);

    /**
     * 查询启用的订单标签
     * @return 订单标签列表
     */
    List<LmOrderLabel> selectEnabledOrderLabels();

    /**
     * 通过ID查询标签
     * @param orderLabelId 标签ID
     * @return 标签信息
     */
    LmOrderLabel selectOrderLabelById(Long orderLabelId);

    /**
     * 校验标签名称是否唯一
     * @param label 标签信息
     * @return 结果
     */
    boolean checkLabelNameUnique(LmOrderLabel label);

    /**
     * 新增标签
     * @param label 标签信息
     * @return 结果
     */
    int insertOrderLabel(LmOrderLabel label);

    /**
     * 修改标签
     * @param label 标签信息
     * @return 结果
     */
    int updateOrderLabel(LmOrderLabel label);

    /**
     * 删除标签
     * @param orderLabelId 标签ID
     * @return 结果
     */
    int deleteOrderLabelById(Long orderLabelId);

    /**
     * 批量删除标签
     * @param orderLabelIds 需要删除的标签ID
     * @return 结果
     */
    int deleteOrderLabelByIds(Long[] orderLabelIds);
}