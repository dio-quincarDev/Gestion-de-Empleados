// src/stores/kpi-module.js
import { defineStore } from 'pinia'
import { kpiService } from 'src/service/kpi.service'

export const useKpiStore = defineStore('kpi', {
  state: () => ({
    reportData: null, // Todo el payload del backend
    loading: false,
  }),

  actions: {
    /**
     * Carga el reporte general del manager
     */
    async loadReport({ startDate, endDate }) {
      this.loading = true
      try {
        this.reportData = await kpiService.getManagerReport({ startDate, endDate })
      } finally {
        this.loading = false
      }
    },
  },
})
