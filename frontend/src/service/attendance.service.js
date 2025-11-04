// src/services/attendance.service.js
import api, { API_CONSTANTS } from 'src/boot/axios'

export const attendanceService = {
  // Registrar asistencia
  recordAttendance: async (attendanceData) => {
    const response = await api.post(
      `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.ATTENDANCE_ROUTE}/`,
      attendanceData,
    )
    return response.data
  },

  // Obtener lista de asistencias por empleado y rango de fechas
  getAttendanceList: async (employeeId, startDate, endDate) => {
    const params = {
      employeeId,
      startDate,
      endDate,
    }
    const response = await api.get(
      `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.ATTENDANCE_ROUTE}/list`,
      { params },
    )
    return response.data
  },

  // Calcular porcentaje de asistencia
  calculateAttendancePercentage: async (employeeId, year, month, day) => {
    const params = {
      employeeId,
      year,
      month,
      day,
    }
    const response = await api.get(
      `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.ATTENDANCE_ROUTE}/percentage`,
      { params },
    )
    return response.data
  },

  // Obtener asistencias de un empleado para una fecha específica
  getEmployeeAttendances: async (employeeId, date) => {
    return await attendanceService.getAttendanceList(employeeId, date, date)
  },
}
