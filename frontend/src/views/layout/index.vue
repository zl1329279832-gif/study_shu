<template>
  <el-container class="layout-container">
    <el-header>
      <div class="header-left">
        <div class="logo">
          <el-icon class="logo-icon"><Reading /></el-icon>
          图书管理系统
        </div>
        <el-icon class="collapse-btn" @click="handleCollapse">
          <Fold v-if="!isCollapse" />
          <Expand v-else />
        </el-icon>
      </div>
      
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <div class="user-info">
            <div class="user-avatar">
              <el-icon><User /></el-icon>
            </div>
            <span class="user-name">{{ userStore.realName }}</span>
            <span class="user-role">{{ getRoleLabel }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="userinfo">
                <el-icon><User /></el-icon>
                个人信息
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    
    <el-container>
      <el-aside :width="isCollapse ? '64px' : '200px'">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :unique-opened="true"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <el-menu-item index="/welcome">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          
          <template v-for="menu in userStore.menus" :key="menu.id">
            <el-sub-menu
              v-if="menu.children && menu.children.length > 0"
              :index="menu.path"
            >
              <template #title>
                <el-icon>
                  <component :is="menu.icon || 'Folder'" />
                </el-icon>
                <span>{{ menu.menuName }}</span>
              </template>
              <el-menu-item
                v-for="child in menu.children"
                :key="child.id"
                :index="child.path"
              >
                <el-icon>
                  <component :is="child.icon || 'Document'" />
                </el-icon>
                <span>{{ child.menuName }}</span>
              </el-menu-item>
            </el-sub-menu>
            
            <el-menu-item
              v-else
              :index="menu.path"
              v-if="menu.path"
            >
              <el-icon>
                <component :is="menu.icon || 'Document'" />
              </el-icon>
              <template #title>{{ menu.menuName }}</template>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>
      
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

const getRoleLabel = computed(() => {
  const roles = userStore.roles
  if (roles.includes('ADMIN')) return '超级管理员'
  if (roles.includes('MANAGER')) return '管理员'
  if (roles.includes('USER')) return '普通用户'
  return ''
})

const handleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await userStore.logoutAction()
    ElMessage.success('退出成功')
    router.push('/login')
  }
}
</script>
