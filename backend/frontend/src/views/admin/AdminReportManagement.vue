<template>
  <div class="admin-reports">
    <el-row :gutter="20">
      <el-col :span="18">
        <h2>举报管理</h2>
      </el-col>
      <el-col :span="6" class="text-right">
        <el-input v-model="searchQuery" placeholder="搜索举报内容/举报人" @input="handleSearch">
          <template #append>
            <el-button :icon="Search" />
          </template>
        </el-input>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>举报类型筛选</span>
              <el-button type="primary" @click="refreshReports">刷新</el-button>
            </div>
          </template>
          
          <el-tabs v-model="activeTab" @tab-click="handleTabChange">
            <el-tab-pane label="全部举报" name="all">
              <report-table 
                :reports="filteredReports" 
                :loading="loading"
                @handle-report="viewReportDetails"
                @delete-report="deleteReport"
                @view-details="viewReportDetails"
              />
            </el-tab-pane>
            <el-tab-pane label="用户举报" name="user">
              <report-table 
                :reports="userReports" 
                :loading="loading"
                @handle-report="viewReportDetails"
                @delete-report="deleteReport"
                @view-details="viewReportDetails"
              />
            </el-tab-pane>
            <el-tab-pane label="笔记举报" name="note">
              <report-table 
                :reports="noteReports" 
                :loading="loading"
                @handle-report="viewReportDetails"
                @delete-report="deleteReport"
                @view-details="viewReportDetails"
              />
            </el-tab-pane>
            <el-tab-pane label="课程举报" name="class">
              <report-table 
                :reports="classReports" 
                :loading="loading"
                @handle-report="viewReportDetails"
                @delete-report="deleteReport"
                @view-details="viewReportDetails"
              />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <!-- ========== 举报详情对话框 ========== -->
    <el-dialog v-model="showDetailDialog" title="举报详情" width="55%">
      <div v-if="currentReport" class="report-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="举报ID">{{ currentReport.id }}</el-descriptions-item>
          <el-descriptions-item label="举报类型">
            <el-tag :type="getReportTypeTag(currentReport.type)">
              {{ getReportTypeText(currentReport.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="举报对象" :span="2">
            <span v-if="currentReport.targetName">
              {{ currentReport.targetName }} (ID: {{ currentReport.typeId }})
            </span>
            <span v-else>ID: {{ currentReport.typeId }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="举报人">
            {{ currentReport.reporterName || (currentReport.userId ? `用户ID: ${currentReport.userId}` : '匿名') }}
          </el-descriptions-item>
          <el-descriptions-item label="举报时间">
            {{ formatDateTime(currentReport.createTime) }}
          </el-descriptions-item>
        </el-descriptions>
        
        <el-divider />
        
        <div class="report-info">
          <h4>举报原因：</h4>
          <el-card class="info-card">
            <p>{{ currentReport.info }}</p>
          </el-card>
        </div>
        
        <div class="action-buttons" v-if="currentReport.type === 1">
          <el-button type="primary" @click="loadUserDetail(currentReport.typeId)">查看用户详情</el-button>
          <el-button 
            :type="currentReport.targetStatus === 1 ? 'danger' : 'warning'" 
            @click="toggleUserStatus(currentReport.typeId, currentReport.targetStatus)">
            {{ currentReport.targetStatus === 1 ? '屏蔽用户' : '解除屏蔽' }}
          </el-button>
        </div>
        
        <div class="action-buttons" v-else-if="currentReport.type === 2">
          <el-button type="primary" @click="loadNoteDetail(currentReport.typeId)">查看笔记详情</el-button>
          <el-button 
            :type="currentReport.targetStatus === 1 ? 'danger' : 'warning'" 
            @click="toggleNoteStatus(currentReport.typeId, currentReport.targetStatus)">
            {{ currentReport.targetStatus === 1 ? '屏蔽笔记' : '解除屏蔽' }}
          </el-button>
        </div>
        
        <div class="action-buttons" v-else-if="currentReport.type === 3">
          <el-button type="primary" @click="loadClassDetail(currentReport.typeId)">查看课程详情</el-button>
          <el-button 
            :type="currentReport.targetStatus === 1 ? 'danger' : 'warning'" 
            @click="toggleClassStatus(currentReport.typeId, currentReport.targetStatus)">
            {{ currentReport.targetStatus === 1 ? '屏蔽课程' : '解除屏蔽' }}
          </el-button>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDetailDialog = false">关闭</el-button>
          <el-button type="primary" @click="markAsHandled">标记为已处理</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- ========== 用户详情子对话框 ========== -->
    <el-dialog v-model="showUserDialog" title="用户详情" width="45%">
      <div v-if="userDetail" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{ userDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ userDetail.username }}</el-descriptions-item>
          <el-descriptions-item label="真实姓名">{{ userDetail.realName || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="userDetail.status === 1 ? 'success' : 'danger'" size="small">
              {{ userDetail.status === 1 ? '正常' : '已屏蔽' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="手机号">{{ userDetail.phone || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ userDetail.gender === 1 ? '男' : userDetail.gender === 0 ? '女' : '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ userDetail.age || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="个性签名" :span="2">{{ userDetail.signature || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="showUserDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- ========== 笔记详情子对话框 ========== -->
    <el-dialog v-model="showNoteDialog" title="笔记详情" width="55%">
      <div v-if="noteDetail" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="笔记ID">{{ noteDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="标题">{{ noteDetail.title }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="noteDetail.status === 1 ? 'success' : 'danger'" size="small">
              {{ noteDetail.status === 1 ? '正常' : '已屏蔽' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(noteDetail.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="作者ID">{{ noteDetail.userId }}</el-descriptions-item>
          <el-descriptions-item label="作业ID">{{ noteDetail.taskId || '无' }}</el-descriptions-item>
        </el-descriptions>
        <el-divider />
        <h4>笔记内容预览：</h4>
        <el-card class="content-preview">
          <div v-html="truncateContent(noteDetail.content)"></div>
        </el-card>
      </div>
      <template #footer>
        <el-button @click="showNoteDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- ========== 课程详情子对话框 ========== -->
    <el-dialog v-model="showClassDialog" title="课程详情" width="45%">
      <div v-if="classDetail" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程ID">{{ classDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="课程名称">{{ classDetail.className }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="classDetail.status === 1 ? 'success' : 'danger'" size="small">
              {{ classDetail.status === 1 ? '正常' : '已屏蔽' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(classDetail.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="教师ID">{{ classDetail.teacherId }}</el-descriptions-item>
          <el-descriptions-item label="课程描述" :span="2">{{ classDetail.describe || '暂无描述' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="showClassDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import api from '../../api'
import ReportTable from './components/ReportTable.vue'

const searchQuery = ref('')
const activeTab = ref('all')
const loading = ref(false)
const showDetailDialog = ref(false)
const currentReport = ref(null)
const allReports = ref([])

// 子对话框状态
const showUserDialog = ref(false)
const showNoteDialog = ref(false)
const showClassDialog = ref(false)
const userDetail = ref(null)
const noteDetail = ref(null)
const classDetail = ref(null)

/**
 * 获取用户真实姓名/用户名
 */
async function getUserDisplayName(userId) {
  if (!userId) return null
  try {
    const res = await api.get(`/api/users/${userId}`)
    if (res.code === 200 && res.data) {
      return res.data.realName || res.data.username || `用户ID:${userId}`
    }
  } catch (error) {
    console.error(`获取用户信息失败(userId=${userId}):`, error)
  }
  return `用户ID:${userId}`
}

// 获取目标名称、状态和举报人名称
async function enrichReportWithTargetInfo(report) {
  // 获取举报人名称
  report.reporterName = await getUserDisplayName(report.userId)

  try {
    if (report.type === 1) {
      const res = await api.get(`/api/users/${report.typeId}`)
      if (res.code === 200 && res.data) {
        report.targetStatus = res.data.status
        report.targetName = res.data.realName || res.data.username || `ID:${report.typeId}`
      }
    } else if (report.type === 2) {
      const res = await api.get(`/api/note/${report.typeId}`)
      if (res.code === 200 && res.data) {
        const note = res.data.note || res.data
        report.targetStatus = note.status
        report.targetName = note.title || `笔记ID:${report.typeId}`
      }
    } else if (report.type === 3) {
      const res = await api.get(`/api/class/${report.typeId}`)
      if (res) {
        report.targetStatus = res.status
        report.targetName = res.className || `课程ID:${report.typeId}`
      }
    }
  } catch (error) {
    console.error(`获取举报目标信息失败(type=${report.type}, typeId=${report.typeId}):`, error)
    report.targetStatus = null
    report.targetName = `ID:${report.typeId}`
  }
  return report
}

// 获取所有举报
const fetchReports = async () => {
  loading.value = true
  try {
    const res = await api.get('/api/reports')
    if (res.code === 200) {
      const rawReports = res.data || []
      await Promise.all(rawReports.map(enrichReportWithTargetInfo))
      allReports.value = rawReports
    }
  } catch (error) {
    console.error('获取举报列表失败:', error)
    ElMessage.error('获取举报列表失败')
    allReports.value = []
  } finally {
    loading.value = false
  }
}

// 计算属性
const filteredReports = computed(() => {
  if (!searchQuery.value) return allReports.value
  const q = searchQuery.value.toLowerCase()
  return allReports.value.filter(report => 
    (report.info && report.info.toLowerCase().includes(q)) ||
    (report.targetName && report.targetName.toLowerCase().includes(q)) ||
    (report.reporterName && report.reporterName.toLowerCase().includes(q))
  )
})

const userReports = computed(() => 
  filteredReports.value.filter(report => report.type === 1)
)

const noteReports = computed(() => 
  filteredReports.value.filter(report => report.type === 2)
)

const classReports = computed(() => 
  filteredReports.value.filter(report => report.type === 3)
)

// 方法
const handleSearch = () => {}
const handleTabChange = () => {}
const refreshReports = () => fetchReports()

const viewReportDetails = (report) => {
  currentReport.value = report
  showDetailDialog.value = true
}

const getReportTypeText = (type) => {
  switch(type) {
    case 1: return '用户举报'
    case 2: return '笔记举报'
    case 3: return '课程举报'
    default: return '未知类型'
  }
}

const getReportTypeTag = (type) => {
  switch(type) {
    case 1: return 'warning'
    case 2: return 'danger'
    case 3: return 'info'
    default: return ''
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

const truncateContent = (content) => {
  if (!content) return '无内容'
  if (content.length > 500) return content.substring(0, 500) + '...'
  return content
}

const deleteReport = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该举报记录吗？', '提示', { type: 'warning' })
    const res = await api.delete(`/api/reports/${id}`)
    if (res.code === 200) {
      ElMessage.success('举报记录已删除')
      fetchReports()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除举报失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const markAsHandled = async () => {
  try {
    await ElMessageBox.confirm('确定标记为已处理吗？', '提示', { type: 'warning' })
    await deleteReport(currentReport.value.id)
    showDetailDialog.value = false
  } catch (error) {
    if (error !== 'cancel') {
      console.error('标记失败:', error)
    }
  }
}

// ========== 加载详情子对话框数据 ==========
const loadUserDetail = async (userId) => {
  try {
    const res = await api.get(`/api/users/${userId}`)
    if (res.code === 200 && res.data) {
      userDetail.value = res.data
      showUserDialog.value = true
    }
  } catch (error) {
    console.error('获取用户详情失败:', error)
    ElMessage.error('获取用户详情失败')
  }
}

const loadNoteDetail = async (noteId) => {
  try {
    const res = await api.get(`/api/note/${noteId}`)
    if (res.code === 200 && res.data) {
      noteDetail.value = res.data.note || res.data
      showNoteDialog.value = true
    }
  } catch (error) {
    console.error('获取笔记详情失败:', error)
    ElMessage.error('获取笔记详情失败')
  }
}

const loadClassDetail = async (classId) => {
  try {
    const res = await api.get(`/api/class/${classId}`)
    if (res) {
      classDetail.value = res
      showClassDialog.value = true
    }
  } catch (error) {
    console.error('获取课程详情失败:', error)
    ElMessage.error('获取课程详情失败')
  }
}

// ========== 切换状态 ==========
const toggleUserStatus = async (userId, currentTargetStatus) => {
  try {
    const isNormal = currentTargetStatus === 1 || currentTargetStatus === null
    const action = isNormal ? '屏蔽' : '解除屏蔽'
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', { type: 'warning' })
    const newStatus = isNormal ? 0 : 1
    const res = await api.put(`/api/users/${userId}/status?status=${newStatus}`)
    if (res.code === 200) {
      ElMessage.success(`用户已${action}`)
      if (currentReport.value) currentReport.value.targetStatus = newStatus
      fetchReports()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新用户状态失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const toggleNoteStatus = async (noteId, currentTargetStatus) => {
  try {
    const isNormal = currentTargetStatus === 1 || currentTargetStatus === null
    const action = isNormal ? '屏蔽' : '解除屏蔽'
    await ElMessageBox.confirm(`确定要${action}该笔记吗？`, '提示', { type: 'warning' })
    const newStatus = isNormal ? 0 : 1
    const res = await api.put(`/api/note/${noteId}/status?status=${newStatus}`)
    if (res.code === 200) {
      ElMessage.success(`笔记已${action}`)
      if (currentReport.value) currentReport.value.targetStatus = newStatus
      fetchReports()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新笔记状态失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const toggleClassStatus = async (classId, currentTargetStatus) => {
  try {
    const isNormal = currentTargetStatus === 1 || currentTargetStatus === null
    const action = isNormal ? '屏蔽' : '解除屏蔽'
    await ElMessageBox.confirm(`确定要${action}该课程吗？`, '提示', { type: 'warning' })
    const newStatus = isNormal ? 0 : 1
    const res = await api.put(`/api/class/${classId}/status`, null, { params: { status: newStatus } })
    if (res.code === 200) {
      ElMessage.success(`课程已${action}`)
      if (currentReport.value) currentReport.value.targetStatus = newStatus
      fetchReports()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新课程状态失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  fetchReports()
})
</script>

<style scoped>
.admin-reports {
  padding: 20px;
}
.mt-20 {
  margin-top: 20px;
}
.text-right {
  text-align: right;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.report-detail {
  padding: 10px;
}
.report-info {
  margin-top: 20px;
}
.info-card {
  margin-top: 10px;
  padding: 15px;
  background-color: #f9f9f9;
}
.action-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}
.detail-content {
  padding: 10px;
}
.content-preview {
  margin-top: 10px;
  padding: 10px;
  background-color: #fafafa;
  max-height: 300px;
  overflow-y: auto;
}
</style>
