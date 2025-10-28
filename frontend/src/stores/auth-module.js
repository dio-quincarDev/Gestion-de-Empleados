import { defineStore } from 'pinia'
import AuthService from 'src/services/auth.service.js'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: AuthService.getToken() || null, // ✅ Usar el servicio para obtener el token
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    currentUser: () => {
      return AuthService.getCurrentUser() // ✅ Siempre calcular desde el token
    },
    userRoles: () => {
      return AuthService.getUserRoles() // ✅ Siempre calcular desde el token
    },
  },

  actions: {
    async login(credentials) {
      try {
        const response = await AuthService.login(credentials)
        this.token = response.accessToken // ✅ response ya es el objeto, no response.data

        // Recalcular usuario desde el nuevo token
        this.user = AuthService.getCurrentUser()

        return response
      } catch (error) {
        this.token = null
        this.user = null
        AuthService.clearToken()
        throw error
      }
    },

    logout() {
      this.token = null
      this.user = null
      AuthService.logout()
    },
  },
})
