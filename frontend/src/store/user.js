import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    username: '',
    role: '',
    token: localStorage.getItem('token') || '',
    id: localStorage.getItem('id') || null
  }),
  getters: {
    isAuthenticated: (state) => {
        // 优先从内存获取，内存没有则尝试从 localStorage 获取（作为兜底）
        return !!state.token || !!localStorage.getItem('token')
    }
  },
  actions: {
    login(username, role, token, id) {
      this.username = username
      this.role = role
      this.token = token
      this.id = id
      // 手动同步到 localStorage，作为持久化插件的补充
      if (token) localStorage.setItem('token', token)
      if (id) localStorage.setItem('id', String(id))
    },
    logout() {
      this.username = ''
      this.role = ''
      this.token = ''
      this.id = null
      localStorage.removeItem('token')
      localStorage.removeItem('id')
      localStorage.removeItem('user') // 清理持久化插件可能存的数据
    }
  },
  // 开启持久化
  persist: true
})
