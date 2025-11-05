<template>
  <q-card
    class="attendance-details-card"
    :style="$q.screen.lt.sm ? 'width: 95vw;' : 'width: 600px; max-width: 90vw;'"
  >
    <q-card-section class="bg-dark text-white">
      <div class="text-h6">Detalles de Asistencia</div>
    </q-card-section>

    <q-separator dark />

    <q-card-section class="q-pa-lg">
      <!-- Estado de la Asistencia -->
      <div class="row items-center q-mb-md">
        <q-badge :color="statusColor" class="q-mr-sm" rounded size="md" />
        <div class="text-subtitle1 text-weight-medium">{{ formattedStatus }}</div>
        <q-space />
        <div class="text-caption text-grey-5">ID: #{{ attendance.id }}</div>
      </div>

      <!-- Información Principal -->
      <div class="q-gutter-y-lg">
        <!-- Empleado -->
        <div class="row items-start">
          <div class="col-4 text-grey-6">Empleado:</div>
          <div class="col-8 text-white">
            <div class="row items-center">
              <q-avatar size="sm" color="primary" text-color="white" class="q-mr-sm">
                {{ employeeInitial }}
              </q-avatar>
              <div>
                <div class="text-weight-medium">{{ employee?.name || 'N/A' }}</div>
                <div class="text-caption text-grey-5">{{ employee?.role || '' }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Fecha -->
        <div class="row items-start">
          <div class="col-4 text-grey-6">Fecha:</div>
          <div class="col-8 text-white">
            <div class="text-weight-medium">{{ formattedDate }}</div>
            <div class="text-caption text-grey-5">{{ formattedDayOfWeek }}</div>
          </div>
        </div>

        <!-- Horario -->
        <div class="row items-start">
          <div class="col-4 text-grey-6">Horario:</div>
          <div class="col-8 text-white">
            <div class="row items-center q-mb-xs">
              <q-icon name="login" size="sm" class="q-mr-sm text-primary" />
              <span class="text-weight-medium">{{ formattedEntryTime }}</span>
              <span class="q-mx-sm text-grey-5">•</span>
              <span class="text-caption text-grey-5">Entrada</span>
            </div>
            <div class="row items-center">
              <q-icon name="logout" size="sm" class="q-mr-sm text-primary" />
              <span class="text-weight-medium">{{ formattedExitTime }}</span>
              <span class="q-mx-sm text-grey-5">•</span>
              <span class="text-caption text-grey-5">Salida</span>
            </div>
          </div>
        </div>

        <!-- Duración -->
        <div class="row items-center">
          <div class="col-4 text-grey-6">Duración:</div>
          <div class="col-8 text-white">
            <div class="text-weight-medium">{{ calculatedDuration }}</div>
            <div v-if="hasOvertime" class="text-caption text-positive">
              + {{ overtimeDuration }} horas extra
            </div>
          </div>
        </div>

        <!-- Información de Pago -->
        <q-card v-if="employee" flat class="bg-grey-9 q-pa-md">
          <div class="text-caption text-grey-5 q-mb-xs">Información de Pago</div>
          <div class="row items-center justify-between">
            <div>
              <div class="text-weight-medium">{{ paymentRate }}</div>
              <div class="text-caption text-grey-5">Tarifa base</div>
            </div>
            <div v-if="hasOvertime && employee.paysOvertime" class="text-right">
              <div class="text-weight-medium text-positive">{{ overtimeRate }}</div>
              <div class="text-caption text-grey-5">Tarifa extra</div>
            </div>
          </div>
        </q-card>

        <!-- Análisis de Puntualidad -->
        <div class="row items-start">
          <div class="col-4 text-grey-6">Puntualidad:</div>
          <div class="col-8 text-white">
            <div class="q-mb-xs" :class="punctualityClass">
              <q-icon :name="punctualityIcon" class="q-mr-xs" />
              {{ punctualityMessage }}
            </div>
            <div v-if="lateMinutes > 0" class="text-caption text-warning">
              {{ lateMinutes }} minutos de retraso
            </div>
            <div v-else-if="earlyMinutes > 0" class="text-caption text-positive">
              {{ earlyMinutes }} minutos antes
            </div>
          </div>
        </div>

        <!-- Comparación con Horario Programado -->
        <q-card v-if="hasScheduleComparison" flat class="bg-blue-9 q-pa-md">
          <div class="text-caption text-grey-5 q-mb-xs">Comparación con Horario</div>
          <div class="row items-center justify-between">
            <div class="text-center">
              <div class="text-weight-medium">{{ scheduledStartTime }}</div>
              <div class="text-caption text-grey-5">Programado</div>
            </div>
            <q-icon name="arrow_forward" color="grey-5" />
            <div class="text-center">
              <div class="text-weight-medium" :class="actualTimeClass">
                {{ actualStartTime }}
              </div>
              <div class="text-caption text-grey-5">Real</div>
            </div>
          </div>
        </q-card>
      </div>
    </q-card-section>

    <q-separator dark />

    <q-card-actions align="right" class="bg-dark">
      <q-btn flat rounded label="Cerrar" color="grey-5" @click="onClose" />
      <q-btn flat rounded label="Editar" color="primary" @click="onEdit" />
      <q-btn flat rounded label="Eliminar" color="negative" @click="onDelete" />
    </q-card-actions>

    <!-- Dialogo de Confirmación -->
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
  </q-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useEmployeeStore } from 'src/stores/employee-module.js'
