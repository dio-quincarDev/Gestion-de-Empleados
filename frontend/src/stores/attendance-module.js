// src/store/attendance-module.js
import { defineStore } from 'pinia'
import { attendanceService } from 'src/services/attendance.service'

export const useAttendanceStore = defineStore('attendance', {
  state: () => ({
    attendances: [],
    currentAttendance: null,
    loading: false,
    error: null,
    attendancePercentage: 0,
  }),

  getters: {
    // Obtener asistencias por empleado - CORREGIDO
    getAttendancesByEmployee: (state) => (employeeId) => {
      return state.attendances.filter((attendance) => attendance.employeeId === employeeId)
    },

    // Asistencias de hoy - CORREGIDO
    todayAttendances: (state) => {
      const today = new Date().toISOString().split('T')[0]
      return state.attendances.filter((attendance) => {
        if (!attendance.entryDateTime) return false
        const attendanceDate = new Date(attendance.entryDateTime).toISOString().split('T')[0]
        return attendanceDate === today
      })
    },
  },

  actions: {
    // Registrar asistencia - CORREGIDO (sin enviar status)
    async recordAttendance(attendanceData) {
      this.loading = true
      this.error = null
      try {
        // El backend calcula el status automáticamente, no lo enviamos
        const { ...dataToSend } = attendanceData
        const newAttendance = await attendanceService.recordAttendance(dataToSend)
        this.attendances.push(newAttendance)
        return newAttendance
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    // Cargar lista de asistencias - CORREGIDO
    async loadAttendanceList(employeeId, startDate, endDate) {
      this.loading = true
      this.error = null
      try {
        const attendances = await attendanceService.getAttendanceList(
          employeeId,
          startDate,
          endDate,
        )
        this.attendances = attendances
        return attendances
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    // Calcular porcentaje de asistencia
    async calculateAttendancePercentage(employeeId, year, month, day) {
      this.loading = true
      this.error = null
      try {
        this.attendancePercentage = await attendanceService.calculateAttendancePercentage(
          employeeId,
          year,
          month,
          day,
        )
        return this.attendancePercentage
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    // Cargar asistencias de empleado para fecha específica
    async loadEmployeeAttendances(employeeId, date) {
      return await this.loadAttendanceList(employeeId, date, date)
    },

    // Limpiar estado
    clearCurrentAttendance() {
      this.currentAttendance = null
    },

    clearError() {
      this.error = null
    },

    clearAttendancePercentage() {
      this.attendancePercentage = 0
    },

    // Resetear store
    reset() {
      this.attendances = []
      this.currentAttendance = null
      this.loading = false
      this.error = null
      this.attendancePercentage = 0
    },
  },
})
