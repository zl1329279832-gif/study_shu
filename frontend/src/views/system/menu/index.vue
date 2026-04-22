<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">菜单管理</span>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增菜单
      </el-button>
    </div>
    
    <el-table :data="menuList" row-key="id" default-expand-all>
      <el-table-column prop="menuName" label="菜单名称" />
      <el-table-column prop="icon" label="图标" width="100">
        <template #default="{ row }">
          <el-icon v-if="row.icon">
            <component :is="row.icon" />
          </el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="path" label="路由路径" />
      <el-table-column prop="component" label="组件路径" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column prop="menuType" label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="row.menuType === 1 ? 'primary' : 'success'">
            {{ row.menuType === 1 ? '菜单' : '按钮' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const menuList = ref([
  {
    id: 1, menuName: '系统管理', icon: 'Setting', path: '/system', component: '', sortOrder: 1, menuType: 1, status: 1,
    children: [
      { id: 2, menuName: '用户管理', icon: 'User', path: '/system/user', component: 'system/user/index', sortOrder: 1, menuType: 1, status: 1 },
      { id: 3, menuName: '角色管理', icon: 'Stamp', path: '/system/role', component: 'system/role/index', sortOrder: 2, menuType: 1, status: 1 },
      { id: 4, menuName: '菜单管理', icon: 'Menu', path: '/system/menu', component: 'system/menu/index', sortOrder: 3, menuType: 1, status: 1 }
    ]
  },
  {
    id: 5, menuName: '图书管理', icon: 'Reading', path: '/book', component: '', sortOrder: 2, menuType: 1, status: 1,
    children: [
      { id: 6, menuName: '图书列表', icon: 'List', path: '/book/list', component: 'book/list/index', sortOrder: 1, menuType: 1, status: 1 },
      { id: 7, menuName: '图书分类', icon: 'Grid', path: '/book/category', component: 'book/category/index', sortOrder: 2, menuType: 1, status: 1 }
    ]
  },
  {
    id: 8, menuName: '借阅管理', icon: 'Tickets', path: '/borrow', component: '', sortOrder: 3, menuType: 1, status: 1,
    children: [
      { id: 9, menuName: '借阅记录', icon: 'Document', path: '/borrow/record', component: 'borrow/record/index', sortOrder: 1, menuType: 1, status: 1 },
      { id: 10, menuName: '我的借阅', icon: 'User', path: '/borrow/my', component: 'borrow/my/index', sortOrder: 2, menuType: 1, status: 1 }
    ]
  }
])

const handleAdd = () => {
  ElMessage.info('新增菜单功能开发中...')
}

const handleEdit = (row) => {
  ElMessage.info(`编辑菜单: ${row.menuName}`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除菜单 ${row.menuName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
  }).catch(() => {})
}
</script>
