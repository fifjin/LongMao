package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.vo.EmployeeVO;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.mapper.EmployeeMapper;
import com.ruoyi.system.service.IEmployeeService;

@Service
public class EmployeeServiceImpl implements IEmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public List<EmployeeVO> selectEmployeeList(SysUser user) {
        return employeeMapper.selectEmployeeList(user);
    }

    @Override
    public int updateUserState(SysUser user) {
        return employeeMapper.updateUserState(user);
    }
}