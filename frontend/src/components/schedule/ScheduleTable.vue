<template>
  <q-card class="schedule-table-card bg-dark">
    <q-card-section class="bg-dark text-white">
      <div class="row items-center justify-between">
        <div class="text-h6">Gestión de Horarios</div>
      </div>
    </q-card-section>

    <q-separator dark class="q-mt-md" />

    <!-- Tabla -->
    <q-card-section class="q-pa-none">
      <q-table
        :rows="schedules"
        :columns="columns"
        :loading="loading"
        row-key="id"
        flat
        dark
        class="bg-dark text-white"
        :rows-per-page-options="[10, 20, 50]"
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
            <!-- Empleado -->
            <q-td>
              <div class="text-weight-medium">{{ props.row.employee?.name }}</div>
              <div class="text-caption text-grey-5">{{ props.row.employee?.role }}</div>
            </q-td>

            <!-- Fecha y Hora -->
            <q-td>
              <div>{{ formatTableDate(props.row.startTime) }}</div>
              <div class="text-caption text-grey-5">
                {{ formatTableTime(props.row.startTime) }} -
                {{ formatTableTime(props.row.endTime) }}
              </div>
            </q-td>

            <!-- Duración -->
            <q-td>
              {{ calculateDuration(props.row.startTime, props.row.endTime) }}
            </q-td>

            <!-- Tipo -->
            <q-td>
              <q-badge :color="getTypeColor(props.row.scheduleType)" rounded>
                {{ getTypeLabel(props.row.scheduleType) }}
              </q-badge>
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
                  @click.stop="onEditSchedule(props.row)"
                >
                  <q-tooltip>Editar</q-tooltip>
                </q-btn>

                <q-btn
                  flat
                  round
                  icon="delete"
                  color="negative"
                  size="sm"
                  @click.stop="onDeleteSchedule(props.row)"
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
            <span>{{ loading ? 'Cargando...' : 'No hay horarios registrados' }}</span>
          </div>
        </template>
      </q-table>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { ref } from 'vue'
import { SCHEDULE_STATUS, SCHEDULE_TYPE } from 'src/constants/schedule.js'

defineOptions({ name: 'ScheduleTable' })

const { schedules } = defineProps({
  schedules: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['create', 'view', 'edit', 'delete'])

const loading = ref(false)

// Métodos

const formatTableDate = (dateString) => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', {
    weekday: 'short',
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
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

const getTypeLabel = (type) => {
  const typeMap = {
    [SCHEDULE_TYPE.REGULAR]: 'Regular',
    [SCHEDULE_TYPE.OVERTIME]: 'Extra',
    [SCHEDULE_TYPE.HOLIDAY]: 'Feriado',
  }
  return typeMap[type] || type
}

const getTypeColor = (type) => {
  const colorMap = {
    [SCHEDULE_TYPE.REGULAR]: 'primary',
    [SCHEDULE_TYPE.OVERTIME]: 'orange',
    [SCHEDULE_TYPE.HOLIDAY]: 'purple',
  }
  return colorMap[type] || 'grey'
}

const getStatusLabel = (status) => {
  const statusMap = {
    [SCHEDULE_STATUS.PENDING]: 'Pendiente',
    [SCHEDULE_STATUS.CONFIRMED]: 'Confirmado',
    [SCHEDULE_STATUS.CANCELLED]: 'Cancelado',
  }
  return statusMap[status] || status
}

const getStatusColor = (status) => {
  const colorMap = {
    [SCHEDULE_STATUS.PENDING]: 'warning',
    [SCHEDULE_STATUS.CONFIRMED]: 'positive',
    [SCHEDULE_STATUS.CANCELLED]: 'negative',
  }
  return colorMap[status] || 'grey'
}

// Event Handlers

const onRowClick = (schedule) => {
  emit('view', schedule)
}

const onViewDetails = (schedule) => {
  emit('view', schedule)
}

const onEditSchedule = (schedule) => {
  emit('edit', schedule)
}

const onDeleteSchedule = (schedule) => {
  emit('delete', schedule.id)
}

// Event Handlers
</script>

<style lang="scss" scoped>
.schedule-table-card {
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
