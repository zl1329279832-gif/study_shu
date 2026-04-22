import { defineStore } from 'pinia'
import { login, getUserInfo, logout } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userId: null,
    username: '',
    realName: '',
    roles: [],
    menus: []
  }),

  getters: {
    isAdmin: (state) => state.roles.includes('ADMIN'),
    isManager: (state) => state.roles.includes('MANAGER') || state.roles.includes('ADMIN'),
    menuRoutes: (state) => {
      return buildRoutes(state.menus)
    }
  },

  actions: {
    async loginAction(loginForm) {
      const res = await login(loginForm)
      const data = res.data
      
      this.token = data.token
      this.userId = data.userId
      this.username = data.username
      this.realName = data.realName
      this.roles = data.roles
      this.menus = data.menus

      localStorage.setItem('token', data.token)
      return data
    },

    async getUserInfoAction() {
      const res = await getUserInfo()
      const data = res.data
      
      this.userId = data.userId
      this.username = data.username
      this.realName = data.realName
      this.roles = data.roles
      this.menus = data.menus
      
      return data
    },

    async logoutAction() {
      try {
        await logout()
      } catch (e) {
        console.error('退出登录请求失败:', e)
      } finally {
        this.token = ''
        this.userId = null
        this.username = ''
        this.realName = ''
        this.roles = []
        this.menus = []
        localStorage.removeItem('token')
      }
    },

    logout() {
      this.token = ''
      this.userId = null
      this.username = ''
      this.realName = ''
      this.roles = []
      this.menus = []
      localStorage.removeItem('token')
    }
  }
})

function buildRoutes(menus) {
  const routes = []
  
  menus.forEach(menu => {
    if (menu.menuType === 1 && menu.path) {
      const route = {
        path: menu.path,
        name: menu.path.replace(/\//g, '-').substring(1),
        meta: {
          title: menu.menuName,
          icon: menu.icon
        }
      }
      
      if (menu.component) {
        route.component = () => import(`@/views${menu.component}.vue`)
      }
      
      if (menu.children && menu.children.length > 0) {
        route.children = buildRoutes(menu.children)
      }
      
      routes.push(route)
    }
  })
  
  return routes
}
