import api from './index'

/**
 * 获取用户信息
 * @param {number} userId - 用户ID
 * @returns {Promise} API响应
 */
export const getUserInfo = (userId) => {
  return api.get(`/api/users/${userId}`)
}

/**
 * 更新用户信息
 * @param {number} userId - 用户ID
 * @param {Object} data - 用户数据
 * @returns {Promise} API响应
 */
export const updateUserInfo = (userId, data) => {
  return api.put(`/api/users/${userId}`, data)
}

/**
 * 修改密码
 * @param {number} userId - 用户ID
 * @param {string} oldPassword - 原密码
 * @param {string} newPassword - 新密码
 * @returns {Promise} API响应
 */
export const updatePassword = (userId, oldPassword, newPassword) => {
  return api.put(`/api/users/${userId}/password`, null, {
    params: { oldPassword, newPassword }
  })
}

/**
 * 更新头像
 * @param {number} userId - 用户ID
 * @param {string} avatarUrl - 头像URL
 * @returns {Promise} API响应
 */
export const updateAvatar = (userId, avatarUrl) => {
  return api.put(`/api/users/${userId}/avatar`, null, {
    params: { avatarUrl }
  })
}

export default {
  getUserInfo,
  updateUserInfo,
  updatePassword,
  updateAvatar
}
