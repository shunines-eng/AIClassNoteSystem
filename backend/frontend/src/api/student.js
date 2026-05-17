/**
 * 学生相关API服务
 * 封装学生模块的所有后端接口调用
 */

import api from './index'

/**
 * 获取学生已加入的课程列表
 * @param {number} studentId - 学生ID
 * @returns {Promise} API响应
 */
export const getStudentCourses = (studentId) => {
  return api.get(`/api/class-joined/byStudentId/${studentId}`)
}

/**
 * 学生加入课程
 * @param {Object} data - 加入课程数据，包含classId和studentId
 * @returns {Promise} API响应
 */
export const joinCourse = (data) => {
  return api.post('/api/class-joined', data)
}

/**
 * 根据课程ID获取课程详情
 * @param {number} classId - 课程ID
 * @returns {Promise} API响应
 */
export const getCourseDetail = (classId) => {
  return api.get(`/api/class/${classId}`)
}

/**
 * 根据课程ID和userId获取作业列表
 * @param {number} classId - 课程ID
 * @Param {number} userId - 用户ID
 * @returns {Promise} API响应
 */
export const getCourseAssignments = (classId,userId) => {
  return api.get(`/api/task/byClassId/${classId}/${userId}`)
}

/**
 * 获取课程的优秀笔记
 * @param {number} classId - 课程ID
 * @returns {Promise} API响应
 */
export const getExcellentNotes = (classId) => {
  return api.get(`/api/note/excellent/${classId}`)
}

/**
 * 创建自由笔记（不关联作业）
 * @param {Object} noteData - 笔记数据
 * @returns {Promise} API响应
 */
export const createFreeNote = (noteData) => {
  return api.post('/api/note', noteData)
}

/**
 * 创建作业笔记（关联作业）
 * @param {number} taskId - 作业ID
 * @param {Object} noteData - 笔记数据
 * @returns {Promise} API响应
 */
export const createAssignmentNote = (taskId, noteData) => {
  return api.post(`/api/note/${taskId}`, noteData)
}

/**
 * 获取公开笔记列表（查阅他人笔记）
 * @param {Object} params - 查询参数
 * @returns {Promise} API响应
 */
export const getPublicNotes = (params = {}) => {
  return api.get('/api/note/public', { params })
}

/**
 * 获取笔记详情
 * @param {number} noteId - 笔记ID
 * @returns {Promise} API响应
 */
export const getNoteDetail = (noteId) => {
  return api.get(`/api/note/${noteId}`)
}

/**
 * 更新笔记
 * @param {number} noteId - 笔记ID
 * @param {Object} noteData - 笔记数据
 * @returns {Promise} API响应
 */
export const updateNote = (noteId, noteData) => {
  return api.put(`/api/note/${noteId}`, noteData)
}

/**
 * 提交举报
 * @param {Object} data - 举报数据 {type, typeId, userId, info}
 * @returns {Promise} API响应
 */
export const submitReport = (data) => {
  return api.post('/api/reports', null, { params: data })
}

/**
 * 获取所有课程列表（用于学生加入课程时选择）
 * @returns {Promise} API响应
 */
export const getAllCourses = () => {
  return api.get('/api/class')
}

/**
 * 获取用户自己的笔记列表
 * @param {number} userId - 用户ID
 * @param {Object} params - 分页参数
 * @returns {Promise} API响应
 */
export const getUserNotes = (userId, params = {}) => {
  return api.get(`/api/note/user/${userId}`, { params })
}

/**
 * 获取笔记互动统计（查看次数、点赞次数）
 * @param {number} noteId - 笔记ID
 * @param {number} userId - 用户ID
 * @returns {Promise} API响应
 */
export const getNoteInteractionStats = (noteId, userId) => {
  return api.get(`/api/note-interactions/note/${noteId}/user/${userId}/status`)
}

/**
 * 获取笔记的查看次数
 * @param {number} noteId - 笔记ID
 * @returns {Promise} API响应
 */
export const getNoteViewCount = (noteId) => {
  return api.get(`/api/note-interactions/note/${noteId}/view-count`)
}

/**
 * 获取笔记的点赞次数
 * @param {number} noteId - 笔记ID
 * @returns {Promise} API响应
 */
export const getNoteLikeCount = (noteId) => {
  return api.get(`/api/note-interactions/note/${noteId}/like-count`)
}

/**
 * 获取用户对笔记的互动状态
 * @param {number} noteId - 笔记ID
 * @param {number} userId - 用户ID
 * @returns {Promise} API响应
 */
export const getUserNoteInteractionStatus = (noteId, userId) => {
  return api.get(`/api/note-interactions/note/${noteId}/user/${userId}/status`)
}

/**
 * 切换点赞状态（点赞/取消点赞）
 * @param {number} noteId - 笔记ID
 * @param {number} userId - 用户ID
 * @returns {Promise} API响应
 */
export const toggleNoteLike = (noteId, userId) => {
  return api.post(`/api/note-interactions/note/${noteId}/user/${userId}/toggle-like`)
}

/**
 * 记录查看笔记
 * @param {number} noteId - 笔记ID
 * @param {number} userId - 用户ID
 * @returns {Promise} API响应
 */
export const viewNote = (noteId, userId) => {
  return api.post(`/api/note-interactions/note/${noteId}/user/${userId}/view`)
}

/**
 * 获取笔记的教师批改信息
 * @param {number} noteId - 笔记ID
 * @returns {Promise} API响应
 */
export const getNoteComments = (noteId) => {
  return api.get(`/api/note-comments/note/${noteId}`)
}

export default {
  getStudentCourses,
  joinCourse,
  getCourseDetail,
  getCourseAssignments,
  getExcellentNotes,
  createFreeNote,
  createAssignmentNote,
  getPublicNotes,
  getNoteDetail,
  updateNote,
  getAllCourses,
  getUserNotes,
  getNoteInteractionStats,
  getNoteViewCount,
  getNoteLikeCount,
  getUserNoteInteractionStatus,
  toggleNoteLike,
  viewNote,
  getNoteComments
}
