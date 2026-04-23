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
          <el-option 
            v-for="cat in categoryList" 
            :key="cat.id" 
            :label="cat.categoryName" 
            :value="cat.id" 
          />
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
    
    <el-table :data="filteredBookList" stripe>
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
          <el-button 
            type="primary" 
            link 
            size="small" 
            @click="handleBorrow(row)"
            :disabled="row.status !== 1 || row.stock <= 0"
          >
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
    
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="图书名称" prop="bookName">
          <el-input v-model="formData.bookName" placeholder="请输入图书名称" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="formData.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="formData.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="formData.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%;">
            <el-option 
              v-for="cat in categoryList" 
              :key="cat.id" 
              :label="cat.categoryName" 
              :value="cat.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="formData.stock" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">可借</el-radio>
            <el-radio :label="0">不可借</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getBookList, addBook, updateBook, deleteBook, borrowBook } from '@/api/book'
import { getCategoryList } from '@/api/category'

const userStore = useUserStore()
const formRef = ref(null)

const searchForm = reactive({
  bookName: '',
  author: '',
  categoryId: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const bookList = ref([])
const categoryList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)

const formData = reactive({
  id: null,
  bookName: '',
  author: '',
  isbn: '',
  publisher: '',
  categoryId: null,
  stock: 0,
  status: 1
})

const formRules = {
  bookName: [{ required: true, message: '请输入图书名称', trigger: 'blur' }]
}

const filteredBookList = computed(() => {
  let result = bookList.value
  if (searchForm.bookName) {
    result = result.filter(item => item.bookName?.includes(searchForm.bookName))
  }
  if (searchForm.author) {
    result = result.filter(item => item.author?.includes(searchForm.author))
  }
  if (searchForm.categoryId) {
    result = result.filter(item => item.categoryId === searchForm.categoryId)
  }
  return result
})

const loadData = async () => {
  try {
    const res = await getBookList()
    bookList.value = res.data || []
    pagination.total = bookList.value.length
  } catch (error) {
    console.error('加载图书列表失败', error)
  }
}

const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    categoryList.value = res.data || []
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
}

const handleReset = () => {
  searchForm.bookName = ''
  searchForm.author = ''
  searchForm.categoryId = null
  pagination.pageNum = 1
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增图书'
  Object.assign(formData, {
    id: null,
    bookName: '',
    author: '',
    isbn: '',
    publisher: '',
    categoryId: null,
    stock: 0,
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑图书'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除图书 ${row.bookName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteBook(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除失败', error)
    }
  }).catch(() => {})
}

const handleBorrow = (row) => {
  ElMessageBox.confirm(`确定要借阅图书 ${row.bookName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await borrowBook(row.id)
      ElMessage.success('借阅成功')
      loadData()
    } catch (error) {
      console.error('借阅失败', error)
      ElMessage.error(error.message || '借阅失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await updateBook(formData)
      ElMessage.success('更新成功')
    } else {
      await addBook(formData)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败', error)
    if (error !== false) {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

onMounted(() => {
  loadData()
  loadCategories()
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
