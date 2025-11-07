<template>
  <q-page class="q-pa-md">
    <!-- Filtros -->
    <div class="row q-mb-md q-gutter-sm items-center">
      <q-select
        v-model="selectedEmployee"
        :options="employeeOptions"
        label="Seleccionar Empleado"
        option-label="label"
        option-value="value"
        filled
        dark
        clearable
        use-input
        input-debounce="300"
        @filter="filterFn"
        style="min-width: 300px"
        class="q-mr-sm"
      />
      <q-date v-model="dateRange" range minimal dark color="primary" class="q-mx-sm" />
      <q-btn flat round icon="refresh" color="white" @click="loadReport" :loading="loading" />
    </div>

    <!-- Sin datos -->
    <div v-if="!report && !loading" class="text-center q-pa-xl">
      <q-icon name="description" size="4em" color="grey-5" />
      <div class="text-h6 text-grey-5 q-mt-md">
        Seleccione un rango de fechas para generar el reporte
      </div>
    </div>

    <!-- Reporte -->
    <div v-else-if="report">
      <q-card class="glass-card q-mb-md">
        <q-card-section>
          <div class="text-h6 text-primary">
            Reporte Completo
            <span v-if="selectedEmployee" class="text-white"> - {{ selectedEmployee.label }} </span>
          </div>
          <div class="text-caption text-grey-5">
            {{ formatRange }}
          </div>
        </q-card-section>
      </q-card>

      <!-- Totales -->
      <div class="row q-gutter-md q-mb-md">
        <q-card class="glass-card col-12 col-sm-6">
          <q-card-section class="text-center">
            <q-icon name="schedule" size="lg" color="primary" />
            <div class="text-h5 text-primary">
              {{ report.totalAttendanceHours?.toFixed(2) ?? '0.00' }}h
            </div>
            <div class="text-caption">Horas Totales</div>
          </q-card-section>
        </q-card>

        <q-card class="glass-card col-12 col-sm-6">
          <q-card-section class="text-center">
            <q-icon name="receipt_long" size="lg" color="warning" />
            <div class="text-h5 text-warning">
              ${{ report.totalConsumptionAmount?.toFixed(2) ?? '0.00' }}
            </div>
            <div class="text-caption">Consumo Total</div>
          </q-card-section>
        </q-card>
      </div>

      <!-- Asistencias -->
      <q-card class="glass-card q-mb-md">
        <q-card-section>
          <div class="text-subtitle1 text-primary q-mb-sm">Asistencias</div>
          <q-table
            :rows="attendances"
            :columns="attendanceColumns"
            row-key="attendanceDate"
            flat
            dark
            :loading="loading"
            hide-pagination
            :rows-per-page-options="[0]"
          >
            <template v-slot:body-cell-attendanceDate="props">
              <q-td>{{ formatDate(props.row.attendanceDate) }}</q-td>
            </template>
            <template v-slot:body-cell-entryTime="props">
              <q-td>{{ formatTime(props.row.entryTime) }}</q-td>
            </template>
            <template v-slot:body-cell-exitTime="props">
              <q-td>{{ formatTime(props.row.exitTime) }}</q-td>
            </template>
          </q-table>
        </q-card-section>
      </q-card>

      <!-- Consumos -->
      <q-card class="glass-card">
        <q-card-section>
          <div class="text-subtitle1 text-warning q-mb-sm">Consumos</div>
          <q-table
            :rows="consumptions"
            :columns="consumptionColumns"
            row-key="consumptionDate"
            flat
            dark
            :loading="loading"
            hide-pagination
            :rows-per-page-options="[0]"
          >
            <template v-slot:body-cell-consumptionDate="props">
              <q-td>{{ formatDateTime(props.row.consumptionDate) }}</q-td>
            </template>
          </q-table>
        </q-card-section>
      </q-card>
    </div>

    <q-inner-loading :showing="loading" />
  </q-page>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useReportStore } from 'src/stores/report-module'
import { useEmployeeStore } from 'src/stores/employee-module'
import { date } from 'quasar'

const reportStore = useReportStore()
const employeeStore = useEmployeeStore()

const selectedEmployee = ref(null)
const dateRange = ref({
  from: date.formatDate(date.subtractFromDate(new Date(), { days: 30 }), 'YYYY-MM-DD'),
  to: date.formatDate(new Date(), 'YYYY-MM-DD'),
})

const employeeOptions = ref([])

const report = computed(() => reportStore.report)
const loading = computed(() => reportStore.loading)

const attendances = computed(() => report.value?.attendanceReports || [])
const consumptions = computed(() => report.value?.individualConsumptionReports || [])

const formatRange = computed(() => {
  if (!dateRange.value || !dateRange.value.from || !dateRange.value.to) {
    return '--'
  }
  const from = date.formatDate(dateRange.value.from, 'DD/MM/YYYY')
  const to = date.formatDate(dateRange.value.to, 'DD/MM/YYYY')
  return `${from} - ${to}`
})

onMounted(async () => {
  await employeeStore.searchEmployees({})
  employeeOptions.value = employeeStore.employees.map((emp) => ({
    label: emp.name,
    value: emp.id,
  }))
})

const filterFn = async (val, update) => {
  if (val === '') {
    await employeeStore.searchEmployees({})
  } else {
    await employeeStore.searchEmployees({ name: val })
  }

  update(() => {
    employeeOptions.value = employeeStore.employees.map((emp) => ({
      label: emp.name,
      value: emp.id,
    }))
  })
}

const attendanceColumns = [
  { name: 'attendanceDate', label: 'Fecha', align: 'left' },
  { name: 'entryTime', label: 'Entrada', align: 'center' },
  { name: 'exitTime', label: 'Salida', align: 'center' },
  { name: 'workedHours', label: 'Horas', align: 'center' },
  { name: 'attendancePercentage', label: '% Asistencia', align: 'center' },
]

const consumptionColumns = [
  { name: 'consumptionDate', label: 'Fecha y Hora', align: 'left' },
  { name: 'description', label: 'Descripción', align: 'left' },
  { name: 'amount', label: 'Monto', align: 'right' },
]

const formatDate = (arr) => {
  if (!arr || arr.length < 3) return '--'
  return date.formatDate(new Date(arr[0], arr[1] - 1, arr[2]), 'DD/MM/YYYY')
}

const formatTime = (arr) => {
  if (!arr || arr.length < 2) return '--'
  return `${String(arr[0]).padStart(2, '0')}:${String(arr[1]).padStart(2, '0')}`
}

const formatDateTime = (iso) => {
  return date.formatDate(iso, 'DD/MM/YYYY HH:mm')
}

const loadReport = () => {
  if (!dateRange.value || !dateRange.value.from || !dateRange.value.to) {
    reportStore.report = null // Clear the report if date range is invalid
    return
  }
  const from = date.formatDate(dateRange.value.from, 'YYYY-MM-DD')
  const to = date.formatDate(dateRange.value.to, 'YYYY-MM-DD')
  
  const employeeId = selectedEmployee.value?.value || null
  reportStore.loadReport({ employeeId, startDate: from, endDate: to })
}

watch(
  [selectedEmployee, dateRange],
  () => {
    loadReport()
  },
  { deep: true },
)
</script>

<style lang="scss" scoped>
.glass-card {
  background: rgba($dark, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}
</style>
