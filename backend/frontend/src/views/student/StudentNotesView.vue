<template>
  <div class="student-notes-view">
    <div class="page-header">
      <h2>我的笔记</h2>
      <p class="page-description">管理您的所有笔记</p>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索笔记标题或内容..."
            :prefix-icon="Search"
            clearable
            @input="handleSearch"
          />
        </el-col>
        <el-col :span="8">
          <el-select
            v-model="filterType"
            placeholder="笔记类型"
            clearable
            @change="handleFilter"
            style="width: 100%"
          >
            <el-option label="全部笔记" value="" />
            <el-option label="作业笔记" value="assignment" />
            <el-option label="自由笔记" value="free" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-select
            v-model="sortBy"
            placeholder="排序方式"
            @change="handleSort"
            style="width: 100%"
          >
            <el-option label="最新创建" value="newest" />
            <el-option label="最早创建" value="oldest" />
            <el-option label="最近更新" value="recently_updated" />
          </el-select>
        </el-col>
      </el-row>
    </div>

    <!-- 创建新笔记按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="createNewNote">
        <el-icon><Plus /></el-icon>
        创建新笔记
      </el-button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>

    <!-- 笔记列表 -->
    <div v-else>
      <el-row :gutter="20">
        <el-col 
          :span="6" 
          v-for="note in filteredNotes" 
          :key="note.id"
          class="note-col"
        >
          <el-card 
            class="note-card" 
            shadow="hover"
          >
            <div class="note-header">
              <el-tag v-if="note.taskId" type="success" size="small">作业笔记</el-tag>
              <el-tag v-else type="info" size="small">自由笔记</el-tag>
              <span class="note-date">{{ formatDate(note.createTime) }}</span>
            </div>
            
            <div class="note-title" @click="viewNote(note.id)">
              {{ note.title || '未命名笔记' }}
            </div>
            <div class="note-summary">
              {{ note.summary || note.content?.substring(0, 100) + '...' || '暂无摘要' }}
            </div>
            
            <div class="note-meta">
              <div class="meta-item">
                <el-icon><View /></el-icon>
                <span>{{ note.views || 0 }}</span>
              </div>
              <div class="meta-item">
                <el-icon><Star /></el-icon>
                <span>{{ note.likes || 0 }}</span>
              </div>
              <div class="meta-item">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ note.comments || 0 }}</span>
              </div>
            </div>
            
            <div class="note-course" v-if="note.courseName">
              <el-tag size="small" type="warning">{{ note.courseName }}</el-tag>
            </div>
            
            <div class="note-actions">
              <el-button-group>
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="viewNote(note.id)"
                >
                  查看
                </el-button>
                <el-button 
                  type="success" 
                  size="small" 
                  @click="editNote(note.id)"
                >
                  编辑
                </el-button>
              </el-button-group>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <div v-if="filteredNotes.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无笔记">
          <el-button type="primary" @click="createNewNote">创建新笔记</el-button>
        </el-empty>
      </div>

      <!-- 分页 -->
      <div v-if="filteredNotes.length > 0" class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[12, 24, 36, 48]"
          :total="totalNotes"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * StudentNotesView.vue
 * 学生个人笔记总览页面
 * 
 * 功能：
 * 1. 展示用户自己的所有笔记
 * 2. 支持搜索、筛选和排序
 * 3. 支持创建新笔记
 * 4. 支持查看、编辑、查看批改信息
 * 
 * API接口：
 * - GET /api/note/user/{userId} - 获取用户笔记列表
 */
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Search, View, Star, ChatDotRound, Plus 
} from '@element-plus/icons-vue'
import studentApi from '../../api/student'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()

// 搜索和筛选
const searchKeyword = ref('')
const filterType = ref('')
const sortBy = ref('newest')
const currentPage = ref(1)
const pageSize = ref(12)
const totalNotes = ref(0)

// 数据
const notes = ref([])
const loading = ref(false)

/**
 * 获取用户笔记列表
 */
const fetchUserNotes = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    const response = await studentApi.getUserNotes(userStore.id, params)
    
    if (response && response.code === 200) {
      notes.value = response.data.notes || []
      totalNotes.value = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取笔记列表失败')
      notes.value = []
    }
  } catch (error) {
    console.error('获取用户笔记失败:', error)
    ElMessage.error('获取笔记列表失败，请检查网络连接')
    notes.value = []
  } finally {
    loading.value = false
  }
}

