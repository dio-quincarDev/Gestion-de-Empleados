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
      if (response.data.token) {
        localStorage.setItem('authToken', response.data.token)
      }
      return response.data // Retornar datos procesados
    } catch (error) {
      console.error('Error en el inicio de sesión:', error)
      throw error // Lanzar error para manejo en el componente
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

  // Obtener roles del usuario desde el token JWT
  getUserRoles() {
    const token = this.getToken()
    if (token) {
      try {
        const decodedToken = jwtDecode(token)
        // Priorizar el campo 'role' singular si existe
        if (decodedToken.role) {
          return [decodedToken.role] // Devolver como array para consistencia
        } else if (decodedToken.roles) {
          return decodedToken.roles
        } else if (decodedToken.authorities) {
          // Si solo hay 'authorities', extraer y limpiar el prefijo 'ROLE_'
          return decodedToken.authorities.map((auth) => auth.authority.replace('ROLE_', ''))
        }
        return []
      } catch (error) {
        console.error('Error decodificando el token:', error)
        return []
      }
    }
    return []
  },

  // Borrar token
  clearToken() {
    localStorage.removeItem('authToken')
  },

  // Obtener ID del usuario actual desde el token JWT
  getCurrentUserId() {
    const token = this.getToken()
    if (token) {
      try {
        const decodedToken = jwtDecode(token)
        // Asumiendo que el ID del usuario está en un campo 'id' o 'sub' (subject)
        // en el payload del token. Ajusta esto según la estructura de tu token.
        return decodedToken.id || decodedToken.sub
      } catch (error) {
        console.error('Error decodificando el token para obtener el ID:', error)
        return null
      }
    }
    return null
  },

  // Cerrar sesión
  logout() {
    try {
      this.clearToken() // Borrar token de sesión
      console.log('Sesión cerrada correctamente')
    } catch (error) {
      console.error('Error al cerrar sesión:', error)
    }
  },

  // Registro de Manager
  async registerManager(userData) {
    try {
      const response = await api.post(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.USERS_ROUTE}/register-manager`,
        userData,
      )
      return response.data
    } catch (error) {
      console.error('Error en el registro del manager:', error)
      throw error
    }
  },
}
