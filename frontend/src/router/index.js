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
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'User' }
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
  
  const flattenMenus = (menuList) => {
    menuList.forEach(menu => {
      if (menu.menuType === 1 && menu.path && menu.component) {
        const routePath = menu.path.substring(1)
        const route = {
          path: routePath,
          name: menu.path.replace(/\//g, '-').substring(1),
          component: () => import(`@/views${menu.component}.vue`),
          meta: {
            title: menu.menuName,
            icon: menu.icon
          }
        }
        routes.push(route)
      }
      
      if (menu.children && menu.children.length > 0) {
        flattenMenus(menu.children)
      }
    })
  }
  
  flattenMenus(menus)
  
  return routes
}

export default router
