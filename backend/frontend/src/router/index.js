import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AdminLayout from '../layouts/AdminLayout.vue'
import BlogLayout from '../layouts/BlogLayout.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'

// 学生端视图
import StudentHome from '../views/student/StudentHome.vue'
import StudentCourseOverview from '../views/student/StudentCourseOverview.vue'
import StudentCourseDetail from '../views/student/StudentCourseDetail.vue'
import StudentProfile from '../views/student/StudentProfile.vue'

// 教师端视图
import TeacherCourseManagement from '../views/teacher/TeacherCourseManagement.vue'
import TeacherAssignmentManagement from '../views/teacher/TeacherAssignmentManagement.vue'
import TeacherNoteGrading from '../views/teacher/TeacherNoteGrading.vue'
import TeacherProfile from '../views/teacher/TeacherProfile.vue'
import TeacherStudentManagement from '../views/teacher/TeacherStudentManagement.vue'

// 管理员视图
import AdminUserManagement from '../views/admin/AdminUserManagement.vue'
import AdminClassManagement from '../views/admin/AdminClassManagement.vue'
import AdminNoteManagement from '../views/admin/AdminNoteManagement.vue'
import AdminReportManagement from '../views/admin/AdminReportManagement.vue'
import { useUserStore } from '../store/user'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: { requiresAuth: false }
  },
  // 学生端路由
  {
    path: '/student',
    component: AdminLayout, // 暂时共用 AdminLayout，之后可以根据需要优化
    meta: { requiresAuth: true, role: ['student', '学生'] },
    children: [
      { path: 'home', name: 'student-home', component: StudentHome },
      { path: 'courses', name: 'student-courses', component: StudentCourseOverview },
      { path: 'courses/:id', name: 'student-course-detail', component: StudentCourseDetail },
      { path: 'profile', name: 'student-profile', component: StudentProfile },
      { path: 'notes', name: 'student-notes', component: () => import('../views/student/StudentNotesView.vue') },
      { path: 'notes/:id', name: 'student-note-detail', component: () => import('../views/student/StudentNoteDetail.vue') },
      { path: 'notes/edit/:id', name: 'note-editor', component: () => import('../views/NoteEditor.vue') }
    ]
  },
  // 教师端路由
  {
    path: '/teacher',
    component: AdminLayout,
    meta: { requiresAuth: true, role: ['teacher', '教师'] },
    children: [
      { path: 'courses', name: 'teacher-courses', component: TeacherCourseManagement },
      { path: 'assignments', name: 'teacher-assignments', component: TeacherAssignmentManagement },
      { path: 'grading/:id', name: 'teacher-note-grading', component: TeacherNoteGrading },
      { path: 'profile', name: 'teacher-profile', component: TeacherProfile },
      { path: 'students', name: 'teacher-student-management', component: TeacherStudentManagement }
    ]
  },
  // 管理员端路由
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, role: ['admin', '管理员'] },
    children: [
      { path: 'users', name: 'admin-users', component: AdminUserManagement },
      { path: 'classes', name: 'admin-classes', component: AdminClassManagement },
      { path: 'notes', name: 'admin-notes', component: AdminNoteManagement },
      { path: 'reports', name: 'admin-reports', component: AdminReportManagement }
    ]
  },
  {
    path: '/blog',
    component: BlogLayout,
    children: [
      {
        path: 'notes',
        name: 'public-notes',
        component: () => import('../views/PublicNotesView.vue')
      },
      {
        path: 'notes/:id',
        name: 'note-detail',
        component: () => import('../views/NoteDetailView.vue')
      }
    ]
  },
  {
    path: '/home',
    name: 'home',
    component: HomeView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 导航守卫：处理登录持久化和路由权限
router.beforeEach((to) => {
  const userStore = useUserStore()
  const isAuthenticated = userStore.isAuthenticated
  const userRole = userStore.role || ''

  console.log(`[路由守卫] 正在跳转到: ${to.path}, 是否已登录: ${isAuthenticated}, 当前角色: ${userRole}`)

  // 1. 如果去往需要登录的页面
  if (to.meta.requiresAuth) {
    if (!isAuthenticated) {
      console.warn('[路由守卫] 未登录或 Token 为空，重定向到 /login')
      return { name: 'login', query: { redirect: to.fullPath } }
    } else {
      // 已登录，检查角色权限
      const normalizedRole = userRole.toLowerCase();
      const allowedRoles = (to.meta.role || []).map(r => r.toLowerCase());

      if (to.meta.role && !allowedRoles.includes(normalizedRole)) {
        console.warn(`[路由守卫] 角色不匹配: 页面要求 ${to.meta.role}, 用户当前角色为 ${userRole}`)
        // 根据角色跳转回其各自首页
        if (normalizedRole === 'student' || normalizedRole === '学生') return '/student/home'
        if (normalizedRole === 'teacher' || normalizedRole === '教师') return '/teacher/courses'
        if (normalizedRole === 'admin' || normalizedRole === '管理员') return '/admin/users'
        
        console.error('[路由守卫] 无法识别的角色:', userRole)
        return '/login'
      }
      return true // 放行
    }
  } 
  
  // 2. 如果去往登录/注册页且已登录
  if (isAuthenticated && (to.name === 'login' || to.name === 'register')) {
    const normalizedRole = userRole.toLowerCase();
    console.log('[路由守卫] 用户已登录，拦截登录页访问并跳转到其首页')
    if (normalizedRole === 'student' || normalizedRole === '学生') return '/student/home'
    if (normalizedRole === 'teacher' || normalizedRole === '教师') return '/teacher/courses'
    if (normalizedRole === 'admin' || normalizedRole === '管理员') return '/admin/users'
    return '/home'
  } 

  // 3. 其他页面（公开页面）
  return true
})

export default router
