import AuthService from 'src/service/auth.service.js'
import { Notify } from 'quasar'

export function authGuard(to, from, next) {
  const isAuthenticated = AuthService.isAuthenticated()

  // Regla 1: Usuario autenticado intenta ir a rutas de guest
  if (to.meta.requiresGuest && isAuthenticated) {
    return next({ path: '/main' })
  }

  // Regla 2: Usuario no autenticado intenta ir a ruta protegida
  if (to.meta.requiresAuth && !isAuthenticated) {
    Notify.create({
      type: 'negative',
      message: 'Necesitas iniciar sesión para acceder',
    })
    return next({ path: '/auth/login', query: { redirect: to.fullPath } })
  }

  // Regla 3: Verificación de roles - CORREGIDO
  if (to.meta.roles && isAuthenticated) {
    const userRoles = AuthService.getUserRoles()
    console.log('Roles requeridos:', to.meta.roles, 'Roles usuario:', userRoles) // Debug

    const hasRequiredRole = to.meta.roles.some(
      (requiredRole) => userRoles.includes(requiredRole.toUpperCase()), // ← Normalizar a mayúsculas
    )

    if (!hasRequiredRole) {
      Notify.create({
        type: 'negative',
        message: 'No tienes permisos para acceder a esta sección',
      })
      return next({ path: '/forbidden' })
    }
  }

  return next()
}
