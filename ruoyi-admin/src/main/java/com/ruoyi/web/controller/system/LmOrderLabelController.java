package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.LmOrderLabel;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.ILmOrderLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 订单标签Controller
 */
@RestController
@RequestMapping("/system/orderLabel")
public class LmOrderLabelController extends BaseController {
    @Autowired
    private ILmOrderLabelService orderLabelService;

    /**
     * 查询订单标签列表
     */
    @PreAuthorize("@ss.hasPermi('system:label:list')")
    @GetMapping("/list")
    public TableDataInfo list(LmOrderLabel orderLabel) {
        startPage();
        List<LmOrderLabel> list = orderLabelService.selectOrderLabelList(orderLabel);
        return getDataTable(list);
    }

    /**
     * 获取启用的订单标签（供前端选择）
     * 修正：使用独立路径，避免与{orderLabelId}冲突
     */
    @PreAuthorize("@ss.hasPermi('system:label:listEnabled')")
    @GetMapping("/listEnabled")
    public AjaxResult getEnabledLabels() {
        LmOrderLabel query = new LmOrderLabel();
        query.setStatus("0"); // 1表示启用状态
        List<LmOrderLabel> labels = orderLabelService.selectOrderLabelList(query);
        return AjaxResult.success(labels);
    }

    /**
     * 获取订单标签详细信息（根据ID查询）
     */
    @PreAuthorize("@ss.hasPermi('system:label:query')")
    @GetMapping("/{orderLabelId}")
    public AjaxResult getInfo(@PathVariable("orderLabelId") Long orderLabelId) {
        return AjaxResult.success(orderLabelService.selectOrderLabelById(orderLabelId));
    }

    /**
     * 新增订单标签
     */
    @PreAuthorize("@ss.hasPermi('system:label:add')")
    @Log(title = "订单标签", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LmOrderLabel orderLabel) {
        orderLabel.setCreateTime(new Date());
        return toAjax(orderLabelService.insertOrderLabel(orderLabel));
    }

    /**
     * 修改订单标签
     */
    @PreAuthorize("@ss.hasPermi('system:label:edit')")
    @Log(title = "订单标签", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LmOrderLabel orderLabel) {
        orderLabel.setUpdateTime(new Date());
        return toAjax(orderLabelService.updateOrderLabel(orderLabel));
    }

    /**
     * 删除订单标签
     */
    @PreAuthorize("@ss.hasPermi('system:label:remove')")
    @Log(title = "订单标签", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderLabelIds}")
    public AjaxResult remove(@PathVariable Long[] orderLabelIds) {
        return toAjax(orderLabelService.deleteOrderLabelByIds(orderLabelIds));
    }
}