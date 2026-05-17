<template>
  <div class="public-notes-view">
    <div class="page-header">
      <h2>公开笔记广场</h2>
      <p class="page-description">浏览其他同学分享的优秀笔记，互相学习，共同进步</p>
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
            v-model="filterCourse"
            placeholder="选择课程"
            clearable
            @change="handleFilter"
            style="width: 100%"
          >
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-select
            v-model="sortBy"
            placeholder="排序方式"
            @change="handleSort"
            style="width: 100%"
          >
            <el-option label="最新发布" value="newest" />
            <el-option label="最多点赞" value="most_liked" />
            <el-option label="最多浏览" value="most_viewed" />
          </el-select>
        </el-col>
      </el-row>
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
            @click="viewNote(note.id)"
          >
            <div class="note-header">
              <el-tag v-if="note.taskId" type="success" size="small">作业笔记</el-tag>
              <el-tag v-else type="info" size="small">自由笔记</el-tag>
              <span class="note-date">{{ formatDate(note.createTime) }}</span>
            </div>
            
            <div class="note-title">{{ note.title || '未命名笔记' }}</div>
            <div class="note-summary">
              {{ note.summary || note.content?.substring(0, 100) + '...' || '暂无摘要' }}
            </div>
            
            <div class="note-author">
              <el-avatar :size="24" :src="note.authorAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
              <span class="author-name">{{ note.authorName || '匿名用户' }}</span>
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
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <div v-if="filteredNotes.length === 0" class="empty-state">
        <el-empty description="暂无公开笔记">
          <el-button type="primary" @click="refreshNotes">刷新</el-button>
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
 * PublicNotesView.vue
 * 公开笔记广场 - 浏览其他同学分享的优秀笔记
 * 
 * 功能：
 * 1. 展示所有公开笔记
 * 2. 支持搜索和筛选
 * 3. 支持按课程筛选
 * 4. 支持排序
 * 
 * API接口：
 * - GET /api/note/public - 获取公开笔记列表
 */
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Search, View, Star, ChatDotRound 
} from '@element-plus/icons-vue'
import studentApi from '../api/student'

const router = useRouter()

// 搜索和筛选
const searchKeyword = ref('')
const filterCourse = ref('')
const sortBy = ref('newest')
const currentPage = ref(1)
const pageSize = ref(12)
const totalNotes = ref(0)

// 数据
const notes = ref([])
const courses = ref([])
const loading = ref(false)

/**
 * 获取公开笔记列表
 */
const fetchPublicNotes = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    const response = await studentApi.getPublicNotes(params)
    
    if (response && response.code === 200) {
      notes.value = response.data.notes || []
      totalNotes.value = response.data.total || 0
      
      // 提取唯一的课程列表用于筛选
      const courseSet = new Set()
      notes.value.forEach(note => {
        if (note.courseId && note.courseName) {
          courseSet.add({ id: note.courseId, name: note.courseName })
        }
      })
      courses.value = Array.from(courseSet)
    } else {
      ElMessage.error(response.message || '获取笔记列表失败')
      notes.value = []
    }
  } catch (error) {
    console.error('获取公开笔记失败:', error)
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
  
  // 课程筛选
  if (filterCourse.value) {
    filtered = filtered.filter(note => note.courseId === filterCourse.value)
  }
  
  // 排序
  if (sortBy.value === 'newest') {
    filtered.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  } else if (sortBy.value === 'most_liked') {
    filtered.sort((a, b) => (b.likes || 0) - (a.likes || 0))
  } else if (sortBy.value === 'most_viewed') {
    filtered.sort((a, b) => (b.views || 0) - (a.views || 0))
  }
  
  return filtered
})

/**
 * 处理搜索
 */
const handleSearch = () => {
  currentPage.value = 1
  fetchPublicNotes()
}

/**
 * 处理筛选
 */
const handleFilter = () => {
  currentPage.value = 1
  fetchPublicNotes()
}

/**
 * 处理排序
 */
const handleSort = () => {
  fetchPublicNotes()
}

/**
 * 处理分页大小变化
 */
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchPublicNotes()
}

/**
 * 处理当前页变化
 */
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchPublicNotes()
}

/**
 * 查看笔记详情
 */
const viewNote = (id) => {
  router.push(`/blog/notes/${id}`)
}

/**
 * 刷新笔记列表
 */
const refreshNotes = () => {
  fetchPublicNotes()
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
  fetchPublicNotes()
})
</script>

<style lang="scss" scoped>
.public-notes-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
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
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.loading-container {
  padding: 40px 0;
}

.note-col {
  margin-bottom: 20px;
}

.note-card {
  cursor: pointer;
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
  
  .note-author {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    
    .author-name {
      margin-left: 8px;
      font-size: 13px;
      color: #606266;
    }
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
    margin-top: 10px;
    padding-top: 10px;
    border-top: 1px solid #f0f0f0;
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
