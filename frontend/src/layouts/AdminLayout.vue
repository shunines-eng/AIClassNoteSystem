<template>
  <el-container class="admin-layout">
    <el-header class="header">
      <span>智能化课堂笔记管理系统</span>
      <div class="user-info">
        <el-dropdown>
          <span class="el-dropdown-link">
            {{ userStore.username }} ({{ userStore.role }})
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人中心</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" class="sidebar">
        <!-- 学生端菜单 -->
        <el-menu
          v-if="userStore.role === '学生' || userStore.role === 'student'"
          :default-active="route.path"
          class="el-menu-vertical-demo"
          router
        >
          <el-menu-item index="/student/home">
            <el-icon><home-filled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/student/courses">
            <el-icon><notebook /></el-icon>
            <span>课程管理</span>
          </el-menu-item>
          <el-menu-item index="/student/profile">
            <el-icon><user /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </el-menu>

        <!-- 教师端菜单 -->
        <el-menu
          v-else-if="userStore.role === '教师' || userStore.role === 'teacher'"
          :default-active="route.path"
          class="el-menu-vertical-demo"
          router
        >
          <el-menu-item index="/teacher/courses">
            <el-icon><collection /></el-icon>
            <span>课程管理</span>
          </el-menu-item>
          <el-menu-item index="/teacher/assignments">
            <el-icon><list /></el-icon>
            <span>作业发布</span>
          </el-menu-item>
          <el-menu-item index="/teacher/profile">
            <el-icon><user /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </el-menu>

        <!-- 管理员菜单 -->
        <el-menu
          v-else-if="userStore.role === '管理员' || userStore.role === 'admin'"
          :default-active="route.path"
          class="el-menu-vertical-demo"
          router
        >
          <el-menu-item index="/admin/users">
            <el-icon><user-filled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/classes">
            <el-icon><management /></el-icon>
            <span>班级监管</span>
          </el-menu-item>
          <el-menu-item index="/admin/notes">
            <el-icon><document /></el-icon>
            <span>笔记管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/reports">
            <el-icon><warning /></el-icon>
            <span>举报管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { 
  Document, 
  Menu as IconMenu, 
  Setting, 
  List, 
  ArrowDown, 
  HomeFilled, 
  Notebook, 
  User, 
  Collection, 
  DataAnalysis, 
  UserFilled, 
  Management,
  Warning 
} from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已安全退出登录')
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #409EFF;
  color: white;
  font-size: 20px;
  font-weight: bold;
}

.user-info {
  font-size: 16px;
}

.el-dropdown-link {
  cursor: pointer;
  color: white;
  display: flex;
  align-items: center;
}
</style>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
}

.header {
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
}

.sidebar {
  background-color: #f4f4f5;
}

.content {
  padding: 20px;
}
</style>
