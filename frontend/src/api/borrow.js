import request from '@/utils/request'

export function getAllRecords() {
  return request({
    url: '/borrow/list',
    method: 'get'
  })
}

export function getMyRecords() {
  return request({
    url: '/borrow/my',
    method: 'get'
  })
}

export function returnBook(id) {
  return request({
    url: `/borrow/return/${id}`,
    method: 'post'
  })
}
