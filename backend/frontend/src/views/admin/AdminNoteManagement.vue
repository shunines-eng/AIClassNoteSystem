<template>
  <div class="admin-notes">
    <el-row :gutter="20">
      <el-col :span="18">
        <h2>全平台笔记管理</h2>
      </el-col>
      <el-col :span="6" class="text-right">
        <el-input v-model="searchQuery" placeholder="搜索笔记标题/作者" @input="handleSearch">
          <template #append>
            <el-button :icon="Search" />
          </template>
        </el-input>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-table :data="notes" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="笔记标题" />
          <el-table-column prop="author" label="作者" width="120" />
          <el-table-column prop="courseName" label="所属课程" width="150" />
          <el-table-column prop="isPublic" label="公开状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.isPublic ? 'success' : 'info'">
                {{ scope.row.isPublic ? '公开' : '私有' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reportCount" label="被举报数" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.reportCount > 0 ? 'danger' : 'info'">
                {{ scope.row.reportCount }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250">
            <template #default="scope">
              <el-button size="small" type="primary" @click="viewNote(scope.row)">查看内容</el-button>
              <el-button 
                size="small" 
                type="warning" 
                @click="viewNoteReports(scope.row)">
                举报查阅
              </el-button>
              <el-popconfirm title="确定删除该笔记吗？此操作不可撤销。" @confirm="deleteNote(scope.row.id)">
                <template #reference>
                  <el-button size="small" type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="handlePageChange"
          />
        </div>
      </el-col>
    </el-row>

    <!-- 笔记预览对话框 -->
    <el-dialog v-model="showPreviewDialog" title="笔记内容预览" width="50%">
      <div class="note-preview-content">
        <h3>{{ currentNote.title }}</h3>
        <p><strong>作者:</strong> {{ currentNote.author }} | <strong>所属课程:</strong> {{ currentNote.courseName }}</p>
        <el-divider />
        <div class="markdown-content" v-html="currentNote.content"></div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPreviewDialog = false">关闭预览</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * AdminNoteManagement.vue
 * 管理员端笔记管理界面，支持查看、搜索、删除和监控被举报笔记。
 * 
 * 学习要点：
 * 1. el-popconfirm: 删除确认。
 * 2. el-dialog: 预览笔记内容。
 * 3. Search: 搜索图标。
 */
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import api from '../../api'

const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)
const showPreviewDialog = ref(false)
const currentNote = ref({})

const notes = ref([])

const fetchNotes = async () => {
  try {
    const res = await api.get('/api/note/admin/list', { 
      params: { 
        page: currentPage.value, 
        size: pageSize.value, 
        query: searchQuery.value 
      } 
    })
    if (res.code === 200) {
      notes.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取笔记列表失败', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchNotes()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchNotes()
}

const viewNote = (note) => {
  currentNote.value = note
  showPreviewDialog.value = true
}

const deleteNote = async (id) => {
  try {
    const res = await api.delete(`/api/note/${id}`)
    if (res.code === 200) {
      ElMessage.success('笔记已从平台删除')
      fetchNotes()
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const viewNoteReports = (note) => {
  // 跳转到举报管理页面，并筛选该笔记的举报
  ElMessage.info(`查看笔记 "${note.title}" 的举报记录，将跳转到举报管理页面`);
  // 实际应该跳转到举报管理页面并传递笔记ID作为筛选条件
  // 这里简单提示
  // 在实际应用中，可以使用路由跳转：
  // router.push({ path: '/admin/reports', query: { type: 2, typeId: note.id } });
}

onMounted(() => {
  fetchNotes()
})
</script>

<style scoped>
.admin-notes {
  padding: 20px;
}
.mt-20 {
  margin-top: 20px;
}
.text-right {
  text-align: right;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.note-preview-content {
  padding: 10px;
}
.markdown-content {
  line-height: 1.6;
}
</style>
