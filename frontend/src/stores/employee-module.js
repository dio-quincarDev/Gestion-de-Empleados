import { defineStore } from 'pinia'
import EmployeeService from 'src/service/employee.service.js'

export const useEmployeeStore = defineStore('employee', {
  state: () => ({
    employees: [],
    selectedEmployee: null,
    loading: false,
    error: null,
    pagination: {
      sortBy: 'name',
      descending: false,
      page: 1,
      rowsPerPage: 15, // Un valor por defecto más común
      rowsNumber: 0,
    },
  }),

  getters: {
    getAllEmployees: (state) => state.employees,
    getEmployeeById: (state) => state.selectedEmployee,
  },

  actions: {
    async fetchEmployees(pagination) {
      this.loading = true
      console.time('API call')

      this.error = null
      const pag = pagination || this.pagination
      console.timeEnd('API call')

      try {
        // Mapear paginación de q-table a parámetros de Spring API
        const params = {
          page: pag.page > 0 ? pag.page - 1 : 0, // q-table es 1-based, Spring es 0-based
          size: pag.rowsPerPage,
          sort: `${pag.sortBy},${pag.descending ? 'desc' : 'asc'}`,
        }

        const response = await EmployeeService.getEmployees(params) // response es un objeto Page de Spring

        // Actualizar store con la respuesta
        this.employees = response.content

        // Actualizar estado de paginación desde el objeto Page de Spring
        this.pagination = {
          sortBy: pag.sortBy,
          descending: pag.descending,
          page: response.number + 1, // Spring es 0-based, q-table es 1-based
          rowsPerPage: response.size,
          rowsNumber: response.totalElements,
        }
      } catch (error) {
        this.error = error
      } finally {
        this.loading = false
      }
    },

    async fetchEmployeeById(id) {
      this.loading = true
      this.error = null
      this.selectedEmployee = null
      try {
        this.selectedEmployee = await EmployeeService.getById(id)
      } catch (error) {
        this.error = error
      } finally {
        this.loading = false
      }
    },

    async createEmployee(employeeData) {
      this.loading = true
      this.error = null
      try {
        await EmployeeService.createEmployee(employeeData)
        await this.fetchEmployees(this.pagination)
      } catch (error) {
        this.error = error
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateEmployee(employeeData) {
      this.loading = true
      this.error = null
      try {
        await EmployeeService.updateEmployee(employeeData.id, employeeData)
        await this.fetchEmployees(this.pagination)
      } catch (error) {
        this.error = error
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteEmployee(employeeId) {
      this.loading = true
      this.error = null
      try {
        await EmployeeService.deleteEmployee(employeeId)
        await this.fetchEmployees(this.pagination)
      } catch (error) {
        this.error = error
        throw error
      } finally {
        this.loading = false
      }
    },
  },
})
