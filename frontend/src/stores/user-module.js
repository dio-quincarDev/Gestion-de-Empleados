import { defineStore } from 'pinia'
import UserService from 'src/services/user.service'

export const useUserStore = defineStore('user', {
  state: () => ({
    users: [],
    currentUser: null,
    loading: false,
    error: null,
  }),
  actions: {
    async fetchAllUsers() {
      this.loading = true
      this.error = null
      try {
        const response = await UserService.getAllUsers()
        this.users = response.data
      } catch (error) {
        this.error = error
        console.error('Error fetching users:', error)
      } finally {
        this.loading = false
      }
    },
    async fetchUserById(id) {
      this.loading = true
      this.error = null
      try {
        const response = await UserService.getUserById(id)
        this.currentUser = response.data
      } catch (error) {
        this.error = error
        console.error(`Error fetching user ${id}:`, error)
      } finally {
        this.loading = false
      }
    },
    async createUser(userData) {
      this.loading = true
      this.error = null
      try {
        const response = await UserService.createUser(userData)
        // Optionally, refresh the list or add the new user to the list
        this.fetchAllUsers()
        return response.data
      } catch (error) {
        this.error = error
        console.error('Error creating user:', error)
        throw error // Re-throw to allow component to handle
      } finally {
        this.loading = false
      }
    },
    async updateUser(id, userData) {
      this.loading = true
      this.error = null
      try {
        const response = await UserService.updateUser(id, userData)
        // Optionally, update the user in the list or refresh
        this.fetchAllUsers()
        return response.data
      } catch (error) {
        this.error = error
        console.error(`Error updating user ${id}:`, error)
        throw error
      } finally {
        this.loading = false
      }
    },
    async deleteUser(id) {
      this.loading = true
      this.error = null
      try {
        await UserService.deleteUser(id)
        this.users = this.users.filter((user) => user.id !== id)
      } catch (error) {
        this.error = error
        console.error(`Error deleting user ${id}:`, error)
        throw error
      } finally {
        this.loading = false
      }
    },
    async updateUserRole(id, role) {
      this.loading = true
      this.error = null
      try {
        const response = await UserService.updateUserRole(id, role)
        // After successful update, re-fetch all users to ensure UI is in sync
        await this.fetchAllUsers()
        return response.data
      } catch (error) {
        this.error = error
        console.error(`Error updating user role for ${id}:`, error)
        throw error
      } finally {
        this.loading = false
      }
    },
  },
})
