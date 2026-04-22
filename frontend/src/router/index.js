import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

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
      
      const dynamicRoutes = generateDynamicRoutes(userStore.menus)
      dynamicRoutes.forEach(route => {
        router.addRoute('Layout', route)
      })
      
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

function generateDynamicRoutes(menus) {
  const routes = []
  
  menus.forEach(menu => {
    if (menu.menuType === 1 && menu.path && menu.component) {
      const route = {
        path: menu.path.replace(/^\/[^\/]+/, ''),
        name: menu.path.replace(/\//g, '-').substring(1),
        component: () => import(`@/views${menu.component}.vue`),
        meta: {
          title: menu.menuName,
          icon: menu.icon
        }
      }
      
      if (menu.children && menu.children.length > 0) {
        const childRoutes = generateDynamicRoutes(menu.children)
        if (childRoutes.length > 0) {
          route.children = childRoutes
        }
      }
      
      routes.push(route)
    }
  })
  
  return routes
}

export default router
