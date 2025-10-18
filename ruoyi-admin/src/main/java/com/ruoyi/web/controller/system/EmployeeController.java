package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.domain.vo.EmployeeVO;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.IEmployeeService;

@RestController
@RequestMapping("/system/employee")
public class EmployeeController extends BaseController {
    @Autowired
    private IEmployeeService employeeService;

    /**
     * 获取打手列表（dept_id=102的用户）
     */
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {
        startPage();
        List<EmployeeVO> list = employeeService.selectEmployeeList(user);
        return getDataTable(list);
    }

    /**
     * 初始化打手列表（dept_id=102的用户）
     */
    @PostMapping("/initView")
    public TableDataInfo initView() {
        startPage();
        List<EmployeeVO> list = employeeService.selectEmployeeList(new SysUser());
        return getDataTable(list);
    }

    /**
     * 导出打手列表
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response, SysUser user) {
        List<EmployeeVO> list = employeeService.selectEmployeeList(user);
        ExcelUtil<EmployeeVO> util = new ExcelUtil<EmployeeVO>(EmployeeVO.class);
        util.exportExcel(response, list, "打手数据");
    }

    /**
     * 修改打手闲忙状态
     */
    @PutMapping("/changeState")
    public AjaxResult changeState(@RequestBody SysUser user) {
        return toAjax(employeeService.updateUserState(user));
    }
}