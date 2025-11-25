// src/services/schedule.service.js
import { api, API_CONSTANTS } from 'src/boot/axios'

export const scheduleService = {
  // Crear un nuevo schedule
  createSchedule: async (scheduleData) => {
    try {
      const response = await api.post(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.SCHEDULE_ROUTE}/`,
        scheduleData,
      );
      return response.data;
    } catch (error) {
      console.error('Error creating schedule:', error);
      throw error;
    }
  },

  // Obtener schedule por ID
  getScheduleById: async (id) => {
    try {
      const response = await api.get(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.SCHEDULE_ROUTE}/${id}`,
      )
      return response.data
    } catch (error) {
      console.error('Error fetching schedule by ID:', error)
      throw error
    }
  },

  // Obtener schedules por empleado
  getSchedulesByEmployee: async (employeeId) => {
    try {
      const response = await api.get(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.SCHEDULE_ROUTE}/employee/${employeeId}`,
      )
      return response.data
    } catch (error) {
      console.error('Error fetching schedules by employee:', error)
      throw error
    }
  },

  // Actualizar schedule
  updateSchedule: async (id, scheduleData) => {
    try {
      const response = await api.put(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.SCHEDULE_ROUTE}/${id}`,
        scheduleData,
      )
      return response.data
    } catch (error) {
      console.error('Error updating schedule:', error)
      throw error
    }
  },

  // Eliminar schedule
  deleteSchedule: async (id) => {
    try {
      await api.delete(`${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.SCHEDULE_ROUTE}/${id}`)
      return true
    } catch (error) {
      console.error('Error deleting schedule:', error)
      throw error
    }
  },
}
