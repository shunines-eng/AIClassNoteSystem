<template>
  <div class="note-detail-view">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <!-- 笔记内容 -->
    <div v-else-if="note.id" class="note-content">
      <el-card>
        <template #header>
          <div class="note-header">
            <div class="note-title-section">
              <h1 class="note-title">{{ note.title || '无标题笔记' }}</h1>
              <div class="note-meta">
                <div class="author-info">
                  <el-avatar :size="32" :src="note.authorAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
                  <div class="author-details">
                    <span class="author-name">
                      {{ '用户编号：'+note.userId || '匿名id' }}
                      <el-button link type="danger" size="small" @click="handleReport(1, note.userId, '该用户')" class="ml-5">
                        <el-icon><Warning /></el-icon>举报
                      </el-button>
                    </span>
                    <span class="publish-date">{{ formatDate(note.updateTime) }}</span>
                  </div>
                </div>
                <div class="note-stats">
                  <div class="stat-item">
                    <el-icon><View /></el-icon>
                    <span>{{ viewCount || 0 }} 次阅读</span>
                  </div>
                  <div class="stat-item">
                    <el-icon><Star /></el-icon>
                    <span>{{ likeCount || 0 }} 个点赞</span>
                  </div>
                  <div class="stat-item">
                    <el-icon><ChatDotRound /></el-icon>
                    <span>{{ note.comments || 0 }} 条评论</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="note-tags">
              <el-tag v-if="note.taskId" type="success" size="small">
                <el-icon><Document /></el-icon> 作业笔记
              </el-tag>
              <el-tag v-else type="info" size="small">
                <el-icon><Edit /></el-icon> 自由笔记
              </el-tag>
              <el-tag v-if="note.courseName" type="warning" size="small">
                {{ note.courseName }}
              </el-tag>
            </div>
          </div>
        </template>

        <!-- 笔记内容区域 -->
        <div class="note-body">
          <v-md-editor 
            :model-value="note.content" 
            mode="preview"
            height="auto"
          ></v-md-editor>
        </div>

        <!-- 互动区域 -->
        <div class="interaction-section">
          <el-button 
            :type="hasLiked ? 'primary' : 'default'" 
            :loading="likeLoading"
            @click="handleLike"
            round
          >
            <el-icon><Star /></el-icon>
            {{ hasLiked ? '已点赞' : '点赞' }}
          </el-button>
          
          <el-button 
            v-if="isOwnNote"
            type="success"
            @click="handleEdit"
            round
          >
            <el-icon><Edit /></el-icon>
            编辑笔记
          </el-button>

          <el-button 
            type="danger"
            plain
            @click="handleReport(2, note.id, '本笔记')"
            round
          >
            <el-icon><Warning /></el-icon>
            举报本笔记
          </el-button>
          
          <el-button 
            type="info"
            @click="$router.back()"
            round
          >
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
      </el-card>

      <!-- 教师批改信息（仅个人笔记且已批改时显示） -->
      <el-card v-if="isOwnNote && note.isScore === 1 && noteComments.length > 0" class="comment-section">
        <template #header>
          <h3><el-icon><ChatDotRound /></el-icon> 教师批改</h3>
        </template>
        
        <div v-for="comment in noteComments" :key="comment.id" class="teacher-comment">
          <div class="comment-header">
            <div class="teacher-info">
              <el-avatar :size="40" :src="'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
              <div class="teacher-details">
                <span class="teacher-name">教师 {{ comment.teacherId }}</span>
                <span class="comment-date">{{ formatDate(comment.updateTime) }}</span>
              </div>
            </div>
            <div class="comment-score">
              <el-rate 
                v-model="comment.score" 
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value} 分"
              />
            </div>
          </div>
          
          <div class="comment-content">
            <v-md-editor 
              :model-value="comment.comment" 
              mode="preview"
              height="auto"
            ></v-md-editor>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 笔记不存在 -->
    <div v-else class="empty-state">
      <el-empty description="笔记不存在或已被删除">
        <el-button type="primary" @click="$router.back()">返回</el-button>
      </el-empty>
    </div>

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
import { ref, onMounted, computed, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  View, Star, ChatDotRound, Document, Edit, ArrowLeft, Warning
} from '@element-plus/icons-vue'
import studentApi, { submitReport as apiSubmitReport } from '../api/student'
import { useUserStore } from '../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const note = ref({})
const loading = ref(false)
const viewCount = ref(0)
const likeCount = ref(0)
const hasLiked = ref(false)
const likeLoading = ref(false)
const noteComments = ref([])

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

// 计算属性：判断是否是自己的笔记
const isOwnNote = computed(() => {
  return note.value.userId === userStore.id
})

/**
 * 加载笔记详情
 */
