import request from '@/utils/request'

// 查询用户列表
export function seckillProductList(query) {
  return request({
    url: '/seckill/seckillProduct/queryByTimeForJob',
    method: 'get',
    params: query
  })
}
