const routes = [
  {
    path: '/',
    component: () => import('layouts/PresentationLayout.vue'),
    children: [
      { path: '', component: () => import('pages/PresentationPage.vue') }
    ]
  },
  {
    path: '/auth',
    component: () => import('layouts/AuthLayout.vue'),
    children: [
      { path: 'login', component: () => import('pages/auth/LoginPage.vue') },
      { path: 'register', component: () => import('pages/auth/RegisterPage.vue') }
    ]
  },
  {
    path: '/main',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/IndexPage.vue') },
      { path: 'employees', component: () => import('pages/main/EmployeesPage.vue') },
      { path: 'consumptions', component: () => import('pages/main/ConsumptionsPage.vue') },
      { path: 'attendance', component: () => import('pages/main/AttendancePage.vue') },
      { path: 'schedules', component: () => import('pages/main/SchedulesPage.vue') },
      { path: 'reports', component: () => import('pages/main/ReportsPage.vue') }
    ],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue')
  }
]

export default routes
