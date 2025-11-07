// src/services/report.service.js
import { api, API_CONSTANTS } from 'src/boot/axios'

export const reportService = {
  getCompleteReport: async ({ employeeId, startDate, endDate }) => {
    try {
      let url = `${API_CONSTANTS.V1_ROUTE}/reports/complete?startDate=${startDate}&endDate=${endDate}`
      if (employeeId) {
        url += `&employeeId=${employeeId}`
      }

      const response = await api.get(url)
      return response.data // ReportDto
    } catch (error) {
      console.error('Error al obtener reporte completo:', error)
      throw error
    }
  },
}
