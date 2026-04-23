import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const componentMap = {
  'book/list/index': () => import('@/views/book/list/index.vue'),
  'book/category/index': () => import('@/views/book/category/index.vue'),
  'borrow/record/index': () => import('@/views/borrow/record/index.vue'),
  'borrow/my/index': () => import('@/views/borrow/my/index.vue'),
  'system/user/index': () => import('@/views/system/user/index.vue'),
  'system/role/index': () => import('@/views/system/role/index.vue'),
  'system/menu/index': () => import('@/views/system/menu/index.vue')
}

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requireAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/welcome',
    meta: { requireAuth: true },
    children: [
      {
        path: 'welcome',
        name: 'Welcome',
        component: () => import('@/views/welcome/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'User' }
      },
      {
        path: 'book/list',
        name: 'BookList',
        component: componentMap['book/list/index'],
        meta: { title: '图书列表', icon: 'List' }
      },
      {
        path: 'book/category',
        name: 'BookCategory',
        component: componentMap['book/category/index'],
        meta: { title: '图书分类', icon: 'Folder' }
      },
      {
        path: 'borrow/record',
        name: 'BorrowRecord',
        component: componentMap['borrow/record/index'],
        meta: { title: '借阅记录', icon: 'Tickets' }
      },
      {
        path: 'borrow/my',
        name: 'MyBorrow',
        component: componentMap['borrow/my/index'],
        meta: { title: '我的借阅', icon: 'Document' }
      },
      {
        path: 'system/user',
        name: 'SystemUser',
        component: componentMap['system/user/index'],
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'system/role',
        name: 'SystemRole',
        component: componentMap['system/role/index'],
        meta: { title: '角色管理', icon: 'UserFilled' }
      },
      {
        path: 'system/menu',
        name: 'SystemMenu',
        component: componentMap['system/menu/index'],
        meta: { title: '菜单管理', icon: 'Menu' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  document.title = to.meta.title ? `${to.meta.title} - 图书管理系统` : '图书管理系统'

  if (to.meta.requireAuth === false) {
    if (token && to.path === '/login') {
      next('/')
    } else {
      next()
    }
    return
  }

  if (!token) {
    next('/login')
    return
  }

  if (!userStore.username) {
    try {
      await userStore.getUserInfoAction()
      next({ ...to, replace: true })
    } catch (error) {
      console.error('获取用户信息失败:', error)
      userStore.logout()
      next('/login')
    }
    return
  }

  next()
})

export default router
