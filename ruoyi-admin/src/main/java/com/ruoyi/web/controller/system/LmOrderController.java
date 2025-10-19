package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.LmOrder;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.param.GrabOrderParam;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ILmOrderService;
import com.ruoyi.system.service.ILmOrderLabelService;
import com.ruoyi.system.service.ILmCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 订单Controller
 */
@RestController
@RequestMapping("/system/order")
public class LmOrderController extends BaseController {
    @Autowired
    private ILmOrderService orderService;

    @Autowired
    private ILmOrderLabelService orderLabelService;

    @Autowired
    private ILmCustomerService customerService;

    /**
     * 查询订单列表（支持时间范围查询）
     */
    @PreAuthorize("@ss.hasPermi('system:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(LmOrder order) {
        startPage();
        List<LmOrder> list = orderService.selectOrderList(order);
        return getDataTable(list);
    }

    /**
     * 查询抢单订单列表（支持时间范围查询）
     */
    @PreAuthorize("@ss.hasPermi('system:order:grabOrderList')")
    @GetMapping("/grabOrderList")
    public TableDataInfo grabOrderList(LmOrder order) {
        startPage();
        order.setEmployeeIdIsNullFlag(true);
        List<LmOrder> list = orderService.selectGrabOrderList(order);
        return getDataTable(list);
    }

    /**
     * 查询我的抢单订单列表（支持时间范围查询）
     */
    @PreAuthorize("@ss.hasPermi('system:order:myGrabOrderList')")
    @GetMapping("/myGrabOrderList")
    public TableDataInfo myGrabOrderList(LmOrder order) {
        startPage();
        order.setEmployeeId(SecurityUtils.getUserId());
        List<LmOrder> list = orderService.selectGrabOrderList(order);
        return getDataTable(list);
    }

    /**
     * 导出订单列表
     */
    @PreAuthorize("@ss.hasPermi('system:order:export')")
    @Log(title = "订单管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LmOrder order) {
        List<LmOrder> list = orderService.selectOrderList(order);
        ExcelUtil<LmOrder> util = new ExcelUtil<LmOrder>(LmOrder.class);
        util.exportExcel(response, list, "订单数据");
    }

    /**
     * 获取订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:order:query')")
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId) {
        return AjaxResult.success(orderService.selectOrderById(orderId));
    }

    /**
     * 获取订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:grabOrder:query')")
    @GetMapping("/{orderId}/grab")
    public AjaxResult getGrabInfo(@PathVariable("orderId") Long orderId) {
        return AjaxResult.success(orderService.selectGrabOrderById(orderId));
    }

    /**
     * 新增订单
     */
    @PreAuthorize("@ss.hasPermi('system:order:add')")
    @Log(title = "订单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LmOrder order) {
        order.setCreateUserId(getUserId());
        order.setCreateUserName(getUsername());
        return toAjax(orderService.insertOrder(order));
    }

    /**
     * 修改订单
     */
    @PreAuthorize("@ss.hasPermi('system:order:edit')")
    @Log(title = "订单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LmOrder order) {
        return toAjax(orderService.updateOrder(order));
    }

    /**
     * 删除订单
     */
    @PreAuthorize("@ss.hasPermi('system:order:remove')")
    @Log(title = "订单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderId}")
    public AjaxResult remove(@PathVariable Long orderId) {
        return toAjax(orderService.deleteOrderById(orderId));
    }

    /**
     * 分配订单接手人
     */
    @PreAuthorize("@ss.hasPermi('system:order:assign')")
    @Log(title = "订单管理", businessType = BusinessType.UPDATE)
    @PutMapping("/assign")
    public AjaxResult assignOrder(@RequestBody LmOrder order) {
        if (order.getOrderId() == null || order.getEmployeeId() == null) {
            return AjaxResult.error("订单ID和接手人ID不能为空");
        }
        return toAjax(orderService.assignOrder(order));
    }

    /**
     * 获取所有客户（供前端选择）
     */
    @GetMapping("/customers/all")
    public AjaxResult getAllCustomers() {
        return AjaxResult.success(customerService.selectCustomerList(null));
    }

    /**
     * 抢单
     */
    @PreAuthorize("@ss.hasPermi('system:order:grabOrder')")
    @PostMapping("/grabOrder")
    public AjaxResult grabOrder(@RequestBody GrabOrderParam param) {
        return toAjax(orderService.grabOrder(param));
    }
}