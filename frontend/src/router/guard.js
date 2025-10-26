import AuthService from 'src/services/auth.service'
import { Notify } from 'quasar'
// eslint-disable-next-line no-unused-vars
import { useRouter } from 'vue-router'

export function authGuard(to, from, next) {
  const isAuthenticated = AuthService.isAuthenticated()

  if (to.meta.requiresAuth && !isAuthenticated) {
    Notify.create({
      type: 'negative',
      message: 'Necesitas iniciar sesión para acceder',
    })
    next({
      path: '/login',
      query: { redirect: to.fullPath },
    })
  } else if (to.meta.roles) {
    // Si la ruta tiene roles definidos
    const userRoles = AuthService.getUserRoles()
    const hasRequiredRole = to.meta.roles.some((role) => userRoles.includes(role))

    if (!hasRequiredRole) {
      Notify.create({
        type: 'negative',
        message: 'No tienes los permisos necesarios para acceder a esta página.',
      })
      // Redirigir a una página de acceso denegado o al inicio
      next({ path: '/' }) // O a una página de error 403
    } else {
      next()
    }
  } else {
    next()
  }
}
