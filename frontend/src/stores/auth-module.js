import { defineStore } from 'pinia'
import { api } from 'src/boot/axios'
import AuthService from 'src/services/auth.service.js' // ← IMPORTAR EL SERVICIO

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null, // ← NO guardar en localStorage, calcular desde token
    token: localStorage.getItem('authToken') || null,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    currentUser: (state) => {
      if (state.token) {
        return AuthService.getCurrentUser() // ← Usar el servicio
      }
      return null
    },
    userRoles: (state) => {
      if (state.token) {
        return AuthService.getUserRoles() // ← Usar el servicio corregido
      }
      return []
    },
  },
  actions: {
    async login(credentials) {
      try {
        const response = await api.post('/v1/auth/login', credentials)
        this.token = response.data.accessToken // ← CAMBIAR: accessToken, no token
        localStorage.setItem('authToken', this.token)

        // El usuario se calcula desde el token, no se guarda
        this.user = AuthService.getCurrentUser()

        return response.data
      } catch (error) {
        this.token = null
        this.user = null
        localStorage.removeItem('authToken')
        throw error
      }
    },
    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('authToken')
    },
  },
})
