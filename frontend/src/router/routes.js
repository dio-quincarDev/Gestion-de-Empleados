const routes = [
  {
    path: '/',
    component: () => import('layouts/PresentationLayout.vue'),
    children: [{ path: '', component: () => import('pages/PresentationPage.vue') }],
  },
  {
    path: '/auth',
    component: () => import('layouts/AuthLayout.vue'),
    meta: { requiresGuest: true }, // <-- AÑADIDO
    children: [
      { path: 'login', component: () => import('pages/auth/LoginPage.vue') },
      { path: 'register', component: () => import('pages/auth/RegisterPage.vue') },
    ],
  },
  {
    path: '/main',
    component: () => import('layouts/MainLayout.vue'),
    meta: { requiresAuth: true }, // <-- AÑADIDO PARA PROTEGER LA RUTA
    children: [
      { path: '', component: () => import('pages/IndexPage.vue') },
      {
        path: 'employees',
        component: () => import(/* webpackPrefetch: true */ 'pages/main/EmployeesPage.vue'),
        meta: { roles: ['MANAGER', 'ADMIN'] },
      },
      { path: 'consumptions', component: () => import('pages/main/ConsumptionsPage.vue') },
      { path: 'attendance', component: () => import('pages/main/AttendancePage.vue') },
      { path: 'schedules', component: () => import('pages/main/SchedulesPage.vue'), meta: { roles: ['MANAGER', 'ADMIN'] } },
      { path: 'reports', component: () => import('pages/main/ReportsPage.vue') },
      {
        path: 'employee/:id',
        name: 'employee-detail',
        component: () => import('pages/main/EmployeeDetailPage.vue'),
        props: true,
        meta: { roles: ['MANAGER', 'ADMIN'] },
      }, // <-- RUTA AÑADIDA,
    ],
  },

  {
    path: '/forbidden',
    component: () => import('pages/ForbiddenPage.vue'),
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
]

export default routes
