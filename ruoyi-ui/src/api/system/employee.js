import request from '@/utils/request'

// 查询打手列表（dept_id=102的用户）
export function listEmployee(query) {
  return request({
    url: '/system/employee/list',
    method: 'get',
    params: query
  })
}

// 导出打手列表
export function exportEmployee(query) {
  return request({
    url: '/system/employee/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 修改打手闲忙状态
export function changeEmployeeState(data) {
  return request({
    url: '/system/employee/changeState',
    method: 'put',
    data: data
  })
}

// 打手初始化
export function initViewEmployee(query) {
  return request({
    url: '/system/employee/initView',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
