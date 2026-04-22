<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">角色管理</span>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增角色
      </el-button>
    </div>
    
    <el-table :data="roleList" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="roleName" label="角色名称" />
      <el-table-column prop="roleCode" label="角色编码" />
      <el-table-column prop="description" label="角色描述" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="success" link size="small" @click="handlePermission(row)">
            权限配置
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

const roleList = ref([
  { id: 1, roleName: '超级管理员', roleCode: 'ADMIN', description: '系统超级管理员，拥有所有权限', status: 1 },
  { id: 2, roleName: '管理员', roleCode: 'MANAGER', description: '普通管理员，拥有图书管理权限', status: 1 },
  { id: 3, roleName: '普通用户', roleCode: 'USER', description: '普通用户，只能查询和借阅图书', status: 1 }
])

const handleAdd = () => {
  ElMessage.info('新增角色功能开发中...')
}

const handleEdit = (row) => {
  ElMessage.info(`编辑角色: ${row.roleName}`)
}

const handlePermission = (row) => {
  ElMessage.info(`配置角色权限: ${row.roleName}`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除角色 ${row.roleName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
  }).catch(() => {})
}
</script>
