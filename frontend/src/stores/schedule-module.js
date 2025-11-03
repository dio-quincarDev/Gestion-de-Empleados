// src/store/schedule-module.js
import { defineStore } from 'pinia'
import { scheduleService } from 'src/service/schedule.service'

export const useScheduleStore = defineStore('schedule', {
  state: () => ({
    schedules: [],
    currentSchedule: null,
    loading: false,
    error: null,
  }),

  getters: {
    // Obtener schedules por empleado
    getSchedulesByEmployee: (state) => (employeeId) => {
      return state.schedules.filter(
        (schedule) => schedule.employee && schedule.employee.id === employeeId,
      )
    },

    // Schedules activos (basado en fecha)
    activeSchedules: (state) => {
      const now = new Date()
      return state.schedules.filter((schedule) => {
        if (!schedule.startTime || !schedule.endTime) return false
        const start = new Date(schedule.startTime)
        const end = new Date(schedule.endTime)
        return start <= now && end >= now
      })
    },
  },

  actions: {
    // Crear schedule
    async createSchedule(scheduleData) {
      this.loading = true
      this.error = null
      try {
        const newSchedule = await scheduleService.createSchedule(scheduleData)
        return newSchedule
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    // Cargar schedule por ID
    async loadScheduleById(id) {
      this.loading = true
      this.error = null
      try {
        this.currentSchedule = await scheduleService.getScheduleById(id)
        return this.currentSchedule
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    // Cargar schedules por empleado y enriquecerlos
    async loadSchedulesByEmployee(employee) {
      if (!employee || !employee.id) {
        this.schedules = []
        return []
      }

      this.loading = true
      this.error = null
      try {
        const schedules = await scheduleService.getSchedulesByEmployee(employee.id)

        // Enrich schedules with the provided employee object
        const enrichedSchedules = schedules.map((schedule) => ({
          ...schedule,
          employee,
        }))

        this.schedules = enrichedSchedules
        return enrichedSchedules
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    // Actualizar schedule
    async updateSchedule(id, scheduleData) {
      this.loading = true
      this.error = null
      try {
        const updatedSchedule = await scheduleService.updateSchedule(id, scheduleData)
        return updatedSchedule
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    // Eliminar schedule
    async deleteSchedule(id) {
      this.loading = true
      this.error = null
      try {
        await scheduleService.deleteSchedule(id)
        return true
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },



    // Limpiar estado
    clearCurrentSchedule() {
      this.currentSchedule = null
    },

    clearError() {
      this.error = null
    },

    // Resetear store
    reset() {
      this.schedules = []
      this.currentSchedule = null
      this.loading = false
      this.error = null
    },
  },
})
