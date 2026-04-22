<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">我的借阅</span>
    </div>
    
    <el-form :inline="true" :model="searchForm" style="margin-bottom: 20px;">
      <el-form-item label="图书名称">
        <el-input v-model="searchForm.bookName" placeholder="请输入图书名称" clearable />
      </el-form-item>
      <el-form-item label="借阅状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
          <el-option label="借阅中" :value="1" />
          <el-option label="已归还" :value="2" />
          <el-option label="已逾期" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </el-form-item>
    </el-form>
    
    <el-table :data="borrowList" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="bookName" label="图书名称" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="borrowTime" label="借阅时间" width="180" />
      <el-table-column prop="expectReturnTime" label="预计归还时间" width="180" />
      <el-table-column prop="actualReturnTime" label="实际归还时间" width="180">
        <template #default="{ row }">
          {{ row.actualReturnTime || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button 
            type="primary" 
            link 
            size="small" 
            @click="handleReturn(row)"
            v-if="row.status === 1 || row.status === 3"
          >
            归还
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      v-model:current-page="pagination.pageNum"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top: 20px; justify-content: flex-end;"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const searchForm = reactive({
  bookName: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 2
})

const borrowList = ref([
  { 
    id: 1, 
    bookName: '红楼梦', 
    author: '曹雪芹',
    borrowTime: '2024-01-15 10:30:00', 
    expectReturnTime: '2024-02-15 10:30:00',
    actualReturnTime: null,
    status: 1
  },
  { 
    id: 2, 
    bookName: '西游记', 
    author: '吴承恩',
    borrowTime: '2024-01-10 14:20:00', 
    expectReturnTime: '2024-02-10 14:20:00',
    actualReturnTime: '2024-02-08 09:15:00',
    status: 2
  }
])

const getStatusType = (status) => {
  const map = {
    1: 'primary',
    2: 'success',
    3: 'danger'
  }
  return map[status] || 'info'
}

const getStatusLabel = (status) => {
  const map = {
    1: '借阅中',
    2: '已归还',
    3: '已逾期'
  }
  return map[status] || '未知'
}

const handleSearch = () => {
  ElMessage.info('搜索功能开发中...')
}

const handleReset = () => {
  searchForm.bookName = ''
  searchForm.status = null
  ElMessage.success('已重置')
}

const handleReturn = (row) => {
  ElMessageBox.confirm(`确定要归还图书 ${row.bookName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('归还成功')
  }).catch(() => {})
}
</script>