/**
 * 计算属性：过滤和排序笔记
 */
const filteredNotes = computed(() => {
  let filtered = [...notes.value]
  
  // 关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(note => 
      (note.title && note.title.toLowerCase().includes(keyword)) ||
      (note.content && note.content.toLowerCase().includes(keyword)) ||
      (note.summary && note.summary.toLowerCase().includes(keyword))
    )
  }
  
  // 笔记类型筛选
  if (filterType.value === 'assignment') {
    filtered = filtered.filter(note => note.taskId)
  } else if (filterType.value === 'free') {
    filtered = filtered.filter(note => !note.taskId)
  }
  
  // 排序
  if (sortBy.value === 'newest') {
    filtered.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  } else if (sortBy.value === 'oldest') {
    filtered.sort((a, b) => new Date(a.createTime) - new Date(b.createTime))
  } else if (sortBy.value === 'recently_updated') {
    filtered.sort((a, b) => new Date(b.updateTime) - new Date(a.updateTime))
  }
  
  return filtered
})

/**
 * 处理搜索
 */
const handleSearch = () => {
  currentPage.value = 1
  fetchUserNotes()
}

/**
 * 处理筛选
 */
const handleFilter = () => {
  currentPage.value = 1
  fetchUserNotes()
}

/**
 * 处理排序
 */
const handleSort = () => {
  fetchUserNotes()
}

/**
 * 处理分页大小变化
 */
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchUserNotes()
}

/**
 * 处理当前页变化
 */
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchUserNotes()
}

/**
 * 查看笔记详情
 */
const viewNote = (id) => {
  router.push({ name: 'student-note-detail', params: { id } })
}

/**
 * 编辑笔记
 */
const editNote = (id) => {
  // 查找笔记的taskId和courseId
  const note = notes.value.find(n => n.id === id)
  router.push({
    name: 'note-editor',
    params: { id: 'edit' },
    query: {
      noteId: id,
      taskId: note?.taskId,
      courseId: note?.courseId
    }
  })
}

/**
 * 查看批改信息
 */
const viewComments = (id) => {
  // 跳转到个人笔记详情页，会自动显示批改信息
  router.push(`/student/notes/${id}`)
}

/**
 * 创建新笔记
 */
const createNewNote = () => {
  router.push({
    name: 'note-editor',
    params: { id: 'new' }
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
  fetchUserNotes()
})
</script>

<style lang="scss" scoped>
.student-notes-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
  
  h2 {
    margin: 0 0 10px 0;
    color: #303133;
    font-size: 28px;
  }
  
  .page-description {
    color: #909399;
    font-size: 14px;
    margin: 0;
  }
}

.filter-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.action-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.loading-container {
  padding: 40px 0;
}

.note-col {
  margin-bottom: 20px;
}

.note-card {
  height: 100%;
  transition: all 0.3s ease;
  border: 1px solid #ebeef5;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
    border-color: #409eff;
  }
  
  .note-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    
    .note-date {
      font-size: 12px;
      color: #909399;
    }
  }
  
  .note-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 10px;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    min-height: 44px;
    cursor: pointer;
    
    &:hover {
      color: #409eff;
    }
  }
  
  .note-summary {
    font-size: 14px;
    color: #606266;
    margin-bottom: 15px;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
    min-height: 63px;
  }
  
  .note-meta {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12px;
    
    .meta-item {
      display: flex;
      align-items: center;
      font-size: 12px;
      color: #909399;
      
      .el-icon {
        margin-right: 4px;
        font-size: 14px;
      }
    }
  }
  
  .note-course {
    margin-bottom: 12px;
    padding-bottom: 12px;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .note-actions {
    display: flex;
    justify-content: center;
    
    .el-button-group {
      width: 100%;
      display: flex;
      
      .el-button {
        flex: 1;
        padding: 8px 0;
      }
    }
  }
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .note-col {
    width: 100%;
  }
  
  .filter-section .el-col {
    margin-bottom: 10px;
  }
}
</style>
