<template>
  <div class="admin-classes">
    <el-row :gutter="20">
      <el-col :span="24">
        <h2>班级/课程内容监管</h2>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card header="全平台课程/班级列表">
          <el-table :data="classes" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="className" label="班级/课程名称" />
            <el-table-column prop="teacherName" label="任课教师" width="120" />
            <el-table-column prop="studentCount" label="人数" width="100" />
            <el-table-column prop="status" label="内容状态" width="120">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '正常' : '已屏蔽' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="250">
              <template #default="scope">
                <el-button size="small" type="primary" @click="viewClassContent(scope.row)">查看内容</el-button>
                <el-button 
                  size="small" 
                  :type="scope.row.status === 1 ? 'danger' : 'success'" 
                  @click="toggleClassStatus(scope.row)">
                  {{ scope.row.status === 1 ? '屏蔽内容' : '解除屏蔽' }}
                </el-button>
                <el-button 
                  size="small" 
                  type="warning" 
                  @click="viewClassReports(scope.row)">
                  举报查阅
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

const classes = ref([])

const fetchClasses = async () => {
  try {
    const res = await api.get('/api/class/admin/list')
    if (res.code === 200) {
      classes.value = res.data
    }
  } catch (error) {
    console.error('获取班级列表失败', error)
  }
}

const toggleClassStatus = async (item) => {
  const isNormal = item.status === 1
  const action = isNormal ? '屏蔽' : '解除屏蔽'
  const newStatus = isNormal ? 0 : 1
  
  try {
    await ElMessageBox.confirm(`确定要${action}该班级的内容吗？这会影响该班级公开笔记的展示。`, '提示', {
      type: 'warning'
    })
    
    const res = await api.put(`/api/class/${item.id}/status`, null, {
      params: { status: newStatus }
    })
    
    if (res.code === 200) {
      item.status = newStatus
      ElMessage.success(`该班级内容已${action}`)
    }
  } catch (error) {
    // 用户取消
  }
}

const viewClassContent = async (item) => {
  try {
    const res = await api.get(`/api/class/${item.id}`)
    if (res) {
      // 创建一个弹窗显示班级详情
      ElMessageBox.alert(`
        <div style="max-height: 400px; overflow-y: auto;">
          <h3>班级详情</h3>
          <p><strong>班级ID:</strong> ${res.id}</p>
          <p><strong>班级名称:</strong> ${res.className}</p>
          <p><strong>教师ID:</strong> ${res.teacherId}</p>
          <p><strong>课程描述:</strong> ${res.describe || '无'}</p>
          <p><strong>创建时间:</strong> ${new Date(res.createTime).toLocaleString()}</p>
          <p><strong>状态:</strong> ${res.status === 1 ? '正常' : '已屏蔽'}</p>
        </div>
      `, '班级详情', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '关闭',
        customClass: 'class-detail-dialog'
      })
    }
  } catch (error) {
    console.error('获取班级详情失败:', error)
    ElMessage.error('获取班级详情失败')
  }
}

const viewClassReports = async (item) => {
  try {
    // 获取班级的举报记录（类型为3表示课程举报）
    const res = await api.get(`/api/reports/type/3/target/${item.id}`)
    if (res.code === 200) {
      const reports = res.data
      
      if (reports.length === 0) {
        ElMessage.info(`班级 "${item.className}" 暂无举报记录`)
        return
      }
      
      // 创建举报记录表格的HTML
      let reportsTable = `
        <div style="max-height: 400px; overflow-y: auto;">
          <h3>班级 "${item.className}" 的举报记录</h3>
          <p><strong>班级ID:</strong> ${item.id}</p>
          <p><strong>班级名称:</strong> ${item.className}</p>
          <p><strong>举报记录总数:</strong> ${reports.length} 条</p>
          <table border="1" cellpadding="8" cellspacing="0" style="width: 100%; margin-top: 20px; border-collapse: collapse;">
            <thead style="background-color: #f5f7fa;">
              <tr>
                <th style="padding: 8px; text-align: left;">举报ID</th>
                <th style="padding: 8px; text-align: left;">举报人ID</th>
                <th style="padding: 8px; text-align: left;">举报原因</th>
                <th style="padding: 8px; text-align: left;">举报时间</th>
                <th style="padding: 8px; text-align: left;">状态</th>
              </tr>
            </thead>
            <tbody>
      `
      
      reports.forEach(report => {
        const statusText = report.status === 1 ? '已处理' : '待处理'
        const statusColor = report.status === 1 ? '#67c23a' : '#e6a23c'
        const createTime = formatDateTime(report.createTime)
        
        reportsTable += `
          <tr>
            <td style="padding: 8px;">${report.id}</td>
            <td style="padding: 8px;">${report.userId || '匿名'}</td>
            <td style="padding: 8px;">${report.info || '无'}</td>
            <td style="padding: 8px;">${createTime}</td>
            <td style="padding: 8px;">
              <span style="color: ${statusColor}; font-weight: bold;">${statusText}</span>
            </td>
          </tr>
        `
      })
      
      reportsTable += `
            </tbody>
          </table>
        </div>
      `
      
      // 使用ElMessageBox显示举报记录
      ElMessageBox.alert(reportsTable, '班级举报记录', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '关闭',
        customClass: 'report-detail-dialog',
        callback: () => {
          // 可以在这里添加回调逻辑
        }
      })
    }
  } catch (error) {
    console.error('获取班级举报记录失败:', error)
    ElMessage.error('获取举报记录失败')
  }
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchClasses()
})
</script>

<style scoped>
.admin-classes {
  padding: 20px;
}
.mt-20 {
  margin-top: 20px;
}
</style>
