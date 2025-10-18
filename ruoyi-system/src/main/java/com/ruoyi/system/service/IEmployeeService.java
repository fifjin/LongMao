package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.common.core.domain.vo.EmployeeVO;
import com.ruoyi.common.core.domain.entity.SysUser;

public interface IEmployeeService {
    /**
     * 查询打手列表
     * @param user 用户信息
     * @return 打手列表
     */
    List<EmployeeVO> selectEmployeeList(SysUser user);

    /**
     * 修改用户闲忙状态
     * @param user 用户信息
     * @return 结果
     */
    int updateUserState(SysUser user);
}