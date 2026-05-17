<template>
  <div class="admin-users">
    <el-row :gutter="20">
      <el-col :span="18">
        <h2>全平台用户信息管理</h2>
      </el-col>
      <el-col :span="6" class="text-right">
        <el-input v-model="searchQuery" placeholder="搜索用户名/姓名/学工号" @input="handleSearch">
          <template #append>
            <el-button :icon="Search" />
          </template>
        </el-input>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-table :data="users" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="realName" label="姓名" width="120" />
          <el-table-column prop="role" label="角色" width="100">
            <template #default="scope">
              <el-tag :type="getRoleType(scope.row.role)">
                {{ getRoleName(scope.row.role) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="电话" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 0 ? 'danger' : 'success'">
                {{ scope.row.status === 0 ? '屏蔽' : '正常' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="300">
            <template #default="scope">
              <el-button size="small" @click="editUser(scope.row)">编辑信息</el-button>
              <el-button 
                size="small" 
                :type="scope.row.status === 0 ? 'success' : 'danger'"
                @click="toggleUserStatus(scope.row)">
                {{ scope.row.status === 0 ? '解除' : '屏蔽' }}
              </el-button>
              <el-button
                size="small"
                type="warning"
                @click="viewUserReports(scope.row)">
                举报查阅
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="handlePageChange"
          />
        </div>
      </el-col>
    </el-row>

    <!-- 编辑用户信息对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑用户信息" width="35%">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="editForm.realName" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="学/教工号">
          <el-input v-model="editForm.number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="handleUpdateUser">确认保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * AdminUserManagement.vue
 * 管理员端用户管理界面。
 * 
 * 学习要点：
 * 1. el-pagination: 分页组件。
 * 2. el-tag: 标签组件，用于展示状态。
 * 3. Search: Element Plus 图标。
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import api from '../../api'

const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)
const showEditDialog = ref(false)
const editForm = reactive({
  id: null,
  username: '',
  realName: '',
  email: '',
  number: ''
})

const users = ref([])

const getRoleName = (role) => {
  const map = { 1: '学生', 2: '教师', 3: '管理员' }
  return map[role] || '未知'
}

const getRoleType = (role) => {
  const map = { 1: 'info', 2: 'warning', 3: 'success' }
  return map[role] || ''
}

const fetchUsers = async () => {
  try {
    const res = await api.get('/api/users/admin/list', { 
      params: { 
        page: currentPage.value, 
        size: pageSize.value, 
        query: searchQuery.value 
      } 
    })
    if (res.code === 200) {
      users.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取用户列表失败', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchUsers()
}

const editUser = (user) => {
  Object.assign(editForm, {
    id: user.id,
    username: user.username,
    realName: user.realName,
    phone: user.phone,
    userAccount: user.userAccount
  })
  showEditDialog.value = true
}

const handleUpdateUser = async () => {
  try {
    const res = await api.put(`/api/users/${editForm.id}`, editForm)
    if (res.code === 200) {
      ElMessage.success('用户信息已更新')
      showEditDialog.value = false
      fetchUsers()
    }
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const toggleUserStatus = async (user) => {
  const isBlocked = user.status === 0
  const action = isBlocked ? '解封' : '封禁'
  const newStatus = isBlocked ? 1 : 0
  
  try {
    await ElMessageBox.confirm(`确定要${action}用户 ${user.username} 吗？`, '提示', {
      type: 'warning'
    })
    const res = await api.put(`/api/users/${user.id}/status`, null, {
      params: { status: newStatus }
    })
    if (res.code === 200) {
      user.status = newStatus
      ElMessage.success(`用户已${action}`)
    }
  } catch (error) {
    // 用户取消
  }
}

const viewUserReports = (user) => {
  // 跳转到举报管理页面，并筛选该用户的举报
  ElMessage.info(`查看用户 ${user.username} 的举报记录，将跳转到举报管理页面`);
  // 实际应该跳转到举报管理页面并传递用户ID作为筛选条件
  // 这里简单提示
  // 在实际应用中，可以使用路由跳转：
  // router.push({ path: '/admin/reports', query: { userId: user.id } });
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.admin-users {
  padding: 20px;
}
.mt-20 {
  margin-top: 20px;
}
.text-right {
  text-align: right;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
