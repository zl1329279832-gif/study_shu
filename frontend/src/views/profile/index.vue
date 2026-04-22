<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">个人中心</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" class="profile-tabs">
        <el-tab-pane label="个人信息" name="info">
          <div class="info-content">
            <div class="avatar-section">
              <el-avatar :size="100" class="user-avatar">
                <el-icon :size="50"><User /></el-icon>
              </el-avatar>
              <div class="user-basic">
                <h3 class="user-name">{{ userStore.realName }}</h3>
                <p class="user-role">
                  <el-tag :type="roleType">{{ roleLabel }}</el-tag>
                </p>
                <p class="user-username">用户名: {{ userStore.username }}</p>
              </div>
            </div>
            
            <el-divider />
            
            <el-form
              ref="infoFormRef"
              :model="infoForm"
              :rules="infoRules"
              label-width="100px"
              class="info-form"
            >
              <el-form-item label="用户名" prop="username">
                <el-input v-model="infoForm.username" disabled />
              </el-form-item>
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="infoForm.realName" placeholder="请输入真实姓名" />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="infoForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="infoForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdateInfo" :loading="infoLoading">
                  <el-icon><Edit /></el-icon>
                  修改信息
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="修改密码" name="password">
          <div class="password-content">
            <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="120px"
              class="password-form"
            >
              <el-form-item label="原密码" prop="oldPassword">
                <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="请输入原密码"
                  show-password
                />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码（至少6位）"
                  show-password
                />
              </el-form-item>
              <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdatePassword" :loading="passwordLoading">
                  <el-icon><Lock /></el-icon>
                  修改密码
                </el-button>
                <el-button @click="resetPasswordForm">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { updateUserInfo, updatePassword, getUserInfo } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('info')
const infoFormRef = ref(null)
const passwordFormRef = ref(null)
const infoLoading = ref(false)
const passwordLoading = ref(false)

const infoForm = reactive({
  username: '',
  realName: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const infoRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const roleLabel = computed(() => {
  const roles = userStore.roles
  if (roles.includes('ADMIN')) return '超级管理员'
  if (roles.includes('MANAGER')) return '管理员'
  if (roles.includes('USER')) return '普通用户'
  return '未知'
})

const roleType = computed(() => {
  const roles = userStore.roles
  if (roles.includes('ADMIN')) return 'danger'
  if (roles.includes('MANAGER')) return 'warning'
  return 'primary'
})

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    const data = res.data
    infoForm.username = data.username || ''
    infoForm.realName = data.realName || ''
    infoForm.phone = data.phone || ''
    infoForm.email = data.email || ''
  } catch (error) {
    console.error('获取用户信息失败:', error)
    infoForm.username = userStore.username
    infoForm.realName = userStore.realName || ''
  }
}

const handleUpdateInfo = async () => {
  const valid = await infoFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  infoLoading.value = true
  try {
    await updateUserInfo({
      realName: infoForm.realName,
      phone: infoForm.phone,
      email: infoForm.email
    })
    ElMessage.success('个人信息修改成功')
    userStore.realName = infoForm.realName
  } catch (error) {
    console.error('修改失败:', error)
  } finally {
    infoLoading.value = false
  }
}

const handleUpdatePassword = async () => {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  passwordLoading.value = true
  try {
    await ElMessageBox.confirm('确定要修改密码吗？修改后需要重新登录。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await updatePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
      confirmPassword: passwordForm.confirmPassword
    })
    
    ElMessage.success('密码修改成功，请重新登录')
    resetPasswordForm()
    
    setTimeout(async () => {
      await userStore.logoutAction()
      router.push('/login')
    }, 1000)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('修改密码失败:', error)
    }
  } finally {
    passwordLoading.value = false
  }
}

const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.resetFields()
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style lang="scss" scoped>
.profile-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 84px);
}

.profile-card {
  max-width: 800px;
  margin: 0 auto;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .card-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
    }
  }
}

.profile-tabs {
  :deep(.el-tabs__header) {
    margin-bottom: 24px;
  }
}

.info-content {
  .avatar-section {
    display: flex;
    align-items: center;
    padding: 20px;
    background: #f5f7fa;
    border-radius: 8px;
    
    .user-avatar {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    
    .user-basic {
      margin-left: 24px;
      
      .user-name {
        font-size: 20px;
        font-weight: 600;
        color: #303133;
        margin: 0 0 8px 0;
      }
      
      .user-role {
        margin: 0 0 8px 0;
      }
      
      .user-username {
        margin: 0;
        font-size: 14px;
        color: #909399;
      }
    }
  }
  
  .info-form {
    max-width: 500px;
    margin-top: 20px;
  }
}

.password-content {
  .password-form {
    max-width: 500px;
  }
}
</style>
