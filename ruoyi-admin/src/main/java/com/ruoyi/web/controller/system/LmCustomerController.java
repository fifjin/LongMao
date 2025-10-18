package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.domain.entity.LmCustomer;
import com.ruoyi.system.service.ILmCustomerService;

@RestController
@RequestMapping("/system/customer")
public class LmCustomerController extends BaseController {
    @Autowired
    private ILmCustomerService customerService;

    /**
     * 获取客户列表
     */
    @GetMapping("/list")
    public TableDataInfo list(LmCustomer customer) {
        startPage();
        List<LmCustomer> list = customerService.selectCustomerList(customer);
        return getDataTable(list);
    }

    /**
     * 导出客户列表
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, LmCustomer customer) {
        List<LmCustomer> list = customerService.selectCustomerList(customer);
        ExcelUtil<LmCustomer> util = new ExcelUtil<LmCustomer>(LmCustomer.class);
        util.exportExcel(response, list, "客户数据");
    }

    /**
     * 根据客户ID获取详细信息
     */
    @GetMapping(value = "/{customerId}")
    public AjaxResult getInfo(@PathVariable Long customerId) {
        return success(customerService.selectCustomerById(customerId));
    }

    /**
     * 新增客户
     */
    @PostMapping
    public AjaxResult add(@RequestBody LmCustomer customer) {
        if (!customerService.checkWxNumberUnique(customer)) {
            return error("新增客户'" + customer.getCustomerName() + "'失败，微信号已存在");
        }
        return toAjax(customerService.insertCustomer(customer));
    }

    /**
     * 修改客户
     */
    @PutMapping
    public AjaxResult edit(@RequestBody LmCustomer customer) {
        if (!customerService.checkWxNumberUnique(customer)) {
            return error("修改客户'" + customer.getCustomerName() + "'失败，微信号已存在");
        }
        return toAjax(customerService.updateCustomer(customer));
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{customerIds}")
    public AjaxResult remove(@PathVariable Long[] customerIds) {
        return toAjax(customerService.deleteCustomerByIds(customerIds));
    }
}