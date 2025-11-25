<template>
  <q-card class="attendance-table-card">
    <q-card-section class="text-white">
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
      <div class="row q-col-gutter-md items-center">
        <!-- Filtro por Rango de Fechas -->
        <div class="col-12 col-md-5">
          <q-input
            v-model="filters.startDate"
            label="Desde"
            type="date"
            outlined
            dense
            color="primary"
            label-color="grey-5"
            input-class="text-white"
          />
        </div>
        <div class="col-12 col-md-5">
          <q-input
            v-model="filters.endDate"
            label="Hasta"
            type="date"
            outlined
            dense
            color="primary"
            label-color="grey-5"
            input-class="text-white"
          />
        </div>

        <!-- Botones de Filtro -->
        <div class="col-12 col-md-2 row justify-end">
            <q-btn flat label="Limpiar" color="grey-5" @click="clearFilters" class="q-mr-sm" />
            <q-btn
              unelevated
              label="Buscar"
              color="primary"
              @click="onSearch"
              :loading="attendanceStore.loading"
            />
        </div>
      </div>
    </q-card-section>

    <q-separator class="q-mt-md" />

    <!-- Tabla -->
    <q-card-section class="q-pa-none">
      <q-table
        :rows="attendanceStore.attendances"
        :columns="columns"
        :loading="attendanceStore.loading"
        :pagination="pagination"
        row-key="id"
        flat
        class="text-white"
        :rows-per-page-options="[10, 20, 50]"
      >
        <!-- Header Personalizado -->
        <template v-slot:header="props">
          <q-tr :props="props" class="bg-grey-9">
            <q-th v-for="col in props.cols" :key="col.name" :props="props" class="text-weight-bold">
              {{ col.label }}
            </q-th>
            <q-th class="text-weight-bold text-right">Acciones</q-th>
          </q-tr>
        </template>

        <!-- Body Personalizado -->
        <template v-slot:body="props">
          <q-tr :props="props" class="cursor-pointer" @click="onRowClick(props.row)">
            <q-td key="entry" :props="props">
              {{ formatDateTime(props.row.entryDateTime) }}
            </q-td>
            <q-td key="exit" :props="props">
              {{ formatDateTime(props.row.exitDateTime) }}
            </q-td>
            <q-td key="status" :props="props">
              <q-badge :color="getStatusColor(props.row.status)" rounded>
                {{ getStatusLabel(props.row.status) }}
              </q-badge>
            </q-td>

            <!-- Acciones -->
            <q-td auto-width>
              <div class="row no-wrap q-gutter-xs justify-end">
                <q-btn flat round icon="visibility" color="info" size="sm" @click.stop="onViewDetails(props.row)">
                  <q-tooltip>Ver Detalles</q-tooltip>
                </q-btn>
                <q-btn flat round icon="edit" color="warning" size="sm" @click.stop="onEditAttendance(props.row)">
                  <q-tooltip>Editar</q-tooltip>
                </q-btn>
                <q-btn flat round icon="delete" color="negative" size="sm" @click.stop="onDeleteAttendance(props.row)">
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
              attendanceStore.loading ? 'Cargando...' : 'No hay registros para el rango seleccionado'
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
  <q-dialog v-model="showDeleteDialog" persistent :full-width="$q.screen.lt.sm">
    <q-card class="text-white">
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
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useAttendanceStore } from 'src/stores/attendance-module.js'
import { ATTENDANCE_STATUS } from 'src/constants/attendance.js'

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

// Inicializar fechas para el rango por defecto
const today = new Date()
const thirtyDaysAgo = new Date()
thirtyDaysAgo.setDate(today.getDate() - 30)

// Estado de los filtros
const filters = ref({
  startDate: thirtyDaysAgo.toISOString().split('T')[0],
  endDate: today.toISOString().split('T')[0],
})

const showDeleteDialog = ref(false)
const selectedAttendance = ref(null)

// Paginación (se mantiene por si se implementa paginación de backend en el futuro)
const pagination = ref({
  page: 1,
  rowsPerPage: 10,
  rowsNumber: computed(() => attendanceStore.attendances.length),
})

// Columnas de la tabla simplificadas
const columns = [
  {
    name: 'entry',
    label: 'Entrada',
    field: 'entryDateTime',
    align: 'left',
    sortable: true,
  },
  {
    name: 'exit',
    label: 'Salida',
    field: 'exitDateTime',
    align: 'left',
    sortable: true,
  },
  {
    name: 'status',
    label: 'Estado',
    field: 'status',
    align: 'center',
    sortable: true,
  },
]

// Métodos
const onSearch = async () => {
  if (!props.employee?.value) {
    $q.notify({ type: 'warning', message: 'Por favor, seleccione un empleado.' })
    return
  }

  try {
    // Para manejar mejor las asistencias que cruzan medianoche, extendemos ligeramente el rango
    let startDate = filters.value.startDate;
    let endDate = filters.value.endDate;

    await attendanceStore.loadAttendanceList({
      employeeId: props.employee.value,
      startDate: startDate,
      endDate: endDate,
    })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: `Error al cargar las asistencias: ${error.message}`,
    })
  }
}

const clearFilters = () => {
  filters.value = {
    startDate: thirtyDaysAgo.toISOString().split('T')[0],
    endDate: today.toISOString().split('T')[0],
  }
  onSearch()
}

const formatDateTime = (dateString) => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleString('es-ES', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
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
    await onSearch() // Recargar la lista con los filtros actuales
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

// Cargar datos iniciales al montar el componente
onMounted(() => {
  onSearch()
})

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
