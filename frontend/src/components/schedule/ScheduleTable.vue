<template>
  <q-card class="schedule-table-card bg-dark">
    <q-card-section class="bg-dark text-white">
      <div class="row items-center justify-between">
        <div class="text-h6">Gestión de Horarios</div>
      </div>
    </q-card-section>

    <q-separator dark class="q-mt-md" />

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
        <template v-slot:body="props">
          <q-tr :props="props" class="cursor-pointer" @click="onRowClick(props.row)">
            <q-td key="employee" :props="props">
              <div class="text-weight-medium">{{ props.row.employee?.name || 'N/A' }}</div>
              <div class="text-caption text-grey-5">
                {{ getRoleLabel(props.row.employee?.role) }}
              </div>
            </q-td>

            <q-td key="date" :props="props">
              <div>{{ formatTableDate(props.row.startTime) }}</div>
              <div class="text-caption text-grey-5">
                {{ formatTableTime(props.row.startTime) }} -
                {{ formatTableTime(props.row.endTime) }}
              </div>
            </q-td>

            <q-td key="duration" :props="props">
              {{ calculateDuration(props.row.startTime, props.row.endTime) }}
            </q-td>

            <q-td key="actions" :props="props" auto-width>
              <div class="row no-wrap q-gutter-xs">
                <q-btn
                  flat
                  round
                  dense
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
                  dense
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
                  dense
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
defineOptions({ name: 'ScheduleTable' })

defineProps({
  schedules: {
    type: Array,
    required: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['view', 'edit', 'delete'])

const columns = [
  {
    name: 'employee',
    label: 'Empleado',
    align: 'left',
    field: (row) => row.employee?.name || 'N/A',
    sortable: true,
  },
  {
    name: 'date',
    label: 'Fecha y Hora',
    align: 'left',
    field: 'startTime',
    sortable: true,
  },
  {
    name: 'duration',
    label: 'Duración',
    align: 'center',
    field: (row) => calculateDuration(row.startTime, row.endTime),
  },
  {
    name: 'actions',
    label: 'Acciones',
    align: 'center',
  },
]

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

const getRoleLabel = (role) => {
  const roleMap = {
    SECURITY: 'Seguridad',
    WAITER: 'Mesero',
    CASHIER: 'Cajero',
    BARTENDER: 'Bartender',
    CHEF: 'Chef',
    CHEF_ASSISTANT: 'Asistente de Chef',
    STOCKER: 'Almacenista',
    MAINTENANCE: 'Mantenimiento',
    ADMIN: 'Administrador',
    HOST: 'Host',
    DJ: 'DJ',
  }
  return roleMap[role] || role
}

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

:deep(.q-table tbody tr) {
  cursor: pointer;

  &:hover {
    background: $grey-9;
  }
}
</style>
