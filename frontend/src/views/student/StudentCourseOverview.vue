<template>
  <div class="course-overview">
    <el-row :gutter="20">
      <el-col :span="18">
        <h2>已参加的课程</h2>
      </el-col>
      <el-col :span="6" class="text-right">
        <el-button type="primary" @click="openJoinDialog">加入新课程</el-button>
      </el-col>
    </el-row>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>

    <!-- 课程卡片列表 -->
    <div v-else>
      <el-row :gutter="20" class="mt-20">
        <el-col 
          :span="6" 
          v-for="course in courses" 
          :key="course.id"
          class="course-col"
        >
          <el-card 
            :body-style="{ padding: '0px' }" 
            class="course-card" 
            shadow="hover"
            @click="viewCourseDetail(course.id,course.teacherName)"
          >
            <div class="course-image">
              <el-icon :size="50" color="#409EFF"><Notebook /></el-icon>
            </div>
            <div style="padding: 14px;">
              <h3 class="course-title">{{ course.name }}</h3>
              <div class="course-description">
                {{ course.description || '暂无课程描述' }}
              </div>
              <div class="bottom clearfix">
                <span class="teacher-name">任课教师: {{ course.teacherName }}</span>
                <el-button link type="primary" class="button">查看详情</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <div v-if="courses.length === 0" class="empty-state">
        <el-empty description="您还没有加入任何课程">
          <el-button type="primary" @click="openJoinDialog">立即加入课程</el-button>
        </el-empty>
      </div>
    </div>

    <!-- 加入课程对话框 -->
    <el-dialog 
      v-model="showJoinDialog" 
      title="加入课程" 
      width="50%"
      :before-close="() => { showJoinDialog = false; showCourseListDialog = false }"
    >
      <div v-if="showCourseListDialog">
        <!-- 搜索框 -->
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程名称或教师"
          :prefix-icon="Search"
          class="search-input"
          clearable
        />
        
        <!-- 课程列表 -->
        <div class="course-list-container">
          <div 
            v-for="course in filteredCourses" 
            :key="course.id"
            class="course-list-item"
            :class="{ 'selected': joinForm.classId === course.id }"
            @click="joinForm.classId = course.id"
          >
            <div class="course-info">
              <h4>{{ course.className }}</h4>
              <p class="course-description">{{ course.describe || '暂无描述' }}</p>
              <div class="course-meta">
                <span class="teacher">教师: {{ course.teacherName }}</span>
                <span class="create-time">创建时间: {{ formatDate(course.createTime) }}</span>
              </div>
            </div>
            <div class="course-action">
              <el-tag v-if="joinForm.classId === course.id" type="success">已选择</el-tag>
              <el-button v-else type="primary" size="small" @click.stop="joinForm.classId = course.id">
                选择
              </el-button>
            </div>
          </div>
          
          <!-- 空状态 -->
          <div v-if="filteredCourses.length === 0" class="empty-course-list">
            <el-empty description="没有找到相关课程" />
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showJoinDialog = false; showCourseListDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleJoinCourse"
            :disabled="!joinForm.classId"
          >
            确认加入
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * StudentCourseOverview.vue
 * 学生课程概览页面 - 展示学生已加入的课程列表
 * 
 * 功能：
 * 1. 展示学生已加入的课程卡片
 * 2. 提供加入新课程的功能
 * 3. 点击课程卡片可查看课程详情
 * 
 * API接口：
 * - GET /api/class-joined/byStudentId/{studentId} - 获取学生已加入的课程
 * - POST /api/class-joined - 学生加入课程
 * - GET /api/class - 获取所有课程列表（用于选择加入）
 */
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Notebook, Search } from '@element-plus/icons-vue'
import { ElMessage, ElLoading } from 'element-plus'
import api from '../../api'
import studentApi from '../../api/student'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()
const showJoinDialog = ref(false)
const showCourseListDialog = ref(false)
const joinForm = reactive({
  classId: '',
})

// 学生已加入的课程列表
const courses = ref([])
// 所有可选课程列表（用于加入课程时选择）
const allCourses = ref([])
// 搜索关键词
const searchKeyword = ref('')
// 加载状态
const loading = ref(false)

/**
 * 计算属性：根据搜索关键词过滤课程
 */
const filteredCourses = computed(() => {
  if (!searchKeyword.value) return allCourses.value
  return allCourses.value.filter(course => 
    course.className.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
    (course.teacherName && course.teacherName.toLowerCase().includes(searchKeyword.value.toLowerCase()))
  )
})

