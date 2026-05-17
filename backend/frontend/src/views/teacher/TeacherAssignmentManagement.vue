<template>
  <div class="teacher-assignments">
    <el-row :gutter="20">
      <el-col :span="24">
        <h2>作业发布与管理</h2>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card header="作业列表">
          <el-table :data="assignments" style="width: 100%">
            <el-table-column prop="title" label="作业标题" />
            <el-table-column prop="className" label="所属课程" width="150" />
            <el-table-column prop="submitCount" label="提交人数" width="100">
              <template #default="scope">
                {{ scope.row.noteCount }} / {{ scope.row.studentCount }}
              </template>
            </el-table-column>
            <el-table-column prop="deadline" label="截止日期" width="180" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button size="small" type="primary" @click="viewSubmissions(scope.row)">查看批改</el-button>
                <el-button size="small" type="warning" @click="editAssignment(scope.row)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑作业对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑作业" width="40%">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="作业标题">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="作业描述">
          <el-input v-model="editForm.content" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="editForm.deadline" type="datetime" style="width: 100%" />
        </el-form-item>
        <el-form-item label="附件要求">
          <el-checkbox v-model="editForm.attachmentRequired">必须上传附件</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="handleUpdateAssignment">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import api from '../../api'

const router = useRouter()
const userStore = useUserStore()

const assignments = ref([])
const showEditDialog = ref(false)
const editForm = reactive({
  id: null,
  title: '',
  content: '',
  deadline: '',
  attachmentRequired: false
})

const fetchAssignments = async () => {
  try {
    const res = await api.get('api/task/byTeacherIdWithClass/'+userStore.id);
    if (res.code === 200) {
      assignments.value = res.data;
    }
  } catch (error) {
    console.error('获取作业列表失败', error);
  }
}

const viewSubmissions = (assignment) => {
  router.push({
    name: 'teacher-note-grading',
    params: { id: assignment.taskId }
  })
}

const editAssignment = (assignment) => {
  Object.assign(editForm, {
    id: assignment.id,
    title: assignment.title,
    content: assignment.content,
    deadline: assignment.deadline,
    attachmentRequired: assignment.attachmentRequired
  })
  showEditDialog.value = true
}

const handleUpdateAssignment = async () => {
  try {
    const res = await api.put(`/api/task/${editForm.id}`, editForm)
    if (res.code === 200 || res) {
      ElMessage.success('作业更新成功')
      showEditDialog.value = false
      fetchAssignments()
    }
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

onMounted(() => {
  fetchAssignments()
})
</script>

<style scoped>
.teacher-assignments {
  padding: 20px;
}
.mt-20 {
  margin-top: 20px;
}
</style>
