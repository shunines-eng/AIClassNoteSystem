/**
 * 笔记版本控制API服务
 */

import api from './index'

/**
 * 创建笔记版本
 * @param {Object} data - 创建版本的数据
 * @returns {Promise} API响应
 */
export const createNoteVersion = (data) => {
  return api.post('/api/note-version', data)
}

/**
 * 获取笔记的所有版本
 * @param {number} noteId - 笔记ID
 * @returns {Promise} API响应
 */
export const getNoteVersions = (noteId) => {
  return api.get(`/api/note-version/note/${noteId}`)
}

/**
 * 获取特定版本
 * @param {number} noteId - 笔记ID
 * @param {number} version - 版本号
 * @returns {Promise} API响应
 */
export const getNoteVersion = (noteId, version) => {
  return api.get(`/api/note-version/note/${noteId}/version/${version}`)
}

/**
 * 获取最新版本
 * @param {number} noteId - 笔记ID
 * @returns {Promise} API响应
 */
export const getLatestNoteVersion = (noteId) => {
  return api.get(`/api/note-version/note/${noteId}/latest`)
}

/**
 * 版本回退
 * @param {number} noteId - 笔记ID
 * @param {number} version - 目标版本号
 * @param {number} userId - 用户ID
 * @returns {Promise} API响应
 */
export const revertToVersion = (noteId, version, userId) => {
  return api.post(`/api/note-version/note/${noteId}/revert/${version}?userId=${userId}`)
}

/**
 * 比较两个版本
 * @param {number} noteId - 笔记ID
 * @param {number} version1 - 第一个版本号
 * @param {number} version2 - 第二个版本号
 * @returns {Promise} API响应
 */
export const compareVersions = (noteId, version1, version2) => {
  return api.get(`/api/note-version/note/${noteId}/compare?version1=${version1}&version2=${version2}`)
}

/**
 * 删除笔记的所有版本
 * @param {number} noteId - 笔记ID
 * @returns {Promise} API响应
 */
export const deleteNoteVersions = (noteId) => {
  return api.delete(`/api/note-version/note/${noteId}`)
}

/**
 * 删除特定版本
 * @param {number} noteId - 笔记ID
 * @param {number} version - 版本号
 * @returns {Promise} API响应
 */
export const deleteNoteVersion = (noteId, version) => {
  return api.delete(`/api/note-version/note/${noteId}/version/${version}`)
}

export default {
  createNoteVersion,
  getNoteVersions,
  getNoteVersion,
  getLatestNoteVersion,
  revertToVersion,
  compareVersions,
  deleteNoteVersions,
  deleteNoteVersion
}
