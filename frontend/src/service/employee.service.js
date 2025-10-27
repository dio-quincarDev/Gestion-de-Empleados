import { api, API_CONSTANTS } from 'src/boot/axios'
export default {
  // Obtener todos los empleados
  async createEmployee(employeeData) {
    try {
      const response = await api.post(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.EMPLOYEES_ROUTE}`,
        employeeData,
      )
      return response.data
    } catch (error) {
      console.error('Error al crear el empleado:', error)
      throw error
    }
  },

  async getById(id) {
    try {
      const response = await api.get(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.EMPLOYEES_ROUTE}/${id}`,
      )
      return response.data
    } catch (error) {
      console.error('Error al obtener el empleado:', error)
      throw error
    }
  },

  async getEmployees() {
    try {
      const response = await api.get(`${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.EMPLOYEES_ROUTE}`)
      return response.data
    } catch (error) {
      console.error('Error al obtener los empleados:', error)
      throw error
    }
  },

  // Actualizar un empleado
  async updateEmployee(id, employeeData) {
    try {
      const response = await api.put(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.EMPLOYEES_ROUTE}/${id}`,
        employeeData,
      )
      return response.data
    } catch (error) {
      console.error('Error al actualizar el empleado:', error)
      throw error
    }
  },

  // Eliminar un empleado
  async deleteEmployee(id) {
    try {
      const response = await api.delete(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.EMPLOYEES_ROUTE}/${id}`,
      )
      return response.data
    } catch (error) {
      console.error('Error al eliminar el empleado:', error)
      throw error
    }
  },
}