import { ATTENDANCE_STATUS } from 'src/constants/attendance.js'

defineOptions({ name: 'AttendanceDetails' })

const props = defineProps({
  attendance: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['close', 'edit', 'delete'])
const $q = useQuasar()

const employeeStore = useEmployeeStore()
const showDeleteDialog = ref(false)

// Computed Properties
const employee = computed(() => {
  return employeeStore.employees.find((emp) => emp.id === props.attendance.employeeId)
})

const employeeInitial = computed(() => {
  return employee.value?.name?.charAt(0) || '?'
})

const formattedStatus = computed(() => {
  const statusMap = {
    [ATTENDANCE_STATUS.PRESENT]: 'Presente',
    [ATTENDANCE_STATUS.LATE]: 'Llegó Tarde',
    [ATTENDANCE_STATUS.ABSENT]: 'Ausente',
  }
  return statusMap[props.attendance.status] || props.attendance.status
})

const statusColor = computed(() => {
  const colorMap = {
    [ATTENDANCE_STATUS.PRESENT]: 'positive',
    [ATTENDANCE_STATUS.LATE]: 'warning',
    [ATTENDANCE_STATUS.ABSENT]: 'negative',
  }
  return colorMap[props.attendance.status] || 'grey'
})

const formattedDate = computed(() => {
  if (!props.attendance.entryDateTime) return 'N/A'
  const date = new Date(props.attendance.entryDateTime)
  return date.toLocaleDateString('es-ES', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
})

const formattedDayOfWeek = computed(() => {
  if (!props.attendance.entryDateTime) return 'N/A'
  const date = new Date(props.attendance.entryDateTime)
  return date
    .toLocaleDateString('es-ES', {
      weekday: 'long',
    })
    .toUpperCase()
})

const formattedEntryTime = computed(() => {
  if (!props.attendance.entryDateTime) return 'N/A'
  const date = new Date(props.attendance.entryDateTime)
  return date.toLocaleTimeString('es-ES', {
    hour: '2-digit',
    minute: '2-digit',
  })
})

const formattedExitTime = computed(() => {
  if (!props.attendance.exitDateTime) return 'N/A'
  const date = new Date(props.attendance.exitDateTime)
  return date.toLocaleTimeString('es-ES', {
    hour: '2-digit',
    minute: '2-digit',
  })
})

const calculatedDuration = computed(() => {
  if (!props.attendance.entryDateTime || !props.attendance.exitDateTime) return 'N/A'

  const start = new Date(props.attendance.entryDateTime)
  const end = new Date(props.attendance.exitDateTime)
  const durationMs = end - start

  const hours = Math.floor(durationMs / (1000 * 60 * 60))
  const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60))

  if (hours === 0) return `${minutes} minutos`
  if (minutes === 0) return `${hours} horas`

  return `${hours}h ${minutes}m`
})

const hasOvertime = computed(() => {
  if (!props.attendance.entryDateTime || !props.attendance.exitDateTime) return false

  const entry = new Date(props.attendance.entryDateTime)
  const exit = new Date(props.attendance.exitDateTime)
  const standardEnd = new Date(entry)
  standardEnd.setHours(17, 0, 0, 0) // Hora estándar de salida: 5:00 PM

  return exit > standardEnd
})

