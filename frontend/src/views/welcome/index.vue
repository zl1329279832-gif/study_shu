<template>
  <div class="page-container welcome-page">
    <h1 class="welcome-title">欢迎使用图书管理系统</h1>
    <p class="welcome-desc">当前登录用户: <strong>{{ userStore.realName }}</strong> ({{ getRoleLabel }})</p>
    
    <div class="welcome-cards">
      <div class="welcome-card" v-if="userStore.isAdmin">
        <el-icon class="card-icon"><Setting /></el-icon>
        <h3 class="card-title">系统管理</h3>
        <p class="card-desc">管理用户、角色和菜单权限</p>
      </div>
      
      <div class="welcome-card" v-if="userStore.isManager">
        <el-icon class="card-icon"><Reading /></el-icon>
        <h3 class="card-title">图书管理</h3>
        <p class="card-desc">管理图书信息和分类</p>
      </div>
      
      <div class="welcome-card">
        <el-icon class="card-icon"><Tickets /></el-icon>
        <h3 class="card-title">借阅管理</h3>
        <p class="card-desc">查看和管理借阅记录</p>
      </div>
    </div>
    
    <el-row :gutter="20" style="margin-top: 40px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div style="font-weight: 600;">系统说明</div>
          </template>
          <div style="line-height: 2;">
            <p><strong>超级管理员:</strong> 拥有所有菜单权限，可以管理用户、角色、菜单、图书和借阅。</p>
            <p><strong>管理员:</strong> 拥有图书管理和借阅管理权限。</p>
            <p><strong>普通用户:</strong> 只能查看图书和自己的借阅记录。</p>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div style="font-weight: 600;">测试账号</div>
          </template>
          <el-table :data="testAccounts" stripe>
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="password" label="密码" />
            <el-table-column prop="role" label="角色" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const getRoleLabel = computed(() => {
  const roles = userStore.roles
  if (roles.includes('ADMIN')) return '超级管理员'
  if (roles.includes('MANAGER')) return '管理员'
  if (roles.includes('USER')) return '普通用户'
  return ''
})

const testAccounts = [
  { username: 'admin', password: '123456', role: '超级管理员' },
  { username: 'manager', password: '123456', role: '管理员' },
  { username: 'user', password: '123456', role: '普通用户' }
]
</script>
