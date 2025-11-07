// src/stores/report-module.js
import { defineStore } from 'pinia'
import { reportService } from 'src/service/report.service'

export const useReportStore = defineStore('report', {
  state: () => ({
    report: null,
    loading: false,
  }),

  actions: {
    async loadReport({ employeeId, startDate, endDate }) {
      this.loading = true
      try {
        this.report = await reportService.getCompleteReport({
          employeeId,
          startDate,
          endDate,
        })
      } finally {
        this.loading = false
      }
    },
  },
})
