import request from '@/utils/request'

// 查询订单标签列表
export function listOrderLabel(query) {
  return request({
    url: '/system/orderLabel/list',
    method: 'get',
    params: query
  })
}

// 查询订单标签详细
export function getOrderLabel(orderLabelId) {
  return request({
    url: '/system/orderLabel/' + orderLabelId,
    method: 'get'
  })
}

// 新增订单标签
export function addOrderLabel(data) {
  return request({
    url: '/system/orderLabel',
    method: 'post',
    data: data
  })
}

// 修改订单标签
export function updateOrderLabel(data) {
  return request({
    url: '/system/orderLabel',
    method: 'put',
    data: data
  })
}

// 删除订单标签
export function delOrderLabel(orderLabelId) {
  return request({
    url: '/system/orderLabel/' + orderLabelId,
    method: 'delete'
  })
}

// 导出订单标签列表
export function exportOrderLabel(query) {
  return request({
    url: '/system/orderLabel/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}

// 查询启用的订单标签（状态为1）
export function selectEnabledOrderLabels() {
  return request({
    url: '/system/orderLabel/listEnabled',
    method: 'get'
  })
}
