// src/services/consumption.service.js
import { api, API_CONSTANTS } from 'src/boot/axios'

const BASE = `${API_CONSTANTS.V1_ROUTE}/consumptions`

export const consumptionService = {
  // CREATE
  createConsumption: async (consumptionData) => {
    const response = await api.post(`${BASE}/`, consumptionData)
    return response.data
  },

  // READ ALL consumptions
  getAllConsumptions: async () => {
    const response = await api.get(`${BASE}/`)
    return response.data
  },

  // READ consumptions by employee with date range and optional description
  getConsumptionsByEmployee: async (employeeId, startDate, endDate, description = null) => {
    const params = { startDate, endDate }
    if (description) params.description = description

    const response = await api.get(`${BASE}/employee/${employeeId}`, { params })
    return response.data
  },

  // READ consumption by ID
  getConsumptionById: async (id) => {
    const response = await api.get(`${BASE}/${id}`)
    return response.data
  },

  // UPDATE
  updateConsumption: async (id, consumptionData) => {
    const response = await api.put(`${BASE}/${id}`, consumptionData)
    return response.data
  },

  // DELETE
  deleteConsumption: async (id) => {
    await api.delete(`${BASE}/${id}`)
  },

  // GET total by employee
  getTotalByEmployee: async (employeeId, startDate, endDate) => {
    const response = await api.get(`${BASE}/total`, {
      params: { employeeId, startDate, endDate },
    })
    return response.data
  },

  // GET total for all employees
  getTotalForAllEmployees: async (startDate, endDate) => {
    const response = await api.get(`${BASE}/total/all`, {
      params: { startDate, endDate },
    })
    return response.data
  },
}
