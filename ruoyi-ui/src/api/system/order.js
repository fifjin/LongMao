import request from '@/utils/request'

// 查询订单列表
export function listOrder(query) {
  return request({
    url: '/system/order/list',
    method: 'get',
    params: query
  })
}

// 查询抢单订单列表
export function grabOrderList(query) {
  return request({
    url: '/system/order/grabOrderList',
    method: 'get',
    params: query
  })
}

// 查询我的抢单订单列表
export function myGrabOrderList(query) {
  return request({
    url: '/system/order/myGrabOrderList',
    method: 'get',
    params: query
  })
}

// 查询订单详细
export function getOrder(orderId) {
  return request({
    url: '/system/order/' + orderId,
    method: 'get'
  })
}

// 查询抢单订单详细
export function getGrabOrder(orderId) {
  return request({
    url: '/system/order/' + orderId +'/grab',
    method: 'get'
  })
}

// 新增订单
export function addOrder(data) {
  return request({
    url: '/system/order',
    method: 'post',
    data: data
  })
}

// 修改订单
export function updateOrder(data) {
  return request({
    url: '/system/order',
    method: 'put',
    data: data
  })
}

// 删除订单
export function delOrder(orderId) {
  return request({
    url: '/system/order/' + orderId,
    method: 'delete'
  })
}

// 导出订单列表
export function exportOrder(query) {
  return request({
    url: '/system/order/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}

// 分配订单接手人
export function assignOrder(data) {
  return request({
    url: '/system/order/assign',
    method: 'put',
    data: data
  })
}

// 获取订单标签列表（状态为1的）
export function getEnabledLabels() {
  return request({
    url: '/system/orderLabel/enabled',
    method: 'get'
  })
}

// 抢单
export function grabOrder(data) {
  return request({
    url: '/system/order/grabOrder',
    method: 'post',
    data: data
  })
}
