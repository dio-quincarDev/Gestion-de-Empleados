<template>
  <div v-if="report">
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
          <template v-slot:no-data>
            <div class="full-width text-center text-grey q-pa-md">Sin registros de asistencia</div>
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
          hide-pagination
          :rows-per-page-options="[0]"
        >
          <template v-slot:body-cell-consumptionDate="props">
            <q-td>{{ formatDateTime(props.row.consumptionDate) }}</q-td>
          </template>
          <template v-slot:no-data>
            <div class="full-width text-center text-grey q-pa-md">Sin consumos registrados</div>
          </template>
        </q-table>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { date } from 'quasar'

const props = defineProps({
  report: Object,
})

const attendances = computed(() => props.report?.attendanceReports || [])
const consumptions = computed(() => props.report?.individualConsumptionReports || [])

const attendanceColumns = [
  { name: 'attendanceDate', label: 'Fecha', align: 'left', field: 'attendanceDate' },
  { name: 'entryTime', label: 'Entrada', align: 'center', field: 'entryTime' },
  { name: 'exitTime', label: 'Salida', align: 'center', field: 'exitTime' },
  {
    name: 'workedHours',
    label: 'Horas',
    align: 'center',
    field: 'workedHours',
    format: (val) => `${val.toFixed(2)}h`,
  },
  {
    name: 'attendancePercentage',
    label: '% Asistencia',
    align: 'center',
    field: 'attendancePercentage',
    format: (val) => `${val.toFixed(2)}%`,
  },
]

const consumptionColumns = [
  { name: 'consumptionDate', label: 'Fecha y Hora', align: 'left', field: 'consumptionDate' },
  { name: 'description', label: 'Descripción', align: 'left', field: 'description' },
  {
    name: 'amount',
    label: 'Monto',
    align: 'right',
    field: 'amount',
    format: (val) => `$${val.toFixed(2)}`,
  },
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
</script>

<style lang="scss" scoped>
.glass-card {
  background: rgba($dark, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}
</style>
