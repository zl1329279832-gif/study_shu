<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">借阅记录</span>
    </div>
    
    <el-form :inline="true" :model="searchForm" style="margin-bottom: 20px;">
      <el-form-item label="用户名">
        <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
      </el-form-item>
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
    
    <el-table :data="filteredRecordList" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="bookName" label="图书名称" />
      <el-table-column prop="borrowTime" label="借阅时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.borrowTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="expectReturnTime" label="预计归还时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.expectReturnTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="actualReturnTime" label="实际归还时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.actualReturnTime) || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right" v-if="userStore.isManager">
        <template #default="{ row }">
          <el-button 
            type="success" 
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getAllRecords, returnBook } from '@/api/borrow'
import { formatDateTime } from '@/utils/date'

const userStore = useUserStore()

const searchForm = reactive({
  username: '',
  bookName: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const borrowList = ref([])

const filteredRecordList = computed(() => {
  let result = borrowList.value
  if (searchForm.username) {
    result = result.filter(item => item.username?.includes(searchForm.username))
  }
  if (searchForm.bookName) {
    result = result.filter(item => item.bookName?.includes(searchForm.bookName))
  }
  if (searchForm.status !== null && searchForm.status !== undefined) {
    result = result.filter(item => item.status === searchForm.status)
  }
  return result
})

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

const loadData = async () => {
  try {
    const res = await getAllRecords()
    borrowList.value = res.data || []
    pagination.total = borrowList.value.length
  } catch (error) {
    console.error('加载借阅记录失败', error)
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.bookName = ''
  searchForm.status = null
  pagination.pageNum = 1
}

const handleReturn = (row) => {
  ElMessageBox.confirm(`确定要归还图书 ${row.bookName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await returnBook(row.id)
      ElMessage.success('归还成功')
      loadData()
    } catch (error) {
      console.error('归还失败', error)
      ElMessage.error(error.message || '归还失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: bold;
}
</style>
