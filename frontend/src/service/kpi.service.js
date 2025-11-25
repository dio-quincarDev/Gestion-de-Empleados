// src/services/kpi.service.js
import { api, API_CONSTANTS } from 'src/boot/axios'

export const kpiService = {
  getManagerReport: async ({ startDate, endDate }) => {
    try {
      // Asegurar que las fechas estén en formato correcto
      const formattedStartDate = new Date(startDate).toISOString().split('T')[0]
      const formattedEndDate = new Date(endDate).toISOString().split('T')[0]

      const url = `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.KPI_ROUTE}/manager?startDate=${formattedStartDate}&endDate=${formattedEndDate}`
      const response = await api.get(url)
      return response.data
    } catch (error) {
      console.error('Error al obtener el reporte del manager:', error)
      if (error.response) {
        console.error('Status:', error.response.status)
        console.error('Data:', error.response.data)
        console.error('Headers:', error.response.headers)
      }
      throw error
    }
  },
}
