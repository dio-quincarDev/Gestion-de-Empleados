<template>
  <q-page class="q-pa-md">
    <!-- Sección de Reporte por Empleado -->
    <q-card class="bg-dark text-white q-mb-md">
      <q-card-section>
        <div class="text-h6">Reporte de Empleado</div>
      </q-card-section>
      <q-separator dark />
      <q-card-section>
        <report-form
          v-model:selected-employee="selectedEmployee"
          v-model:date-range="dateRange"
          :loading="loading"
          @load="loadReport"
        />
      </q-card-section>
    </q-card>

    <!-- Sección de Descarga de Nómina General (PDF) -->
    <q-card class="bg-dark text-white q-mb-md">
      <q-card-section>
        <div class="text-h6">Descargar Reporte de Nómina General (PDF)</div>
        <div class="text-caption text-grey-5">
          Genera un reporte de nómina para el rango de fechas seleccionado. Opcionalmente, puedes
          filtrar por empleado.
        </div>
      </q-card-section>
      <q-separator dark />
      <q-card-section class="row justify-end">
        <q-btn
          unelevated
          rounded
          color="red"
          icon="picture_as_pdf"
          label="Descargar Nómina PDF"
          @click="downloadPdfReport"
          :disable="loading"
        />
      </q-card-section>
    </q-card>

    <!-- Sin datos para el reporte de empleado -->
    <div v-if="!report && !loading" class="text-center q-pa-xl">
      <q-icon name="description" size="4em" color="grey-5" />
      <div class="text-h6 text-grey-5 q-mt-md">
        Seleccione un empleado y un rango de fechas para generar el reporte
      </div>
    </div>

    <!-- Tabla con resultados del reporte de empleado -->
    <report-table v-else-if="report" :report="report" />

    <q-inner-loading :showing="loading" />
  </q-page>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useQuasar, date } from 'quasar'
import { useReportStore } from 'src/stores/report-module'
import ReportForm from 'components/report/ReportForm.vue'
import ReportTable from 'components/report/ReportTable.vue'
import { api, API_CONSTANTS } from 'src/boot/axios'

const $q = useQuasar()

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

const downloadPdfReport = async () => {
  if (!dateRange.value || !dateRange.value.from || !dateRange.value.to) {
    $q.notify({
      type: 'warning',
      message: 'Por favor, seleccione un rango de fechas.',
      position: 'top',
    })
    return
  }

  const from = date.formatDate(dateRange.value.from, 'YYYY-MM-DD')
  const to = date.formatDate(dateRange.value.to, 'YYYY-MM-DD')

  let url = `${API_CONSTANTS.V1_ROUTE}/reports/weekly/pdf?startDate=${from}&endDate=${to}`
  if (selectedEmployee.value?.value) {
    url += `&employeeId=${selectedEmployee.value.value}`
  }

  try {
    // Set loading state if available
    // loading.value = true; // Assuming 'loading' ref is available and managed

    const response = await api.get(url, {
      responseType: 'blob', // Important for downloading files
    })

    // Extract filename from Content-Disposition header if available, otherwise default
    const contentDisposition = response.headers['content-disposition']
    let filename = 'reporte_nomina.pdf'
    if (contentDisposition) {
      const filenameMatch = contentDisposition.match(/filename="([^"]+)"/)
      if (filenameMatch && filenameMatch[1]) {
        filename = filenameMatch[1]
      }
    }

    const blob = new Blob([response.data], { type: 'application/pdf' })
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(link.href)

    $q.notify({
      type: 'positive',
      message: 'Reporte PDF descargado correctamente.',
      position: 'top',
    })
  } catch (error) {
    console.error('Error al descargar el reporte PDF:', error)
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || 'Error al descargar el reporte PDF.',
      position: 'top',
    })
  } finally {
    // loading.value = false; // Assuming 'loading' ref is available and managed
  }
}
</script>
