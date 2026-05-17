<template>
  <div class="teacher-dashboard">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>待批改作业</template>
          <div class="stat-number">12</div>
          <p>来自 3 个不同课程</p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>活跃学生数</template>
          <div class="stat-number">156</div>
          <p>本周新增 8 名学生</p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>已发布课程</template>
          <div class="stat-number">5</div>
          <p>共包含 42 个笔记主题</p>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="mt-20">
      <template #header>
        <div class="card-header">
          <span>最近提交的作业</span>
          <el-button type="primary" size="small">查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentSubmissions">
        <el-table-column prop="studentName" label="学生姓名" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="submitTime" label="提交时间" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === '待批改' ? 'warning' : 'success'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="text" size="small" @click="goToGrade(scope.row)">去批改</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const recentSubmissions = ref([
  { id: 1, studentName: '张三', courseName: '软件工程', submitTime: '2024-03-19 10:00', status: '待批改' },
  { id: 2, studentName: '李四', courseName: '数据库系统', submitTime: '2024-03-19 09:30', status: '待批改' },
  { id: 3, studentName: '王五', courseName: '软件工程', submitTime: '2024-03-18 16:20', status: '已批改' },
])

const goToGrade = (submission) => {
  // TODO: 跳转到具体的作业批改页面
  console.log('跳转到作业批改页面:', submission)
}
</script>

<style scoped>
.teacher-dashboard {
  padding: 10px;
}

.stat-number {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
  text-align: center;
  margin: 10px 0;
}

.mt-20 {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
