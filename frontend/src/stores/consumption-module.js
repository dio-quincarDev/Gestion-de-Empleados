// src/stores/consumption-module.js
import { defineStore } from 'pinia'
import { consumptionService } from 'src/service/consumption.service'

export const useConsumptionStore = defineStore('consumption', {
  state: () => ({
    consumptions: [],
    currentEmployeeConsumptions: [],
    totalByEmployee: 0,
    totalAllEmployees: 0,
    loading: false,
    error: null,
  }),

  getters: {
    // Obtener consumos del empleado actual ordenados por fecha
    sortedEmployeeConsumptions: (state) => {
      return [...state.currentEmployeeConsumptions].sort(
        (a, b) => new Date(b.consumptionDate) - new Date(a.consumptionDate),
      )
    },

    // Total de consumos cargados
    totalConsumptionsCount: (state) => state.consumptions.length,

    // Consumos del empleado actual count
    employeeConsumptionsCount: (state) => state.currentEmployeeConsumptions.length,
  },

  actions: {
    // CREATE - Crear nuevo consumo
    async createConsumption(payload) {
      this.loading = true
      this.error = null
      try {
        const newConsumption = await consumptionService.createConsumption(payload)
        return newConsumption
      } catch (error) {
        this.error = error.response?.data?.message || 'Error al crear consumo'
        throw error
      } finally {
        this.loading = false
      }
    },

    // READ ALL - Cargar todos los consumos (para administradores)
    async loadAllConsumptions() {
      this.loading = true
      this.error = null
      try {
        this.consumptions = await consumptionService.getAllConsumptions()
      } catch (error) {
        this.error = error.response?.data?.message || 'Error al cargar consumos'
        throw error
      } finally {
        this.loading = false
      }
    },

    // READ BY EMPLOYEE - Cargar consumos de un empleado específico
    async loadConsumptionsByEmployee(employeeId, startDate, endDate, description = null) {
      this.loading = true
      this.error = null
      try {
        this.currentEmployeeConsumptions = await consumptionService.getConsumptionsByEmployee(
          employeeId,
          startDate,
          endDate,
          description,
        )
      } catch (error) {
        this.error = error.response?.data?.message || 'Error al cargar consumos del empleado'
        throw error
      } finally {
        this.loading = false
      }
    },

    // GET TOTAL BY EMPLOYEE - Obtener total de un empleado
    async loadTotalByEmployee(employeeId, startDate, endDate) {
      this.loading = true
      this.error = null
      try {
        this.totalByEmployee = await consumptionService.getTotalByEmployee(
          employeeId,
          startDate,
          endDate,
        )
        return this.totalByEmployee
      } catch (error) {
        this.error = error.response?.data?.message || 'Error al calcular total'
        throw error
      } finally {
        this.loading = false
      }
    },

    // GET TOTAL ALL EMPLOYEES - Obtener total de todos los empleados
    async loadTotalAllEmployees(startDate, endDate) {
      this.loading = true
      this.error = null
      try {
        this.totalAllEmployees = await consumptionService.getTotalForAllEmployees(
          startDate,
          endDate,
        )
        return this.totalAllEmployees
      } catch (error) {
        this.error = error.response?.data?.message || 'Error al calcular total general'
        throw error
      } finally {
        this.loading = false
      }
    },

    // UPDATE - Actualizar consumo existente
    async updateConsumption(id, payload) {
      this.loading = true
      this.error = null
      try {
        const updated = await consumptionService.updateConsumption(id, payload)

        // Actualizar en la lista local si existe
        const index = this.currentEmployeeConsumptions.findIndex((c) => c.id === id)
        if (index !== -1) {
          this.currentEmployeeConsumptions[index] = updated
        }

        return updated
      } catch (error) {
        this.error = error.response?.data?.message || 'Error al actualizar consumo'
        throw error
      } finally {
        this.loading = false
      }
    },

    // DELETE - Eliminar consumo
    async deleteConsumption(id) {
      this.loading = true
      this.error = null
      try {
        await consumptionService.deleteConsumption(id)

        // Remover de las listas locales
        this.consumptions = this.consumptions.filter((c) => c.id !== id)
        this.currentEmployeeConsumptions = this.currentEmployeeConsumptions.filter(
          (c) => c.id !== id,
        )
      } catch (error) {
        this.error = error.response?.data?.message || 'Error al eliminar consumo'
        throw error
      } finally {
        this.loading = false
      }
    },

    // CLEAR ERROR - Limpiar errores
    clearError() {
      this.error = null
    },

    // CLEAR DATA - Limpiar datos (útil para logout)
    clearData() {
      this.consumptions = []
      this.currentEmployeeConsumptions = []
      this.totalByEmployee = 0
      this.totalAllEmployees = 0
      this.error = null
    },
  },
})
