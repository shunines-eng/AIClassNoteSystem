<template>
  <div class="course-detail">
    <el-page-header @back="router.go(-1)" :title="course.name" />
    
    <el-row :gutter="20" class="mt-20">
      <!-- 课程简介 -->
      <el-col :span="16">
        <el-card header="课程简介">
          <div v-if="loading">
            <el-skeleton :rows="3" animated />
          </div>
          <div v-else>
            <p>{{ course.description }}</p>
            <div class="teacher-info">
              <el-avatar :size="40" :src="course.teacherAvatar" />
              <div class="teacher-name">
                <span>任课教师: {{ course.teacherName }}</span>
                <el-tag size="small" type="success" class="ml-10">已认证</el-tag>
                <el-button link type="danger" size="small" @click="handleReport(1, course.teacherId, '该用户')" class="ml-10">
                  <el-icon><Warning /></el-icon>举报该用户
                </el-button>
              </div>
            </div>
            <div class="course-actions mt-10">
              <el-button type="danger" plain size="small" @click="handleReport(3, courseId, '本课程')">
                <el-icon><Warning /></el-icon>举报本课程
              </el-button>
            </div>
          </div>
        </el-card>

        <!-- 作业详情 -->
        <el-card header="作业详情" class="mt-20">
          <div class="assignment-header">
            <h3 style="margin: 0">课程作业</h3>
            <el-button type="primary" size="small" @click="createFreeNote">
              <el-icon><DocumentAdd /></el-icon>
              创建自由笔记
            </el-button>
          </div>
          
          <div v-if="loading">
            <el-skeleton :rows="4" animated />
          </div>
          <div v-else>
            <el-table :data="assignments" style="width: 100%" v-if="assignments.length > 0">
              <el-table-column prop="title" label="作业名称" />
              <el-table-column prop="id" label="作业编号" width="120"></el-table-column>
              <el-table-column prop="deadline" label="截止时间" width="180" />
              <el-table-column prop="status" label="完成情况" width="120">
                <template #default="scope">
                  <el-tag :type="scope.row.status === '已完成' ? 'success' : 'warning'">
                    {{ scope.row.status }}
                  </el-tag>
                  <el-tag v-if="scope.row.isScore === 1" type="danger" size="small" class="ml-10">已批改</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="noteId" label="笔记编号（若有）" width="120"></el-table-column>
              <el-table-column label="操作" width="200">
                <template #default="scope">
                  <el-button link type="primary" @click="handleStartAssignment(scope.row)">进入作业</el-button>
                  <el-button v-if="scope.row.isScore === 1" link type="success" @click="viewTeacherComment(scope.row)">查看评语</el-button>
                </template>
              </el-table-column>
            </el-table>

    <!-- 教师评语对话框 -->
    <el-dialog v-model="showCommentDialog" title="教师批改评语" width="35%">
      <div v-if="currentComment" class="comment-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="得分">
            <el-tag type="danger" effect="dark">{{ currentComment.score }} 分</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评语">
            <div class="comment-content">{{ currentComment.comment }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="批改时间">
            {{ formatDate(currentComment.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <el-empty v-else description="暂无评语" />
      <template #footer>
        <el-button type="primary" @click="showCommentDialog = false">我知道了</el-button>
      </template>
    </el-dialog>

          </div>
        </el-card>
      </el-col>

      <!-- 侧边栏：优秀笔记 & AI 小测试 -->
      <el-col :span="8">
        <el-card header="本课程优秀笔记">
          <div v-if="loading">
            <el-skeleton :rows="3" animated />
          </div>
          <div v-else>
            <div v-for="note in excellentNotes" :key="note.id" class="note-card-small">
              <div class="note-info">
                <h4>{{ note.title }}</h4>
                <p>作者: {{ note.authorName }}</p>
              </div>
              <el-button link type="primary" @click="viewNote(note.id)">查看</el-button>
            </div>
            <el-empty v-if="excellentNotes.length === 0" description="暂无优秀笔记" />
          </div>
        </el-card>

<!--        <el-card header="AI 知识点小测试" class="mt-20">-->
<!--          <p class="ai-quiz-desc">基于课程内容和您的笔记，AI 为您生成了 3 道判断题，快来挑战吧！</p>-->
<!--          <el-button type="success" @click="startAIQuiz" style="width: 100%">开始测试</el-button>-->
<!--        </el-card>-->
      </el-col>
    </el-row>

    <!-- AI 测试对话框 -->
    <el-dialog v-model="showQuizDialog" title="AI 知识点测试" width="40%">
      <div v-if="!quizFinished" class="quiz-content">
        <div v-for="(q, index) in quizQuestions" :key="index" class="quiz-item mt-20">
          <p><strong>Q{{ index + 1 }}:</strong> {{ q.question }}</p>
          <el-radio-group v-model="q.userAnswer">
            <el-radio :label="true">正确</el-radio>
            <el-radio :label="false">错误</el-radio>
          </el-radio-group>
        </div>
      </div>
      <div v-else class="quiz-result text-center">
        <el-result
          icon="success"
          title="测试完成"
          :sub-title="`您的得分: ${quizScore} / ${quizQuestions.length}`"
        >
          <template #extra>
            <el-button type="primary" @click="showQuizDialog = false">返回课程</el-button>
          </template>
        </el-result>
      </div>
      <template #footer v-if="!quizFinished">
        <span class="dialog-footer">
          <el-button @click="showQuizDialog = false">取消</el-button>
          <el-button type="primary" @click="submitQuiz">提交答案</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 进入作业确认对话框 -->
    <el-dialog v-model="showConfirmDialog" title="进入作业" width="30%">
      <div class="assignment-requirement">
        <h4>作业要求:</h4>
        <p>{{ currentAssignment.requirement }}</p>
        <el-alert
          title="提示"
          type="info"
          description="点击确认后，将携带作业标识进入笔记编辑界面。您可以在那里完成您的作业笔记并提交。"
          show-icon
          :closable="false"
        />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showConfirmDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmStartAssignment">确认进入</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 创建自由笔记确认对话框 -->
    <el-dialog v-model="showFreeNoteDialog" title="创建自由笔记" width="30%">
      <div class="free-note-info">
        <p>您将创建一个不关联任何作业的自由笔记。</p>
        <p class="tip-text">自由笔记不会关联到具体作业，适用于课堂笔记、学习心得等。</p>
        <el-alert
          title="提示"
          type="info"
          description="自由笔记可以随时编辑，并且可以设置为公开供其他同学学习参考。"
          show-icon
          :closable="false"
        />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showFreeNoteDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmCreateFreeNote">创建笔记</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 举报对话框 -->
    <el-dialog v-model="showReportDialog" :title="'举报' + reportTargetName" width="30%">
      <el-form :model="reportForm" label-position="top">
        <el-form-item label="举报原因" required>
          <el-input
            v-model="reportForm.info"
            type="textarea"
            :rows="4"
            placeholder="请详细描述举报原因..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showReportDialog = false">取消</el-button>
          <el-button type="danger" @click="submitReport" :loading="reportLoading">提交举报</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * StudentCourseDetail.vue
 * 课程详情界面，展示简介、作业、优秀笔记。
 * 
 * 功能：
 * 1. 显示课程详细信息
 * 2. 展示课程作业列表
 * 3. 展示本课程优秀笔记
 * 4. 提供两种笔记创建方式：
 *    - 创建作业笔记（携带作业ID）
 *    - 创建自由笔记（不携带作业ID）
 * 
 * API接口：
 * - GET /api/class/{id} - 获取课程详情
 * - GET /api/task/byClassId/{classId} - 获取课程作业列表
 * - GET /api/note/excellent/{classId} - 获取课程优秀笔记
 */
import { ref, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { DocumentAdd, Warning } from '@element-plus/icons-vue'
import api from '../../api'
import studentApi, {getCourseAssignments, submitReport as apiSubmitReport} from '../../api/student'
import { useUserStore } from '../../store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const courseId = route.params.id
const showConfirmDialog = ref(false)
const showFreeNoteDialog = ref(false)
const showCommentDialog = ref(false)
const currentAssignment = ref({})
const currentComment = ref(null)

/**
 * 查看教师评语
 */
const viewTeacherComment = async (assignment) => {
  if (!assignment.noteId) return
  
  try {
    const response = await api.get(`/api/note-comments/note/${assignment.noteId}`)
    if (response && response.length > 0) {
      currentComment.value = response[0]
      showCommentDialog.value = true
    } else {
      ElMessage.info('教师尚未提交评语')
    }
  } catch (error) {
    console.error('获取评语失败:', error)
    ElMessage.error('获取评语失败')
  }
}

// 举报相关
const showReportDialog = ref(false)
const reportLoading = ref(false)
const reportTargetName = ref('')
const reportForm = reactive({
  type: 1,
  typeId: null,
  userId: userStore.id,
  info: ''
})

const handleReport = (type, typeId, targetName) => {
  reportForm.type = type
  reportForm.typeId = typeId
  reportTargetName.value = targetName
  reportForm.info = ''
  showReportDialog.ref = true // Wait, I should use showReportDialog.value = true
  showReportDialog.value = true
}

const submitReport = async () => {
  if (!reportForm.info.trim()) {
    ElMessage.warning('请填写举报原因')
    return
  }
  
  reportLoading.value = true
  try {
    const response = await apiSubmitReport({
      type: reportForm.type,
      typeId: reportForm.typeId,
      userId: userStore.id,
      info: reportForm.info
    })
    
    if (response.code === 200) {
      ElMessage.success('举报提交成功，我们将尽快处理')
      showReportDialog.value = false
    } else {
      ElMessage.error(response.message || '举报提交失败')
    }
  } catch (error) {
    console.error('举报提交失败:', error)
    ElMessage.error('举报提交失败，请稍后重试')
  } finally {
    reportLoading.value = false
  }
}

// 加载状态
const loading = ref(false)

// AI 测试相关
const showQuizDialog = ref(false)
const quizFinished = ref(false)
const quizScore = ref(0)
const quizQuestions = ref([
  { question: 'React Native 的所有组件都是原生的。', answer: false, userAnswer: null },
  { question: 'Flexbox 是移动端布局的首选方案。', answer: true, userAnswer: null },
  { question: 'AsyncStorage 可以存储无限大小的数据。', answer: false, userAnswer: null }
])

// 课程详情
const course = ref({
  id: courseId,
  name: '',
  description: '',
  teacherName: '',
  teacherAvatar: '',
  teacherId: null
})

// 作业列表
const assignments = ref([])

// 优秀笔记列表
const excellentNotes = ref([])

/**
 * 开始AI测试
 */
const startAIQuiz = () => {
  quizFinished.value = false
  quizScore.value = 0
  quizQuestions.value.forEach(q => q.userAnswer = null)
  showQuizDialog.value = true
}

/**
 * 提交AI测试答案
 */
const submitQuiz = () => {
  quizScore.value = quizQuestions.value.filter(q => q.userAnswer === q.answer).length
  quizFinished.value = true
}

/**
 * 获取课程详情
 */
const fetchCourseDetail = async () => {
  loading.value = true
  try {
    // 获取课程基本信息
    const courseResponse = await studentApi.getCourseDetail(courseId)
    if (courseResponse.id) {
      course.value = {
        id: courseResponse.id,
        name: courseResponse.className || '未命名课程',
        description: courseResponse.describe || '暂无课程描述',
        teacherName: route.query.teacherName || '未知',
        teacherAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        teacherId: courseResponse.teacherId || null
      }
    }

    // 获取课程作业列表
    const assignmentsResponse = await studentApi.getCourseAssignments(courseId,userStore.id)
    if (assignmentsResponse.code === 200) {
      assignments.value = assignmentsResponse.data.map(task => ({
        id: task.taskId,
        title: task.taskTitle || '未命名作业',
        deadline: task.deadline ? formatDate(task.deadline) : '暂无截止时间',
        status: task.noteId === null ? '待完成' : '已完成',
        requirement: task.description || '暂无作业要求',
        description: task.description || '',
        noteId : task.noteId,
        isScore: task.isScore
      }))
    }

    // 获取课程优秀笔记
    try {
      const notesResponse = await studentApi.getExcellentNotes(courseId)
      if (notesResponse.code === 200) {
        excellentNotes.value = notesResponse.data.map(note => ({
          id: note.id,
          title: note.title || '未命名笔记',
          authorName: note.authorName || '匿名用户'
        }))
      }
    } catch (error) {
      console.warn('获取优秀笔记失败，可能接口未实现:', error)
      excellentNotes.value = []
    }
  } catch (error) {
    console.error('获取课程详情失败:', error)
    ElMessage.error('获取课程信息失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

/**
 * 处理开始作业
 */
const handleStartAssignment = (assignment) => {
  console.log("[StudentCourseDetail.vue,handleStartAssignment]assignment:", assignment)
  currentAssignment.value = {assignment,userId: userStore.id}
  console.log("currentAssignment.value:", currentAssignment.value)
  showConfirmDialog.value = true
}

/**
 * 确认开始作业 - 携带作业ID跳转到笔记编辑页面
 */
const confirmStartAssignment = () => {
  showConfirmDialog.value = false
  // 携带作业ID跳转到笔记编辑页面
  console.log("[StudentCourseDetail.vue]currentAss:",
      "id:", currentAssignment.value.assignment?.id,
      "noteId:", currentAssignment.value.assignment?.noteId
  )

  router.push({
    name: 'note-editor',
    params: { id: 'new' },
    query: {
      taskId: currentAssignment.value.assignment?.id,  // 注意：这里要用assignment.id
      courseId: courseId,
      assignmentTitle: currentAssignment.value.assignment?.title,  // 这里也是
      noteId: currentAssignment.value.assignment?.noteId  // 这里也是
    }
  })
}

/**
 * 创建自由笔记 - 不携带作业ID跳转到笔记编辑页面
 */
const createFreeNote = () => {
  showFreeNoteDialog.value = true
}

/**
 * 确认创建自由笔记
 */
const confirmCreateFreeNote = () => {
  showFreeNoteDialog.value = false
  // 不携带作业ID跳转到笔记编辑页面
  router.push({
    name: 'note-editor',
    params: { id: 'new' },
    query: { 
      courseId: courseId,
      isFreeNote: 'true'
    }
  })
}

/**
 * 查看笔记详情
 */
const viewNote = (id) => {
  router.push(`/blog/notes/${id}`)
}

/**
 * 格式化日期
 */
const formatDate = (dateString) => {
  if (!dateString) return '暂无截止时间'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (error) {
    return dateString
  }
}

onMounted(() => {
  fetchCourseDetail()
})
</script>

<style scoped>
.course-detail {
  padding: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.ml-10 {
  margin-left: 10px;
}

.teacher-info {
  display: flex;
  align-items: center;
  margin-top: 20px;
}

.teacher-name {
  margin-left: 15px;
  display: flex;
  align-items: center;
}

.note-card-small {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.note-card-small:last-child {
  border-bottom: none;
}

.note-info h4 {
  margin: 0;
  font-size: 15px;
  color: #303133;
}

.note-info p {
  margin: 5px 0 0 0;
  font-size: 12px;
  color: #999;
}

.assignment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.assignment-requirement {
  padding: 10px;
}

.assignment-requirement h4 {
  margin-top: 0;
  color: #303133;
}

.assignment-requirement p {
  color: #606266;
  line-height: 1.6;
}

.free-note-info {
  padding: 10px;
}

.free-note-info p {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
}

.free-note-info .tip-text {
  color: #909399;
  font-size: 14px;
}

.quiz-content {
  max-height: 400px;
  overflow-y: auto;
}

.quiz-item {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.quiz-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.quiz-item p {
  margin-bottom: 10px;
  color: #303133;
}

.quiz-result {
  padding: 40px 0;
}

.text-center {
  text-align: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
