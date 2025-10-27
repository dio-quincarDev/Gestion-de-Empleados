// src/services/auth.service.js
import { api, API_CONSTANTS } from 'src/boot/axios'
import { jwtDecode } from 'jwt-decode'

export default {
  // Inicio de sesión
  async login(credentials) {
    try {
      const response = await api.post(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.AUTH_ROUTE}/login`,
        credentials,
      )

      // Guardar el token en localStorage
      if (response.data.accessToken) {
        // ← CAMBIAR: tu backend devuelve accessToken, no token
        localStorage.setItem('authToken', response.data.accessToken)
      }
      return response.data
    } catch (error) {
      console.error('Error en el inicio de sesión:', error)
      throw error
    }
  },

  // Registro de usuario
  async register(userData) {
    try {
      const response = await api.post(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.USERS_ROUTE}/register-manager`,
        userData,
      )
      return response.data
    } catch (error) {
      console.error('Error en el registro:', error)
      throw error
    }
  },

  // Verificar si el usuario está autenticado
  isAuthenticated() {
    return !!localStorage.getItem('authToken')
  },

  // Obtener token
  getToken() {
    return localStorage.getItem('authToken')
  },

  // Obtener roles del usuario desde el token JWT - CORREGIDO
  getUserRoles() {
    const token = this.getToken()
    if (token) {
      try {
        const decodedToken = jwtDecode(token)
        console.log('Token decodificado:', decodedToken) // ← DEBUG

        // Tu backend ahora devuelve "roles" como array: ["ROLE_MANAGER"]
        if (decodedToken.roles && Array.isArray(decodedToken.roles)) {
          // Normalizar roles - quitar ROLE_ y convertir a mayúsculas
          return decodedToken.roles.map((role) => {
            if (typeof role === 'string') {
              return role.replace('ROLE_', '').toUpperCase()
            }
            return role
          })
        }

        // Fallback para compatibilidad
        if (decodedToken.role) {
          const role =
            typeof decodedToken.role === 'string'
              ? decodedToken.role.replace('ROLE_', '').toUpperCase()
              : decodedToken.role
          return [role]
        }

        return []
      } catch (error) {
        console.error('Error decodificando el token:', error)
        return []
      }
    }
    return []
  },

  // Obtener usuario actual desde el token
  getCurrentUser() {
    const token = this.getToken()
    if (token) {
      try {
        const decodedToken = jwtDecode(token)
        const roles = this.getUserRoles() // Reutiliza el método existente

        return {
          email: decodedToken.sub,
          roles: roles,
          // Agrega más campos si tu token los incluye
          firstname: decodedToken.firstname,
          lastname: decodedToken.lastname,
        }
      } catch (error) {
        console.error('Error obteniendo usuario actual:', error)
        return null
      }
    }
    return null
  },

  // Borrar token
  clearToken() {
    localStorage.removeItem('authToken')
  },

  // Obtener ID del usuario actual
  getCurrentUserId() {
    const user = this.getCurrentUser()
    return user?.id || null
  },

  // Cerrar sesión
  logout() {
    try {
      this.clearToken()
      console.log('Sesión cerrada correctamente')
    } catch (error) {
      console.error('Error al cerrar sesión:', error)
    }
  },

  // Registro de Manager
  async registerManager(userData) {
    try {
      const response = await api.post(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.AUTH_ROUTE}/register-manager`, // ← CORREGIR RUTA
        userData,
      )
      return response.data
    } catch (error) {
      console.error('Error en el registro del manager:', error)
      throw error
    }
  },
}
