<template>
  <div class="student-profile">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息设置</span>
          <el-button type="primary" @click="handleSave">保存修改</el-button>
        </div>
      </template>

      <el-form :model="profileForm" label-width="100px" class="profile-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="电话">
              <el-input v-model="profileForm.phone" placeholder="请输入电话号码" />
            </el-form-item>
            <el-form-item label="年龄">
              <el-input-number v-model="profileForm.age" :min="0" :max="150" />
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="profileForm.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="0">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="头像">
              <el-upload
                class="avatar-uploader"
                action=""
                :http-request="uploadAvatar"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload">
                <img v-if="profileForm.avatar" :src="getFullUrl(profileForm.avatar)" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item label="个性签名">
              <el-input
                v-model="profileForm.signature"
                type="textarea"
                :rows="4"
                placeholder="向大家介绍一下你自己吧..."
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card class="account-security mt-20" header="账号安全">
      <el-form :model="securityForm" label-width="100px">
        <el-form-item label="原密码">
          <el-input v-model="securityForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="securityForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="warning" @click="handleChangePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
/**
 * StudentProfile.vue
 * 个人信息管理页面。
 * 
 * 学习要点：
 * 1. el-upload: 处理文件上传（头像）。
 * 2. el-form: 表单数据校验。
 * 3. before-upload: 上传前的逻辑校验（格式、大小等）。
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '../../store/user'
import api from '../../api'
import userApi from '../../api/user'
import commonApi from '../../api/common'

const userStore = useUserStore()

const profileForm = reactive({
  username: '',
  realName: '',
  phone: '',
  age: 0,
  gender: 1,
  avatar: '',
  signature: ''
})

const securityForm = reactive({
  oldPassword: '',
  newPassword: ''
})

const getFullUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  // 如果已经是 /api 开头的相对路径，直接使用（由Vite代理处理）
  if (url.startsWith('/')) return url
  return '/' + url
}

const fetchProfile = async () => {
  if (!userStore.id) return
  try {
    const res = await userApi.getUserInfo(userStore.id)
    if (res.code === 200) {
      Object.assign(profileForm, res.data)
    }
  } catch (error) {
    console.error('获取个人信息失败', error)
    ElMessage.error('获取个人信息失败')
  }
}

const handleSave = async () => {
  try {
    const res = await userApi.updateUserInfo(userStore.id, profileForm)
    if (res.code === 200) {
      ElMessage.success('个人资料已更新！')
      userStore.username = profileForm.username // Update store if needed
    }
  } catch (error) {
    console.error('资料更新失败', error)
    ElMessage.error('资料更新失败')
  }
}

const handleChangePassword = async () => {
  if (!securityForm.oldPassword || !securityForm.newPassword) {
    ElMessage.error('请填写完整的密码信息')
    return
  }
  try {
    const res = await userApi.updatePassword(userStore.id, securityForm.oldPassword, securityForm.newPassword)
    if (res.code === 200) {
      ElMessage.success('密码修改成功')
      securityForm.oldPassword = ''
      securityForm.newPassword = ''
    } else {
      ElMessage.error(res.message || '修改密码失败')
    }
  } catch (error) {
    console.error('修改密码失败', error)
    ElMessage.error(error.response?.data?.message || '修改密码失败')
  }
}

const uploadAvatar = async (options) => {
  const { file } = options
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    const res = await commonApi.uploadFile(formData)
    if (res.code === 200) {
      const avatarUrl = res.data.fileUrl
      await userApi.updateAvatar(userStore.id, avatarUrl)
      profileForm.avatar = avatarUrl
      ElMessage.success('头像上传成功')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG 或 PNG 格式!');
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!');
  }
  return isJPG && isLt2M;
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped>
.student-profile {
  padding: 20px;
}
.profile-card {
  max-width: 900px;
  margin: 0 auto;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.mt-20 {
  margin-top: 20px;
}
.account-security {
  max-width: 900px;
  margin: 20px auto 0;
}
.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  display: block;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
}
.avatar-uploader-icon:hover {
  border-color: #409EFF;
}
</style>
