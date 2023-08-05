import request from '@/utils/request'

// 查询秒杀商品信息列表
export function intoSeckillQueue(query) {
  return request({
    url: '/seckill/seckillOrder/intoSeckillQueue',
    method: 'get',
    params: query
  })
}


