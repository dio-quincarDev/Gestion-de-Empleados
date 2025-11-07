// src/stores/consumption-module.js
import { defineStore } from 'pinia'
import { consumptionService } from 'src/service/consumpotion.service'

export const useConsumptionStore = defineStore('consumption', {
  state: () => ({
    consumptions: [], // Lista completa
    loading: false,
  }),

  actions: {
    /**
     * Registra un nuevo consumo y lo añade a la lista local.
     */
    async createConsumption(payload) {
      this.loading = true
      try {
        const newConsumption = await consumptionService.createConsumption(payload)
        // Re-asignar el array para garantizar la reactividad
        this.consumptions = [newConsumption, ...this.consumptions]
        return newConsumption
      } finally {
        this.loading = false
      }
    },

    /**
     * Asegura que todos los consumos estén cargados, pero solo los busca si la lista está vacía.
     */
    async ensureConsumptionsLoaded() {
      if (this.consumptions.length > 0) {
        return // Ya están cargados
      }

      this.loading = true
      try {
        this.consumptions = await consumptionService.getAllConsumptions()
      } finally {
        this.loading = false
      }
    },
  },
})
