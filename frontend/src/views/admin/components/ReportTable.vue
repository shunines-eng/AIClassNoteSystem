<template>
  <div class="report-table">
    <el-table :data="reports" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="type" label="举报类型" width="120">
        <template #default="scope">
          <el-tag :type="getTypeTag(scope.row.type)">
            {{ getTypeText(scope.row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="targetName" label="举报对象" width="150">
        <template #default="scope">
          <span>{{ scope.row.targetName || `ID: ${scope.row.typeId}` }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="reporterName" label="举报人" width="120">
        <template #default="scope">
          <span>{{ scope.row.reporterName || (scope.row.userId ? `用户ID: ${scope.row.userId}` : '匿名') }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="info" label="举报原因" min-width="200">
        <template #default="scope">
          <div class="report-info">
            {{ scope.row.info }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="举报时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="targetStatus" label="当前状态" width="120">
        <template #default="scope">
          <el-tag :type="getTargetStatusTag(scope.row.type, scope.row.targetStatus)" size="small">
            {{ getTargetStatusText(scope.row.type, scope.row.targetStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" @click="$emit('view-details', scope.row)">
            查看详情
          </el-button>
          <el-button size="small" type="warning" @click="$emit('handle-report', scope.row)">
            处理
          </el-button>
          <el-popconfirm 
            title="确定删除该举报吗？" 
            @confirm="$emit('delete-report', scope.row.id)"
          >
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 空状态 -->
    <div v-if="!loading && reports.length === 0" class="empty-state">
      <el-empty description="暂无举报记录" />
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  reports: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['handle-report', 'delete-report', 'view-details'])

const getTypeText = (type) => {
  switch(type) {
    case 1: return '用户举报'
    case 2: return '笔记举报'
    case 3: return '课程举报'
    default: return '未知类型'
  }
}

const getTypeTag = (type) => {
  switch(type) {
    case 1: return 'warning'
    case 2: return 'danger'
    case 3: return 'info'
    default: return ''
  }
}

/**
 * ReportTable.vue 报表表格组件
 * 注意：所有类型的状态值已统一：
 * - 1=正常, 0=屏蔽
 */
const getTargetStatusText = (type, status) => {
  if (status === null || status === undefined) return '未知'
  
  // 统一：1=正常, 0=屏蔽
  return status === 1 ? '正常' : '已屏蔽'
}

const getTargetStatusTag = (type, status) => {
  if (status === null || status === undefined) return 'info'
  
  return status === 1 ? 'success' : 'danger'
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}
</script>

<style scoped>
.report-table {
  margin-top: 20px;
}
.report-info {
  max-height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}
.empty-state {
  margin-top: 50px;
  text-align: center;
}
</style>
