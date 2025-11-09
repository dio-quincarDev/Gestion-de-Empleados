<template>
  <q-page class="flex column flex-center q-pa-md">
    <div class="dashboard-container">


      <div class="text-h4 text-white q-mb-lg text-center">Colaboradores 1800</div>

      <!-- Métricas Clave -->
      <div class="q-gutter-md q-mb-xl metrics-grid">
        <!-- Empleados Activos -->
        <q-card class="metric-card animated fadeInUp">
          <q-card-section class="text-center">
            <q-icon name="people" size="lg" color="positive" />
            <div class="text-h5 text-positive">
              {{ loading ? '...' : (report?.totalActiveEmployees ?? '--') }}
            </div>
            <div class="text-caption text-grey">Activos</div>
          </q-card-section>
        </q-card>

        <!-- Empleados Inactivos -->
        <q-card class="metric-card animated fadeInUp delay-1">
          <q-card-section class="text-center">
            <q-icon name="person_off" size="lg" color="negative" />
            <div class="text-h5 text-negative">
              {{ loading ? '...' : (report?.totalInactiveEmployees ?? '--') }}
            </div>
            <div class="text-caption text-grey">Inactivos</div>
          </q-card-section>
        </q-card>

        <!-- Total Horas Trabajadas -->
        <q-card class="metric-card animated fadeInUp delay-2">
          <q-card-section class="text-center">
            <q-icon name="schedule" size="lg" color="primary" />
            <div class="text-h5 text-primary">
              {{ loading ? '...' : formatHours(report?.totalHoursWorkedOverall) }}
            </div>
            <div class="text-caption text-grey">Horas Totales</div>
          </q-card-section>
        </q-card>

        <!-- Total Consumos -->
        <q-card class="metric-card animated fadeInUp delay-3">
          <q-card-section class="text-center">
            <q-icon name="receipt_long" size="lg" color="warning" />
            <div class="text-h5 text-warning">
              ${{ loading ? '...' : formatAmount(report?.totalConsumptionsOverall) }}
            </div>
            <div class="text-caption text-grey">Consumos Totales</div>
          </q-card-section>
        </q-card>
      </div>

      <!-- Top 5 por Horas y Consumos -->
      <div class="row q-gutter-md q-mb-xl">
        <!-- Top 5 Horas -->
        <div class="col-12 col-md-6">
          <q-card class="animated fadeInUp delay-4">
            <q-card-section>
              <div class="text-subtitle1 text-primary q-mb-sm">Top 5 por Horas Trabajadas</div>
              <q-list v-if="topHours.length" dense>
                <q-item v-for="(emp, i) in topHours" :key="emp.employeeId">
                  <q-item-section avatar>
                    <q-avatar :color="i === 0 ? 'positive' : 'grey-7'" text-color="white" size="sm">
                      {{ i + 1 }}
                    </q-avatar>
                  </q-item-section>
                  <q-item-section>
                    <q-item-label class="text-weight-medium">{{ emp.employeeName }}</q-item-label>
                    <q-item-label caption>{{ formatHours(emp.totalHoursWorked) }}h</q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <q-linear-progress :value="emp.percentage" color="primary" size="6px" rounded />
                  </q-item-section>
                </q-item>
              </q-list>
              <div v-else class="text-center text-grey q-pa-md">No hay datos</div>
            </q-card-section>
          </q-card>
        </div>

        <!-- Top 5 Consumos -->
        <div class="col-12 col-md-6">
          <q-card class="animated fadeInUp delay-5">
            <q-card-section>
              <div class="text-subtitle1 text-warning q-mb-sm">Top 5 por Consumo</div>
              <q-list v-if="topConsumptions.length" dense>
                <q-item v-for="(emp, i) in topConsumptions" :key="emp.employeeId">
                  <q-item-section avatar>
                    <q-avatar :color="i === 0 ? 'warning' : 'grey-7'" text-color="white" size="sm">
                      {{ i + 1 }}
                    </q-avatar>
                  </q-item-section>
                  <q-item-section>
                    <q-item-label class="text-weight-medium">{{ emp.employeeName }}</q-item-label>
                    <q-item-label caption>${{ formatAmount(emp.totalConsumptions) }}</q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <q-linear-progress :value="emp.percentage" color="warning" size="6px" rounded />
                  </q-item-section>
                </q-item>
              </q-list>
              <div v-else class="text-center text-grey q-pa-md">No hay datos</div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- Acciones Rápidas (manteniendo tu estilo) -->
      <div class="q-gutter-md actions-grid">
        <q-btn
          class="full-width animated fadeInUp delay-6"
          label="Registrar Asistencia"
          icon="how_to_reg"
          size="lg"
          to="/main/attendance"
          unelevated
          rounded
          color="primary"
        />
        <q-btn
          class="full-width animated fadeInUp delay-7"
          label="Añadir Consumo"
          icon="add_shopping_cart"
          size="lg"
          to="/main/consumptions"
          unelevated
          rounded
          color="primary"
        />
        <q-btn
          class="full-width animated fadeInUp delay-8"
          label="Ver Empleados"
          icon="group"
          size="lg"
          to="/main/employees"
          unelevated
          rounded
          color="primary"
        />
        <q-btn
          class="full-width animated fadeInUp delay-9"
          label="Planificar Horarios"
          icon="calendar_today"
          size="lg"
          to="/main/schedules"
          unelevated
          rounded
          color="primary"
        />
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useKpiStore } from 'src/stores/kpi-module'
import { date } from 'quasar'

