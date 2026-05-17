<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
          <el-button type="primary" @click="openCreateDialog" v-if="userStore.role === 'teacher'">创建课程</el-button>
        </div>
      </template>

      <el-table :data="courses" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="课程 ID" width="80" />
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="describe" label="课程描述" />
        <el-table-column prop="teacherId" label="教师 ID" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleJoin(scope.row)" v-if="userStore.role === 'student'">加入课程</el-button>
            <el-button size="small" @click="handleEdit(scope.row)" v-if="userStore.role === 'teacher'">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)" v-if="userStore.role === 'teacher'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="创建新课程" width="500">
      <el-form :model="form" label-width="120px">
        <el-form-item label="课程名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input v-model="form.describe" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCreateCourse">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const dialogVisible = ref(false)
const loading = ref(false)
const courses = ref([])

const form = reactive({
  name: '',
  describe: '',
  teacherId: null // 将在提交时设置为当前用户 ID
})

const fetchCourses = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/class')
    courses.value = response
  } catch (error) {
    console.error('获取课程失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCourses()
})

const openCreateDialog = () => {
  dialogVisible.value = true
}

const handleCreateCourse = async () => {
  try {
    // 假设当前登录用户的 ID 存储在 userStore 中，这里为了演示先写死为 1
    form.teacherId = 1 
    await api.post('/api/class', form)
    ElMessage.success('课程创建成功！')
    dialogVisible.value = false
    fetchCourses() // 重新加载列表
    Object.assign(form, { name: '', describe: '' })
  } catch (error) {
    console.error('创建课程失败:', error)
  }
}

const handleJoin = async (row) => {
  try {
    // 假设当前登录学生的 ID 存储在 userStore 中，这里为了演示先写死为 2
    const studentId = userStore.id || 2
    const joinData = { classId: row.id, studentId: studentId }
    await api.post('/api/class-joined', joinData)
    ElMessage.success(`成功加入课程: ${row.name}`)
  } catch (error) {
    console.error('加入课程失败:', error)
    ElMessage.error('加入课程失败，您可能已经加入该课程。')
  }
}

const handleEdit = (row) => {
  console.log('正在编辑:', row)
  ElMessage.info('编辑功能尚未实现。')
}

const handleDelete = async (row) => {
  try {
    await api.delete(`/api/class/${row.id}`)
    ElMessage.success('课程删除成功。')
    fetchCourses()
  } catch (error) {
    console.error('删除课程失败:', error)
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
