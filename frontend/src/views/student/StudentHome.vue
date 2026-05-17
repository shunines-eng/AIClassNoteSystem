<template>
  <div class="student-home">
    <el-row :gutter="20">
      <!-- 欢迎信息和作业提醒 -->
      <el-col :span="24">
        <el-card class="welcome-card">
          <h2>欢迎回来, {{ username }}!</h2>
          <div class="assignment-reminder" v-if="pendingAssignments.length > 0">
            <el-alert
              title="待完成作业提醒"
              type="warning"
              :closable="false"
              show-icon>
              您有 {{ pendingAssignments.length }} 个待完成的笔记作业，请尽快处理。
            </el-alert>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <!-- 快捷入口 -->
      <el-col :span="16">
        <el-card header="功能快捷入口">
          <div class="quick-links">
            <el-button type="primary" @click="router.push('/student/courses')">
              <el-icon><Document /></el-icon>
              课程管理
            </el-button>
            <el-button type="success" @click="router.push('/student/notes')">
              <el-icon><Document /></el-icon>
              我的笔记
            </el-button>
            <el-button type="warning" @click="router.push('/blog/notes')">
              <el-icon><Share /></el-icon>
              笔记广场
            </el-button>
            <el-button type="info" @click="router.push('/student/profile')">
              <el-icon><User /></el-icon>
              个人信息
            </el-button>
          </div>
        </el-card>

        <!-- 热门开放笔记 -->
        <el-card header="热门公开笔记" class="mt-20">
          <el-table :data="hotNotes" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="authorName" label="作者" width="120" />
            <el-table-column prop="likes" label="点赞数" width="100" />
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button link type="primary" @click="viewNote(scope.row.id)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 待办作业列表 -->
      <el-col :span="8">
        <el-card header="待办作业">
          <div v-for="assignment in pendingAssignments" :key="assignment.id" class="assignment-item">
            <div class="assignment-info">
              <h4>{{ assignment.courseName }}</h4>
              <p>{{ assignment.title }}</p>
              <span class="deadline">截止日期: {{ assignment.deadline }}</span>
            </div>
            <el-button type="primary" size="small" @click="startAssignment(assignment)">去完成</el-button>
          </div>
          <el-empty v-if="pendingAssignments.length === 0" description="暂无待办作业" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
/**
 * StudentHome.vue
 * 学生端首页，展示热门笔记、功能入口、作业提醒。
 * 
 * 学习要点：
 * 1. reactive/ref: Vue 3 的响应式数据定义方式。
 * 2. onMounted: 生命周期钩子，用于页面加载时获取数据。
 * 3. useRouter: 路由跳转。
 * 4. useUserStore: 使用 Pinia 进行状态管理（用户名、角色等）。
 */
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import api from '../../api' // 导入封装好的 axios 实例
import { Document, Share, User } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const username = ref(userStore.username || '学生')

// 模拟假数据：热门笔记
const loading = ref(false)

const hotNotes = ref([])
const pendingAssignments = ref([])

/**
 * 获取首页数据的模拟方法
 */
const fetchHomeData = async () => {
  if (!userStore.id) return
  loading.value = true
  try {
    const res = await api.get('/api/student/home/data', { params: { userId: userStore.id } })
    if (res.code === 200) {
      hotNotes.value = res.data.hotNotes
      pendingAssignments.value = res.data.pendingAssignments
    }
  } catch (error) {
    console.error('获取首页数据失败', error)
  } finally {
    loading.value = false
  }
}

const viewNote = (id) => {
  router.push(`/blog/notes/${id}`)
}

const startAssignment = (assignment) => {
  // 携带作业标识进入课程详情
  router.push({
    name: 'student-course-detail',
    params: { id: assignment.classId },
    query: { assignmentId: assignment.classId  }
  })
}

onMounted(() => {
  fetchHomeData()
})
</script>

<style scoped>
.student-home {
  padding: 20px;
}
.mt-20 {
  margin-top: 20px;
}
.welcome-card {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}
.quick-links {
  display: flex;
  gap: 15px;
}
.assignment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}
.assignment-info h4 {
  margin: 0 0 5px 0;
}
.assignment-info p {
  margin: 0;
  font-size: 14px;
  color: #666;
}
.deadline {
  font-size: 12px;
  color: #999;
}
</style>