const overtimeDuration = computed(() => {
  if (!hasOvertime.value) return '0h'

  const entry = new Date(props.attendance.entryDateTime)
  const exit = new Date(props.attendance.exitDateTime)
  const standardEnd = new Date(entry)
  standardEnd.setHours(17, 0, 0, 0)

  const overtimeMs = exit - standardEnd
  const hours = Math.floor(overtimeMs / (1000 * 60 * 60))
  const minutes = Math.floor((overtimeMs % (1000 * 60 * 60)) / (1000 * 60))

  if (hours === 0) return `${minutes}m`
  if (minutes === 0) return `${hours}h`

  return `${hours}h ${minutes}m`
})

const paymentRate = computed(() => {
  if (!employee.value) return 'N/A'

  if (employee.value.paymentType === 'HOURLY') {
    return `$${employee.value.hourlyRate?.toFixed(2)}/hora`
  } else {
    return `Salario: $${employee.value.salary?.toFixed(2)}/mes`
  }
})

const overtimeRate = computed(() => {
  if (!employee.value?.paysOvertime) return 'No aplica'

  const hourlyRate = employee.value.hourlyRate || 0
  const overtimeType = employee.value.overtimeRateType

  if (overtimeType === 'FIFTY_PERCENT') {
    return `$${(hourlyRate * 1.5).toFixed(2)}/hora`
  } else if (overtimeType === 'DOUBLE') {
    return `$${(hourlyRate * 2).toFixed(2)}/hora`
  }

  return 'N/A'
})

const lateMinutes = computed(() => {
  if (!props.attendance.entryDateTime || props.attendance.status !== ATTENDANCE_STATUS.LATE)
    return 0

  const entry = new Date(props.attendance.entryDateTime)
  const scheduledStart = new Date(entry)
  scheduledStart.setHours(9, 0, 0, 0) // Hora programada: 9:00 AM

  return Math.max(0, Math.round((entry - scheduledStart) / (1000 * 60)))
})

const earlyMinutes = computed(() => {
  if (!props.attendance.entryDateTime || props.attendance.status !== ATTENDANCE_STATUS.PRESENT)
    return 0

  const entry = new Date(props.attendance.entryDateTime)
  const scheduledStart = new Date(entry)
  scheduledStart.setHours(9, 0, 0, 0) // Hora programada: 9:00 AM

  return Math.max(0, Math.round((scheduledStart - entry) / (1000 * 60)))
})

const punctualityMessage = computed(() => {
  if (props.attendance.status === ATTENDANCE_STATUS.LATE) {
    return `Llegó ${lateMinutes.value} minutos tarde`
  } else if (props.attendance.status === ATTENDANCE_STATUS.PRESENT) {
    if (earlyMinutes.value > 0) {
      return `Llegó ${earlyMinutes.value} minutos antes`
    } else {
      return 'Llegó a tiempo'
    }
  } else {
    return 'No se registró asistencia'
  }
})

const punctualityClass = computed(() => {
  if (props.attendance.status === ATTENDANCE_STATUS.LATE) {
    return 'text-warning'
  } else if (props.attendance.status === ATTENDANCE_STATUS.PRESENT) {
    return earlyMinutes.value > 0 ? 'text-positive' : 'text-primary'
  } else {
    return 'text-negative'
  }
})

const punctualityIcon = computed(() => {
  if (props.attendance.status === ATTENDANCE_STATUS.LATE) {
    return 'schedule'
  } else if (props.attendance.status === ATTENDANCE_STATUS.PRESENT) {
    return earlyMinutes.value > 0 ? 'check_circle' : 'access_time'
  } else {
    return 'cancel'
  }
})

const hasScheduleComparison = computed(() => {
  return (
    props.attendance.status === ATTENDANCE_STATUS.PRESENT ||
    props.attendance.status === ATTENDANCE_STATUS.LATE
  )
})

const scheduledStartTime = computed(() => {
  return '09:00' // Hora programada estándar
})

const actualStartTime = computed(() => {
  return formattedEntryTime.value
})

const actualTimeClass = computed(() => {
  return props.attendance.status === ATTENDANCE_STATUS.LATE ? 'text-warning' : 'text-positive'
})

// Métodos
const onClose = () => {
  emit('close')
}

const onEdit = () => {
  emit('edit', props.attendance)
}

const onDelete = () => {
  showDeleteDialog.value = true
}

const confirmDelete = () => {
  showDeleteDialog.value = false
  emit('delete', props.attendance.id)
}

// Lifecycle
onMounted(async () => {
  if (employeeStore.employees.length === 0) {
    await employeeStore.searchEmployees({
      status: 'ACTIVE',
      page: 0,
      size: 50,
    })
  }
})
</script>

<style lang="scss" scoped>
.attendance-details-card {
  background: $dark;
  border-radius: 15px;
}
</style>
