<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">图书分类</span>
      <el-button type="primary" @click="handleAdd" v-if="userStore.isManager">
        <el-icon><Plus /></el-icon>
        新增分类
      </el-button>
    </div>
    
    <el-table :data="categoryList" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="categoryName" label="分类名称" />
      <el-table-column prop="categoryCode" label="分类编码" />
      <el-table-column prop="description" label="分类描述" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right" v-if="userStore.isManager">
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
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const categoryList = ref([
  { id: 1, categoryName: '文学小说', categoryCode: 'LITERATURE', description: '文学类小说作品', sortOrder: 1, status: 1 },
  { id: 2, categoryName: '科技教育', categoryCode: 'TECH', description: '科技、教育类书籍', sortOrder: 2, status: 1 },
  { id: 3, categoryName: '历史人文', categoryCode: 'HISTORY', description: '历史、人文类书籍', sortOrder: 3, status: 1 },
  { id: 4, categoryName: '经济管理', categoryCode: 'ECONOMY', description: '经济、管理类书籍', sortOrder: 4, status: 1 }
])

const handleAdd = () => {
  ElMessage.info('新增分类功能开发中...')
}

const handleEdit = (row) => {
  ElMessage.info(`编辑分类: ${row.categoryName}`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除分类 ${row.categoryName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
  }).catch(() => {})
}
</script>
