// src/stores/attendance-module.js
import { defineStore } from 'pinia'
import { attendanceService } from 'src/service/attendance.service'
import { employeeService } from 'src/service/employee.service'

export const useAttendanceStore = defineStore('attendance', {
  state: () => ({
    attendances: [],
    loading: false,
  }),

  actions: {
    async loadAttendanceList({ employeeId, startDate, endDate }) {
      this.loading = true
      try {
        const data = await attendanceService.getAttendanceList({ employeeId, startDate, endDate })
        this.attendances = data
      } finally {
        this.loading = false
      }
    },

    async createAttendance(payload) {
      const newAttendance = await attendanceService.createAttendance(payload)
      this.attendances.push(newAttendance)
      return newAttendance
    },

    async updateAttendance(id, payload) {
      this.loading = true
      try {
        const updatedAttendance = await attendanceService.updateAttendance(id, payload)
        const index = this.attendances.findIndex((att) => att.id === id)
        if (index !== -1) {
          this.attendances[index] = updatedAttendance
        }
        return updatedAttendance
      } catch (error) {
        console.error('Error al actualizar la asistencia:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteAttendance(id) {
      this.loading = true
      try {
        await attendanceService.deleteAttendance(id)
        this.attendances = this.attendances.filter((att) => att.id !== id)
        return true // Indicate successful deletion
      } catch (error) {
        console.error('Error al eliminar la asistencia:', error)
        throw error
      } finally {
        this.loading = false
      }
    },
  },
})