/**
 * 获取学生已加入的课程列表
 */
const fetchStudentCourses = async () => {
  loading.value = true
  try {
    // 从userStore获取学生ID
    const studentId = userStore.id || 1 // 默认为1，实际应从登录用户获取
    console.log("studentId:"+studentId)
    const response = await studentApi.getStudentCourses(studentId)
    
    if (response.code === 200) {
      console.log("[StudentCourseOverview.vue]:fetchStudentCourses success")
      courses.value = response.data.map(item => ({
        id: item.teacherId,
        name: item.className,
        teacherName: item.teacherName,
        description: item.description,
        createTime: item.createTime
      }))
    } else {
      ElMessage.error(response.message || '获取课程列表失败')
    }
  } catch (error) {
    console.error('获取学生课程失败:', error)
    ElMessage.error('获取课程列表失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

/**
 * 获取所有可选课程列表
 */
const fetchAllCourses = async () => {
  try {
    const response = await studentApi.getAllCourses()
    if (Array.isArray(response)) {
      allCourses.value = response.map(course => ({
        id: course.id,
        className: course.className,
        teacherName: '未知教师', // 实际应从其他接口获取教师信息
        describe: course.describe,
        createTime: course.createTime
      }))
    }
  } catch (error) {
    console.error('获取所有课程失败:', error)
    ElMessage.error('获取课程列表失败')
  }
}

/**
 * 处理加入课程
 */
const handleJoinCourse = async () => {
  if (!joinForm.classId) {
    ElMessage.warning('请选择要加入的课程')
    return
  }

  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在加入课程...',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  try {
    const studentId = userStore.id || 1 // 默认为1，实际应从登录用户获取
    const joinData = {
      studentId: studentId,
      classId: parseInt(joinForm.classId)
    }
    
    const response = await studentApi.joinCourse(joinData)
    
    if (response.code === 200) {
      ElMessage.success('成功加入课程！')
      showJoinDialog.value = false
      showCourseListDialog.value = false
      joinForm.classId = ''
      // 重新加载学生课程列表
      await fetchStudentCourses()
    } else {
      ElMessage.error(response.message || '加入课程失败')
    }
  } catch (error) {
    console.error('加入课程失败:', error)
    ElMessage.error('加入课程失败，请稍后重试')
  } finally {
    loadingInstance.close()
  }
}

/**
 * 格式化日期
 * @param {string} dateString - 日期字符串
 * @returns {string} 格式化后的日期
 */
const formatDate = (dateString) => {
  if (!dateString) return '未知'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
  } catch (error) {
    return dateString
  }
}

/**
 * 查看课程详情
 */
const viewCourseDetail = (id,teacherName) => {
  router.push({
    path: `/student/courses/${id}`,
    query: {
      teacherName: teacherName
    }
  })
  console.log('teacherName:'+teacherName)
}

/**
 * 打开加入课程对话框
 */
const openJoinDialog = () => {
  showJoinDialog.value = true
  showCourseListDialog.value = true
  fetchAllCourses()
}

onMounted(() => {
  fetchStudentCourses()
})
</script>

<style scoped>
.course-overview {
  padding: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.text-right {
  text-align: right;
}

.loading-container {
  padding: 40px 0;
}

.course-col {
  margin-bottom: 20px;
}

.course-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #ebeef5;
  height: 100%;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.course-image {
  height: 120px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
}

.course-title {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-description {
  font-size: 13px;
  color: #606266;
  margin-bottom: 10px;
  line-height: 1.4;
  height: 36px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.teacher-name {
  font-size: 12px;
  color: #909399;
}

.bottom {
  margin-top: 13px;
  line-height: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-state {
  margin-top: 60px;
  text-align: center;
}

.search-input {
  margin-bottom: 20px;
}

.course-list-container {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.course-list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.2s;
}

.course-list-item:hover {
  background-color: #f5f7fa;
}

.course-list-item.selected {
  background-color: #ecf5ff;
  border-left: 3px solid #409eff;
}

.course-info {
  flex: 1;
}

.course-info h4 {
  margin: 0 0 8px 0;
  font-size: 15px;
  color: #303133;
}

.course-info .course-description {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.4;
}

.course-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}

.course-action {
  margin-left: 16px;
}

.empty-course-list {
  padding: 40px 0;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .course-col {
    width: 100%;
  }
  
  .course-list-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .course-action {
    margin-left: 0;
    margin-top: 10px;
    align-self: flex-end;
  }
  
  .course-meta {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
