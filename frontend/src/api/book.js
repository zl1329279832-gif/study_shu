import request from '@/utils/request'

export function getBookList() {
  return request({
    url: '/book/list',
    method: 'get'
  })
}

export function getBookById(id) {
  return request({
    url: `/book/${id}`,
    method: 'get'
  })
}

export function addBook(data) {
  return request({
    url: '/book',
    method: 'post',
    data
  })
}

export function updateBook(data) {
  return request({
    url: '/book',
    method: 'put',
    data
  })
}

export function deleteBook(id) {
  return request({
    url: `/book/${id}`,
    method: 'delete'
  })
}

export function borrowBook(id) {
  return request({
    url: `/book/borrow/${id}`,
    method: 'post'
  })
}
