<template>
  <q-page class="q-pa-md">
    <!-- Formulario -->
    <report-form
      v-model:selected-employee="selectedEmployee"
      v-model:date-range="dateRange"
      :loading="loading"
      @load="loadReport"
      @downloadPdf="downloadPdfReport"
    />

    <!-- Sin datos -->
    <div v-if="!report && !loading" class="text-center q-pa-xl">
      <q-icon name="description" size="4em" color="grey-5" />
      <div class="text-h6 text-grey-5 q-mt-md">
        Seleccione un rango de fechas para generar el reporte
      </div>
    </div>

    <!-- Tabla con resultados -->
    <report-table v-else-if="report" :report="report" />

    <q-inner-loading :showing="loading" />
  </q-page>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useReportStore } from 'src/stores/report-module'
import ReportForm from 'components/report/ReportForm.vue'
import ReportTable from 'components/report/ReportTable.vue'
import { date } from 'quasar'
import { api, API_CONSTANTS } from 'src/boot/axios'

const reportStore = useReportStore()

const selectedEmployee = ref(null)
const dateRange = ref({
  from: date.formatDate(date.subtractFromDate(new Date(), { days: 30 }), 'YYYY-MM-DD'),
  to: date.formatDate(new Date(), 'YYYY-MM-DD'),
})

const report = computed(() => reportStore.report)
const loading = computed(() => reportStore.loading)

const loadReport = () => {
  const employeeId = selectedEmployee.value?.value || null
  if (!dateRange.value || !dateRange.value.from || !dateRange.value.to || !employeeId) {
    reportStore.report = null
    return
  }
  const from = date.formatDate(dateRange.value.from, 'YYYY-MM-DD')
  const to = date.formatDate(dateRange.value.to, 'YYYY-MM-DD')
  
  reportStore.loadReport({ employeeId, startDate: from, endDate: to })
}

const downloadPdfReport = () => {
  if (!dateRange.value || !dateRange.value.from || !dateRange.value.to) {
    // Optionally notify the user that dates are required
    return
  }
  const from = date.formatDate(dateRange.value.from, 'YYYY-MM-DD')
  const to = date.formatDate(dateRange.value.to, 'YYYY-MM-DD')

  let url = `${api.defaults.baseURL}${API_CONSTANTS.V1_ROUTE}/reports/weekly/pdf?startDate=${from}&endDate=${to}`
  if (selectedEmployee.value?.value) {
    url += `&employeeId=${selectedEmployee.value.value}`
  }
  window.open(url, '_blank')
}
</script>
