<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">图书列表</span>
      <el-button type="primary" @click="handleAdd" v-if="userStore.isManager">
        <el-icon><Plus /></el-icon>
        新增图书
      </el-button>
    </div>
    
    <el-form :inline="true" :model="searchForm" style="margin-bottom: 20px;">
      <el-form-item label="图书名称">
        <el-input v-model="searchForm.bookName" placeholder="请输入图书名称" clearable />
      </el-form-item>
      <el-form-item label="作者">
        <el-input v-model="searchForm.author" placeholder="请输入作者" clearable />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable>
          <el-option label="文学小说" :value="1" />
          <el-option label="科技教育" :value="2" />
          <el-option label="历史人文" :value="3" />
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
    
    <el-table :data="bookList" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="bookName" label="图书名称" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="isbn" label="ISBN" width="150" />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="publisher" label="出版社" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '可借' : '不可借' }}
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
      <el-table-column label="操作" width="100" fixed="right" v-else>
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleBorrow(row)">
            借阅
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
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const searchForm = reactive({
  bookName: '',
  author: '',
  categoryId: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 3
})

const bookList = ref([
  { id: 1, bookName: '红楼梦', author: '曹雪芹', isbn: '9787020002207', categoryName: '文学小说', publisher: '人民文学出版社', stock: 50, status: 1 },
  { id: 2, bookName: '西游记', author: '吴承恩', isbn: '9787020008735', categoryName: '文学小说', publisher: '人民文学出版社', stock: 45, status: 1 },
  { id: 3, bookName: 'JavaScript高级程序设计', author: 'Zakas', isbn: '9787115545381', categoryName: '科技教育', publisher: '人民邮电出版社', stock: 30, status: 1 }
])

const handleSearch = () => {
  ElMessage.info('搜索功能开发中...')
}

const handleReset = () => {
  searchForm.bookName = ''
  searchForm.author = ''
  searchForm.categoryId = null
  ElMessage.success('已重置')
}

const handleAdd = () => {
  ElMessage.info('新增图书功能开发中...')
}

const handleEdit = (row) => {
  ElMessage.info(`编辑图书: ${row.bookName}`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除图书 ${row.bookName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
  }).catch(() => {})
}

const handleBorrow = (row) => {
  ElMessageBox.confirm(`确定要借阅图书 ${row.bookName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('借阅成功')
  }).catch(() => {})
}
</script>
