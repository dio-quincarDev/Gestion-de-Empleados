// src/services/report.service.js
import { api, API_CONSTANTS } from 'src/boot/axios'

export const reportService = {
  getManagerReport: async ({ startDate, endDate }) => {
    try {
      const url = `${API_CONSTANTS.V1_ROUTE}/kpi/manager?startDate=${startDate}&endDate=${endDate}`
      const response = await api.get(url)
      return response.data
    } catch (error) {
      console.error('Error al obtener el reporte del manager:', error)
      throw error
    }
  },
}