const loadNoteDetail = async () => {
  loading.value = true
  try {
    const noteId = route.params.id
    
    // 加载笔记详情
    const response = await studentApi.getNoteDetail(noteId)
    if (response && response.code === 200) {
      const data = response.data
      note.value = data.note
      
      // 加载互动统计
      await loadInteractionStats(noteId)
      
      // 如果是自己的笔记且已批改，加载批改信息
      if (isOwnNote.value && note.value.isScore === 1) {
        await loadNoteComments(noteId)
      }
      console.log("test!!")
    } else {
      ElMessage.error(response?.message || '笔记不存在')
    }
  } catch (error) {
    console.error('加载笔记失败:', error)
    ElMessage.error('加载笔记失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

/**
 * 加载互动统计
 */
const loadInteractionStats = async (noteId) => {
  try {
    // 获取互动统计（包含查看次数、点赞次数和用户互动状态）
    const statsResponse = await studentApi.getNoteInteractionStats(noteId, userStore.id)
    if (statsResponse) {
      viewCount.value = statsResponse.viewCount || 0
      likeCount.value = statsResponse.likeCount || 0
      hasLiked.value = statsResponse.hasLiked || false
    }
    
    // 记录查看
    await studentApi.viewNote(noteId, userStore.id)
    
  } catch (error) {
    console.error('加载互动统计失败:', error)
  }
}

/**
 * 加载笔记批改信息
 */
const loadNoteComments = async (noteId) => {
  try {
    const response = await studentApi.getNoteComments(noteId)
    if (response && Array.isArray(response)) {
      noteComments.value = response
    }
  } catch (error) {
    console.error('加载批改信息失败:', error)
  }
}

/**
 * 处理点赞/取消点赞
 */
const handleLike = async () => {
  likeLoading.value = true
  try {
    const response = await studentApi.toggleNoteLike(route.params.id, userStore.id)
    if (response) {
      hasLiked.value = response.type === 1
      likeCount.value = hasLiked.value ? likeCount.value + 1 : Math.max(0, likeCount.value - 1)
      ElMessage.success(hasLiked.value ? '点赞成功' : '已取消点赞')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    likeLoading.value = false
  }
}

/**
 * 处理编辑笔记
 */
const handleEdit = () => {
  router.push({
    name: 'note-editor',
    params: { id: note.value.id },
    query: {
      noteId: note.value.id,
      taskId: note.value.taskId,
      courseId: note.value.courseId
    }
  })
}

/**
 * 格式化日期
 */
const formatDate = (dateString) => {
  if (!dateString) return '未知时间'
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
  loadNoteDetail()
})
</script>

<style lang="scss" scoped>
.note-detail-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.loading-container {
  padding: 40px 0;
}

.note-content {
  .note-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;
    
    .note-title-section {
      flex: 1;
      
      .note-title {
        margin: 0 0 16px 0;
        font-size: 28px;
        color: #303133;
        line-height: 1.3;
      }
      
      .note-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .author-info {
          display: flex;
          align-items: center;
          gap: 12px;
          
          .author-details {
            display: flex;
            flex-direction: column;
            
            .author-name {
              font-weight: 500;
              color: #303133;
              font-size: 14px;
            }
            
            .publish-date {
              font-size: 12px;
              color: #909399;
              margin-top: 2px;
            }
          }
        }
        
        .note-stats {
          display: flex;
          gap: 24px;
          
          .stat-item {
            display: flex;
            align-items: center;
            gap: 6px;
            color: #606266;
            font-size: 14px;
            
            .el-icon {
              color: #909399;
            }
          }
        }
      }
    }
    
    .note-tags {
      display: flex;
      gap: 8px;
      flex-shrink: 0;
      margin-left: 20px;
    }
  }
  
  .note-body {
    margin: 24px 0;
    min-height: 300px;
    
    :deep(.v-md-editor) {
      border: none;
      box-shadow: none;
      background-color: transparent;
    }
  }
  
  .interaction-section {
    display: flex;
    gap: 12px;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid #ebeef5;
  }
}

.comment-section {
  margin-top: 24px;
  
  .teacher-comment {
    padding: 16px;
    border-radius: 8px;
    background-color: #f8f9fa;
    margin-bottom: 16px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .comment-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      
      .teacher-info {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .teacher-details {
          display: flex;
          flex-direction: column;
          
          .teacher-name {
            font-weight: 500;
            color: #303133;
            font-size: 14px;
          }
          
          .comment-date {
            font-size: 12px;
            color: #909399;
            margin-top: 2px;
          }
        }
      }
      
      .comment-score {
        display: flex;
        align-items: center;
      }
    }
    
    .comment-content {
      :deep(.v-md-editor) {
        border: none;
        box-shadow: none;
        background-color: transparent;
        font-size: 14px;
        line-height: 1.6;
      }
    }
  }
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .note-header {
    flex-direction: column;
    gap: 16px;
    
    .note-tags {
      margin-left: 0 !important;
      align-self: flex-start;
    }
  }
  
  .note-meta {
    flex-direction: column;
    align-items: flex-start !important;
    gap: 16px;
    
    .note-stats {
      width: 100%;
      justify-content: space-between;
    }
  }
  
  .interaction-section {
    flex-direction: column;
    
    .el-button {
      width: 100%;
    }
  }
}
</style>
