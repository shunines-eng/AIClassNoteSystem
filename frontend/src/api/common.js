import api from './index'

/**
 * 标签模糊搜索
 * @param {string} keyword - 关键词
 * @returns {Promise} API响应
 */
export const searchTags = (keyword) => {
  return api.get('/api/tag/search', { params: { keyword } })
}

/**
 * 上传文件
 * @param {FormData} formData - 包含文件的表单数据
 * @returns {Promise} API响应
 */
export const uploadFile = (formData) => {
  return api.post('/api/files/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export default {
  searchTags,
  uploadFile
}
