<template>
  <q-card class="attendance-table-card bg-dark">
    <q-card-section class="bg-dark text-white">
      <div class="row items-center justify-between">
        <div class="text-h6">Registros de Asistencia para {{ employee.label }}</div>
        <q-btn
          unelevated
          rounded
          color="primary"
          icon="add"
          label="Nueva Asistencia"
          @click="onCreateAttendance"
        />
      </div>
    </q-card-section>

    <!-- Filtros -->
    <q-card-section class="q-pb-none">
      <div class="row q-col-gutter-md">
        <!-- Filtro por Estado -->
        <div class="col-12 col-sm-6">
          <q-select
            v-model="filters.status"
            :options="statusOptions"
            label="Estado"
            clearable
            emit-value
            map-options
            dark
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
          />
        </div>

        <!-- Filtro por Fecha -->
        <div class="col-12 col-sm-6">
          <q-input
            v-model="filters.date"
            label="Fecha"
            type="date"
            clearable
            dark
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
          />
        </div>
      </div>

      <!-- Botones de Filtro -->
      <div class="row justify-end q-mt-md">
        <q-btn flat label="Limpiar" color="grey-5" @click="clearFilters" class="q-mr-sm" />
        <q-btn
          unelevated
          label="Buscar"
          color="primary"
          @click="onSearch"
          :loading="attendanceStore.loading"
        />
      </div>
    </q-card-section>

    <q-separator dark class="q-mt-md" />

    <!-- Tabla -->
    <q-card-section class="q-pa-none">
      <q-table
        :rows="filteredAttendances"
        :columns="columns"
        :loading="attendanceStore.loading"
        :pagination="pagination"
        row-key="id"
        flat
        dark
        class="bg-dark text-white"
        :rows-per-page-options="[10, 20, 50]"
        @request="onTableRequest"
      >
        <!-- Header Personalizado -->
        <template v-slot:header="props">
          <q-tr :props="props" class="bg-grey-9">
            <q-th v-for="col in props.cols" :key="col.name" :props="props" class="text-weight-bold">
              {{ col.label }}
            </q-th>
            <q-th class="text-weight-bold">Acciones</q-th>
          </q-tr>
        </template>

        <!-- Body Personalizado -->
        <template v-slot:body="props">
          <q-tr :props="props" class="cursor-pointer" @click="onRowClick(props.row)">
            <!-- Fecha -->
            <q-td>
              <div>{{ formatTableDate(props.row.entryDateTime) }}</div>
              <div class="text-caption text-grey-5">
                {{ formatDayOfWeek(props.row.entryDateTime) }}
              </div>
            </q-td>

            <!-- Horario -->
            <q-td>
              <div class="text-weight-medium">{{ formatTableTime(props.row.entryDateTime) }}</div>
              <div class="text-caption text-grey-5">
                a {{ formatTableTime(props.row.exitDateTime) }}
              </div>
            </q-td>

            <!-- Duración -->
            <q-td>
              <div>{{ calculateDuration(props.row.entryDateTime, props.row.exitDateTime) }}</div>
            </q-td>

            <!-- Estado -->
            <q-td>
              <q-badge :color="getStatusColor(props.row.status)" rounded>
                {{ getStatusLabel(props.row.status) }}
              </q-badge>
            </q-td>

            <!-- Acciones -->
            <q-td auto-width>
              <div class="row no-wrap q-gutter-xs">
                <q-btn
                  flat
                  round
                  icon="visibility"
                  color="info"
                  size="sm"
                  @click.stop="onViewDetails(props.row)"
                >
                  <q-tooltip>Ver Detalles</q-tooltip>
                </q-btn>

                <q-btn
                  flat
                  round
                  icon="edit"
                  color="warning"
                  size="sm"
                  @click.stop="onEditAttendance(props.row)"
                >
                  <q-tooltip>Editar</q-tooltip>
                </q-btn>

                <q-btn
                  flat
                  round
                  icon="delete"
                  color="negative"
                  size="sm"
                  @click.stop="onDeleteAttendance(props.row)"
                >
                  <q-tooltip>Eliminar</q-tooltip>
                </q-btn>
              </div>
            </q-td>
          </q-tr>
        </template>

        <!-- Estado Vacío -->
        <template v-slot:no-data>
          <div class="full-width row flex-center text-grey q-pa-lg">
            <q-icon name="schedule" size="2em" class="q-mr-sm" />
            <span>{{
              attendanceStore.loading ? 'Cargando...' : 'No hay registros de asistencia'
            }}</span>
          </div>
        </template>

        <!-- Loading State -->
        <template v-slot:loading>
          <q-inner-loading showing color="primary" />
        </template>
      </q-table>
    </q-card-section>
  </q-card>

  <!-- Dialogo de Confirmación de Eliminación -->
  <q-dialog v-model="showDeleteDialog" persistent>
    <q-card class="bg-dark text-white" style="width: 300px">
      <q-card-section>
        <div class="text-h6">Confirmar Eliminación</div>
      </q-card-section>

      <q-card-section class="q-pt-none">
        ¿Está seguro de que desea eliminar este registro de asistencia?
      </q-card-section>

      <q-card-actions align="right">
        <q-btn flat label="Cancelar" color="grey-5" v-close-popup />
        <q-btn flat label="Eliminar" color="negative" @click="confirmDelete" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useQuasar } from 'quasar'
import { useAttendanceStore } from 'src/stores/attendance-module.js'
import { ATTENDANCE_STATUS, ATTENDANCE_STATUS_OPTIONS } from 'src/constants/attendance.js'

