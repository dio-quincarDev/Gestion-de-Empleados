// src/stores/report-module.js
import { defineStore } from 'pinia'
import { reportService } from 'src/service/report.service'

export const useReportStore = defineStore('report', {
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
        this.reportData = await reportService.getManagerReport({ startDate, endDate })
      } finally {
        this.loading = false
      }
    },
  },
})
