import { api, API_CONSTANTS } from 'src/boot/axios'

export const employeeService = {
  // Obtener todos los empleados
  createEmployee: async (employeeData) => {
    try {
      const response = await api.post(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.EMPLOYEE_ROUTE}`,
        employeeData,
      )
      return response.data
    } catch (error) {
      console.error('Error al crear el empleado:', error)
      throw error
    }
  },

  getById: async (id) => {
    try {
      const response = await api.get(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.EMPLOYEE_ROUTE}/${id}`,
      )
      return response.data
    } catch (error) {
      console.error('Error al obtener el empleado:', error)
      throw error
    }
  },

  getEmployees: async (params) => {
    try {
      const response = await api.get(`${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.EMPLOYEE_ROUTE}`, {
        params,
      })
      return response.data
    } catch (error) {
      console.error('Error al obtener los empleados:', error)
      throw error
    }
  },

  // Actualizar un empleado
  updateEmployee: async (id, employeeData) => {
    try {
      const response = await api.put(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.EMPLOYEE_ROUTE}/${id}`,
        employeeData,
      )
      return response.data
    } catch (error) {
      console.error('Error al actualizar el empleado:', error)
      throw error
    }
  },

  searchEmployees: async (params) => {
    const queryParams = new URLSearchParams()

    if (params.name) queryParams.append('name', params.name)
    if (params.role) queryParams.append('role', params.role)
    if (params.status) queryParams.append('status', params.status)
    if (params.page !== undefined) queryParams.append('page', params.page)
    if (params.size) queryParams.append('size', params.size)

    const response = await api.get(`/v1/employees/search?${queryParams.toString()}`)
    return response.data
  },

  // Eliminar un empleado
  deleteEmployee: async (id) => {
    try {
      const response = await api.delete(
        `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.EMPLOYEE_ROUTE}/${id}`,
      )
      return response.data
    } catch (error) {
      console.error('Error al eliminar el empleado:', error)
      throw error
    }
  },
}