defineOptions({ name: 'AttendanceTable' })

const props = defineProps({
  employee: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['create', 'view', 'edit', 'delete'])
const $q = useQuasar()

const attendanceStore = useAttendanceStore()

// Estado
const filters = ref({
  status: null,
  date: '',
})

const showDeleteDialog = ref(false)
const selectedAttendance = ref(null)

// Paginación
const pagination = ref({
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0,
})

// Columnas de la tabla
const columns = [
  {
    name: 'date',
    label: 'Fecha',
    field: (row) => row.entryDateTime,
    align: 'left',
    sortable: true,
  },
  {
    name: 'schedule',
    label: 'Horario',
    field: (row) => row.entryDateTime,
    align: 'center',
    sortable: false,
  },
  {
    name: 'duration',
    label: 'Duración',
    field: (row) => row.duration,
    align: 'center',
    sortable: false,
  },
  {
    name: 'status',
    label: 'Estado',
    field: (row) => row.status,
    align: 'center',
    sortable: true,
  },
]

// Computed
const filteredAttendances = computed(() => {
  let filtered = attendanceStore.attendances

  // Filtrar por estado
  if (filters.value.status) {
    filtered = filtered.filter((att) => att.status === filters.value.status)
  }

  // Filtrar por fecha
  if (filters.value.date) {
    filtered = filtered.filter((att) => {
      const attDate = new Date(att.entryDateTime).toISOString().split('T')[0]
      return attDate === filters.value.date
    })
  }

  // Ordenar por fecha más reciente primero
  return filtered.sort((a, b) => new Date(b.entryDateTime) - new Date(a.entryDateTime))
})

// Watch para actualizar la paginación
watch(filteredAttendances, (newValue) => {
  pagination.value.rowsNumber = newValue.length
})

const statusOptions = computed(() => ATTENDANCE_STATUS_OPTIONS)

// Métodos
const loadAttendances = async () => {
  try {
    const endDate = new Date().toISOString().split('T')[0]
    const startDate = new Date()
    startDate.setDate(startDate.getDate() - 30)
    const startDateStr = startDate.toISOString().split('T')[0]

    await attendanceStore.loadAttendanceList(props.employee.id, startDateStr, endDate)
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: `Error al cargar las asistencias: ${error.message}`,
    })
  }
}

const onTableRequest = async (props) => {
  const { page, rowsPerPage } = props.pagination

  pagination.value.page = page
  pagination.value.rowsPerPage = rowsPerPage

  // En una implementación real con backend paginado, aquí harías la llamada API
  // Por ahora usamos filtrado local
  props.pagination.rowsNumber = pagination.value.rowsNumber
}

const onSearch = () => {
  pagination.value.page = 1
  // The filtering is done in the computed property
}

const clearFilters = () => {
  filters.value = {
    status: null,
    date: '',
  }
  onSearch()
}

const formatTableDate = (dateString) => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

const formatDayOfWeek = (dateString) => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', { weekday: 'long' })
}

const formatTableTime = (dateString) => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleTimeString('es-ES', {
    hour: '2-digit',
    minute: '2-digit',
  })
}

const calculateDuration = (startTime, endTime) => {
  if (!startTime || !endTime) return 'N/A'

  const start = new Date(startTime)
  const end = new Date(endTime)
  const durationMs = end - start

  const hours = Math.floor(durationMs / (1000 * 60 * 60))
  const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60))

  if (hours === 0) return `${minutes}m`
  if (minutes === 0) return `${hours}h`

  return `${hours}h ${minutes}m`
}

const getStatusLabel = (status) => {
  if (status === null) return 'Pendiente'
  const statusMap = {
    [ATTENDANCE_STATUS.PRESENT]: 'Presente',
    [ATTENDANCE_STATUS.LATE]: 'Tarde',
    [ATTENDANCE_STATUS.ABSENT]: 'Ausente',
  }
  return statusMap[status] || status
}

const getStatusColor = (status) => {
  if (status === null) return 'grey'
  const colorMap = {
    [ATTENDANCE_STATUS.PRESENT]: 'positive',
    [ATTENDANCE_STATUS.LATE]: 'warning',
    [ATTENDANCE_STATUS.ABSENT]: 'negative',
  }
  return colorMap[status] || 'grey'
}

// Event Handlers
const onCreateAttendance = () => {
  emit('create')
}

const onRowClick = (attendance) => {
  emit('view', attendance)
}

const onViewDetails = (attendance) => {
  emit('view', attendance)
}

const onEditAttendance = (attendance) => {
  emit('edit', attendance)
}

const onDeleteAttendance = (attendance) => {
  selectedAttendance.value = attendance
  showDeleteDialog.value = true
}

const confirmDelete = async () => {
  if (!selectedAttendance.value) return

  try {
    await attendanceStore.deleteAttendance(selectedAttendance.value.id)
    $q.notify({
      type: 'positive',
      message: 'Registro de asistencia eliminado correctamente',
    })
    emit('delete', selectedAttendance.value.id)
    await loadAttendances()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: `Error al eliminar el registro de asistencia: ${error.message}`,
    })
  } finally {
    showDeleteDialog.value = false
    selectedAttendance.value = null
  }
}
</script>

<style lang="scss" scoped>
.attendance-table-card {
  border-radius: 15px;
}

:deep(.q-table__top) {
  background: $dark;
}

:deep(.q-table__bottom) {
  background: $dark;
  border-top: 1px solid $grey-8;
}

:deep(.q-table tbody tr:hover) {
  background: $grey-9;
}
</style>
