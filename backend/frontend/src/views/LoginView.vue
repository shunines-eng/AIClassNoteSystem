<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2 class="login-title">系统登录</h2>
      </template>
      <el-form :model="tempLoginForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="tempLoginForm.userAccount" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="tempLoginForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="loginForm.role">
            <el-radio label="student">学生</el-radio>
            <el-radio label="teacher">教师</el-radio>
            <el-radio label="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" class="login-button">登录</el-button>
          <div class="register-link">
            还没有账号？<router-link to="/register">立即注册</router-link>
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
import { useUserStore } from '../store/user'
import api from "../api/index.js";

const router = useRouter()
const userStore = useUserStore()
const loginForm = reactive({
  userAccount: '',
  password: '',
  role: 'student'
})
const tempLoginForm = reactive({
  userAccount: '',
  password: ''
})

const handleLogin = async () => {
  if (!tempLoginForm.userAccount || !tempLoginForm.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }
  try {
    const loginData = {
      userAccount: tempLoginForm.userAccount,
      password: tempLoginForm.password,
      role: loginForm.role
    }
    const response = await api.post('/api/users/login', loginData)
    
    // 2. 检查响应业务状态码（后端约定的 code 为 200 表示成功）
    console.log("登录反馈信息:"+response.code)
    if (response.code === 200) {
      console.log("登录成功，返回数据:", response.data)
      const username = response.data.user.username
      const token = response.data.token
      const user_id = response.data.user.id
      
      console.log(`[LoginView] 解析出的数据: username=${username}, token=${token}, user_id=${user_id}`)
      
      if (!token) {
        console.error("[LoginView] 错误：后端返回的 Token 为空！请检查后端响应结构。")
      }

      /**
       * 3. 存储用户信息到 Pinia Store
       * userStore 是通过 useUserStore() 获取的全局状态管理对象。
       * login 方法会把用户名、角色、token 和 id 存入 store。
       * 由于开启了 pinia-plugin-persistedstate，这些信息会被自动持久化到 localStorage。
       */
      userStore.login(username, loginForm.role, token, user_id)
      console.log("[LoginView] 已调用 userStore.login, 当前 Store 状态:", userStore.$state)

      /**
       * 4. 持久化存储
       * localStorage.setItem 将数据保存在浏览器的本地存储中，即使刷新页面或关闭浏览器也不会丢失。
       * 以后每次请求后端时，我们会在请求拦截器（request.js）中自动读取这里的 token 放入 Header 中。
       */
      localStorage.setItem('token', token)
      localStorage.setItem('id', user_id)

      // 5. 提示用户并跳转页面
      ElMessage.success('登录成功！')
      
      /**
       * 6. 角色路由分发
       * 根据登录时选择的角色，使用 router.push 跳转到不同的起始页面。
       */
      if (loginForm.role === 'student') {
        await router.push('/student/home')
      } else if (loginForm.role === 'teacher') {
        await router.push('/teacher/courses')
      } else if (loginForm.role === 'admin') {
        await router.push('/admin/users')
      } else {
        await router.push('/home')
      }
    } else {
      ElMessage.error(response.data.message || '登录失败')
    }
  } catch (error) {
    console.error('登录错误:', error)
    if (error.response) {
      // 服务器返回了错误状态码
      ElMessage.error(error.response.data?.message || '登录失败')
    } else if (error.request) {
      // 请求发送了但没有收到响应
      ElMessage.error('网络错误，请检查网络连接')
    } else {
      // 其他错误
      ElMessage.error('登录失败，请重试')
    }
  }
}

</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.login-card {
  width: 400px;
}

.login-title {
  text-align: center;
  margin: 0;
  color: #409EFF;
}

.login-button {
  width: 100%;
}

.register-link {
  margin-top: 10px;
  font-size: 14px;
  text-align: center;
  width: 100%;
}
</style>
