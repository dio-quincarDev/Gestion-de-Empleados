// src/services/attendance-service.js
import { api, API_CONSTANTS } from 'src/boot/axios'

export const attendanceService = {
  // Listar asistencias por empleado y rango de fechas
  getAttendanceList: async (params) => {
    try {
      const response = await api.get(`${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.ATTENDANCE_ROUTE}/list`, { params })
      return response.data
    } catch (error) {
      console.error('Error al obtener la lista de asistencias:', error)
      throw error
    }
  },

  // Obtener porcentaje de asistencia
  getAttendancePercentage: async (params) => {
    try {
      const response = await api.get(`${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.ATTENDANCE_ROUTE}/percentage`, { params })
      return response.data
    } catch (error) {
      console.error('Error al obtener el porcentaje de asistencia:', error)
      throw error
    }
  },

  // Crear nueva asistencia
  createAttendance: async (attendanceData) => {
    try {
      const response = await api.post(`${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.ATTENDANCE_ROUTE}/`, attendanceData)
      return response.data
    } catch (error) {
      console.error('Error al registrar la asistencia:', error)
      throw error
    }
  },

  // Actualizar asistencia existente
  updateAttendance: async (id, attendanceData) => {
    try {
      const response = await api.put(`${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.ATTENDANCE_ROUTE}/${id}`, attendanceData)
      return response.data
    } catch (error) {
      console.error('Error al actualizar la asistencia:', error)
      throw error
    }
  },

  // Eliminar asistencia
  deleteAttendance: async (id) => {
    try {
      const response = await api.delete(`${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.ATTENDANCE_ROUTE}/${id}`)
      return response.data
    } catch (error) {
      console.error('Error al eliminar la asistencia:', error)
      throw error
    }
  },
}
