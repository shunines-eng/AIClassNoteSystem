<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <h2 class="register-title">用户注册</h2>
      </template>
      <el-form :model="registerForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="registerForm.username" placeholder="请输入显示的用户名" />
        </el-form-item>
        <el-form-item label="账号">
          <el-input v-model="registerForm.userAccount" placeholder="请输入登录账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="registerForm.role">
            <el-radio label="student">学生</el-radio>
            <el-radio label="teacher">教师</el-radio>
            <el-radio label="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" class="register-button">注册</el-button>
          <div class="login-link">
            已经有账号了？<router-link to="/login">立即登录</router-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import api from "../api/index.js";

const router = useRouter()
const registerForm = reactive({
  username: '',
  userAccount: '',
  password: '',
  confirmPassword: '',
  role: 'student'
})

const handleRegister = async () => {
  if (!registerForm.username || !registerForm.userAccount || !registerForm.password || !registerForm.role) {
    ElMessage.error('请填写完整的注册信息')
    return
  }
  
  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  try {
    const roleMap = {
      'student': 1,
      'teacher': 2,
      'admin': 3
    }
    
    const requestData = {
      username: registerForm.username,
      userAccount: registerForm.userAccount,
      password: registerForm.password,
      role: roleMap[registerForm.role]
    }

    const response = await api.post('/api/users/register', requestData)
    
    if (response.code === 200) {
      ElMessage.success('注册成功！请登录')
      router.push('/login')
    } else {
      ElMessage.error(response.message || '注册失败')
    }
  } catch (error) {
    console.error('注册错误:', error)
    ElMessage.error(error.response?.data?.message || '注册失败，请重试')
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.register-card {
  width: 400px;
}

.register-title {
  text-align: center;
  margin: 0;
  color: #409EFF;
}

.register-button {
  width: 100%;
}

.login-link {
  margin-top: 10px;
  font-size: 14px;
  text-align: center;
  width: 100%;
}
</style>
