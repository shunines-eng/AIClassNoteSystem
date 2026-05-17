<template>
  <div class="blog-layout">
    <el-header class="header">
      <div class="logo">NoteAI 笔记分享</div>
      <div class="user-actions">
        <router-link to="/login">返回主页</router-link>
        <el-button type="primary" @click="goToDashboard">返回仪表盘</el-button>
      </div>
    </el-header>
    <el-main class="main-content">
      <router-view />
    </el-main>
    <el-footer class="footer">
      <p>© 2026 智能化课堂笔记管理系统. All Rights Reserved.</p>
    </el-footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToDashboard = () => {
  router.push('/admin/courses')
}
</script>

<style lang="scss" scoped>
.blog-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 50px;
  border-bottom: 1px solid #e4e7ed;
  background-color: #fff;

  .logo {
    font-size: 24px;
    font-weight: bold;
    color: #409EFF;
  }

  .nav-links a {
    margin: 0 15px;
    text-decoration: none;
    color: #303133;
    font-size: 16px;
    &:hover {
      color: #409EFF;
    }
  }

  .user-actions a {
    margin-right: 15px;
    text-decoration: none;
    color: #606266;
  }
}

.main-content {
  flex-grow: 1;
  padding: 20px 80px;
  background-color: #f5f7fa;
}

.footer {
  text-align: center;
  padding: 20px;
  color: #909399;
  background-color: #fff;
  border-top: 1px solid #e4e7ed;
}
</style>
