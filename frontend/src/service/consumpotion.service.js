// src/services/consumption.service.js
import { api, API_CONSTANTS } from 'src/boot/axios'

export const consumptionService = {
  /**
   * Registra un nuevo consumo
   * POST /v1/consumptions/
   */
  createConsumption: async (consumptionData) => {
    try {
      const response = await api.post(`${API_CONSTANTS.V1_ROUTE}/consumptions/`, consumptionData)
      return response.data // ConsumptionClass
    } catch (error) {
      console.error('Error al registrar el consumo:', error)
      throw error
    }
  },

  /**
   * Obtiene el total de consumos por empleado en un rango de fechas
   * GET /v1/consumptions/total?employeeId=...&startDate=...&endDate=...
   */
  getTotalByEmployee: async ({ employeeId, startDate, endDate }) => {
    try {
      const response = await api.get(`${API_CONSTANTS.V1_ROUTE}/consumptions/total`, {
        params: { employeeId, startDate, endDate },
      })
      return response.data // BigDecimal
    } catch (error) {
      console.error('Error al obtener el total de consumos:', error)
      throw error
    }
  },

  /**
   * Obtiene todos los consumos (opcional)
   * GET /v1/consumptions/
   */
  getAllConsumptions: async () => {
    try {
      const response = await api.get(`${API_CONSTANTS.V1_ROUTE}/consumptions/`)
      return response.data // List<ConsumptionDto>
    } catch (error) {
      console.error('Error al obtener todos los consumos:', error)
      throw error
    }
  },

  /**
   * Obtiene un consumo por su ID
   * GET /v1/consumptions/{id}
   */
  getConsumptionById: async (id) => {
    try {
      const response = await api.get(`${API_CONSTANTS.V1_ROUTE}/consumptions/${id}`)
      return response.data // ConsumptionDto
    } catch (error) {
      console.error(`Error al obtener el consumo con ID ${id}:`, error)
      throw error
    }
  },
}