const kpiStore = useKpiStore()

// Filtros de fecha (últimos 30 días por defecto)
const dateRange = ref({
  from: date.formatDate(date.subtractFromDate(new Date(), { days: 30 }), 'YYYY-MM-DD'),
  to: date.formatDate(new Date(), 'YYYY-MM-DD'),
})

const loading = computed(() => kpiStore.loading)
const report = computed(() => kpiStore.reportData)

// Cálculo de máximos para barras
const maxHours = computed(() => {
  const values = report.value?.topEmployeesByHoursWorked?.map((e) => e.totalHoursWorked) || [1]
  return Math.max(...values, 1)
})

const maxConsumption = computed(() => {
  const values = report.value?.topEmployeesByConsumptions?.map((e) => e.totalConsumptions) || [1]
  return Math.max(...values, 1)
})

const topHours = computed(() => {
  return (report.value?.topEmployeesByHoursWorked || []).map((emp) => ({
    ...emp,
    percentage: emp.totalHoursWorked / maxHours.value,
  }))
})

const topConsumptions = computed(() => {
  return (report.value?.topEmployeesByConsumptions || []).map((emp) => ({
    ...emp,
    percentage: emp.totalConsumptions / maxConsumption.value,
  }))
})

// Formateo
const formatHours = (hours) => {
  if (hours == null) return '--'
  return hours % 1 === 0 ? hours.toFixed(0) : hours.toFixed(1)
}

const formatAmount = (amount) => {
  if (amount == null) return '--'
  return Number(amount).toFixed(2)
}

// Cargar reporte
const loadReport = () => {
  if (!dateRange.value || !dateRange.value.from || !dateRange.value.to) {
    kpiStore.reportData = null
    return
  }
  const from = date.formatDate(dateRange.value.from, 'YYYY-MM-DD')
  const to = date.formatDate(dateRange.value.to, 'YYYY-MM-DD')
  kpiStore.loadReport({ startDate: from, endDate: to })
}

// Carga inicial
onMounted(() => {
  loadReport()
})


</script>

<style lang="scss" scoped>
/* === TUS ESTILOS ORIGINALES (sin cambios) === */
.dashboard-container {
  max-width: 900px;
  width: 100%;
  padding: 20px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
}

.metric-card {
  background: $dark; // Fondo sólido para un look minimalista
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 15px;
  // Transiciones y efectos de hover eliminados para un diseño más estático y limpio
  .text-h6 {
    font-weight: 600;
  }
}

.actions-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 15px;
}



/* Animaciones */
.animated.fadeInUp {
  animation-duration: 0.6s;
  animation-fill-mode: both;
}
.delay-1 {
  animation-delay: 0.1s;
}
.delay-2 {
  animation-delay: 0.2s;
}
.delay-3 {
  animation-delay: 0.3s;
}
.delay-4 {
  animation-delay: 0.4s;
}
.delay-5 {
  animation-delay: 0.5s;
}
.delay-6 {
  animation-delay: 0.6s;
}
.delay-7 {
  animation-delay: 0.7s;
}
.delay-8 {
  animation-delay: 0.8s;
}
.delay-9 {
  animation-delay: 0.9s;
}

@media (min-width: 600px) {
  .actions-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }
}
</style>