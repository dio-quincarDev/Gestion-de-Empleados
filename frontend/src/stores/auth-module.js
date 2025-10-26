import { defineStore } from 'pinia'
import { api } from 'src/boot/axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('authToken') || null,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
  },
  actions: {
    async login(credentials) {
      try {
        const response = await api.post('/v1/auth/login', credentials)
        this.token = response.data.token
        this.user = response.data.user // Asumiendo que el backend devuelve el objeto de usuario
        localStorage.setItem('authToken', this.token)
        localStorage.setItem('user', JSON.stringify(this.user))
        return response.data
      } catch (error) {
        this.token = null
        this.user = null
        localStorage.removeItem('authToken')
        localStorage.removeItem('user')
        throw error
      }
    },
    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('authToken')
      localStorage.removeItem('user')
    },
    // Puedes añadir una acción para cargar el usuario desde el token si es necesario
    // async fetchUserFromToken() {
    //   if (this.token) {
    //     try {
    //       const response = await api.get('/v1/auth/me'); // Endpoint para obtener el usuario actual
    //       this.user = response.data;
    //     } catch (error) {
    //       console.error('Error fetching user from token:', error);
    //       this.logout(); // Si el token no es válido, cerrar sesión
    //     }
    //   }
    // },
  },
})
