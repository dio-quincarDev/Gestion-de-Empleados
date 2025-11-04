<template>
  <q-card
    class="schedule-details-card"
    :style="$q.screen.lt.sm ? 'width: 95vw;' : 'width: 600px; max-width: 90vw;'"
  >
    <q-card-section class="bg-dark text-white">
      <div class="text-h6">Detalles del Horario</div>
    </q-card-section>

    <q-separator dark />

    <q-card-section class="q-pa-lg">


      <!-- Información Principal -->
      <div class="q-gutter-y-lg">
        <!-- Empleado -->
        <div class="row items-start">
          <div class="col-4 text-grey-6">Empleado:</div>
          <div class="col-8 text-white">
            <div class="text-weight-medium">{{ schedule.employee?.name || 'N/A' }}</div>
            <div class="text-caption text-grey-5">{{ schedule.employee?.role || '' }}</div>
          </div>
        </div>

        <!-- Fecha y Hora -->
        <div class="row items-start">
          <div class="col-4 text-grey-6">Horario:</div>
          <div class="col-8 text-white">
            <div class="text-weight-medium">{{ formattedDateRange }}</div>
            <div class="text-caption text-grey-5">{{ formattedTimeRange }}</div>
            <div class="text-caption text-grey-5 q-mt-xs">Duración: {{ calculateDuration() }}</div>
          </div>
        </div>



        <!-- Información de Pago -->
        <div v-if="showPaymentInfo" class="row items-start">
          <div class="col-4 text-grey-6">Información de Pago:</div>
          <div class="col-8 text-white">
            <div class="text-weight-medium">{{ paymentRate }}</div>
            <div v-if="schedule.scheduleType === 'OVERTIME'" class="text-caption text-grey-5">
              Tarifa Extra: {{ overtimeRate }}
            </div>
          </div>
        </div>



        <!-- Información de Creación -->
        <q-separator dark class="q-my-md" />

        <div class="row items-center">
          <div class="col-4 text-grey-6">Creado:</div>
          <div class="col-8 text-caption text-grey-5">
            {{ formatCreationDate(schedule.createdAt) }}
          </div>
        </div>

        <div v-if="schedule.updatedAt" class="row items-center">
          <div class="col-4 text-grey-6">Actualizado:</div>
          <div class="col-8 text-caption text-grey-5">
            {{ formatCreationDate(schedule.updatedAt) }}
          </div>
        </div>
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
          ¿Está seguro de que desea eliminar este horario?
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
import { ref, computed } from 'vue'
import { useQuasar } from 'quasar'

defineOptions({ name: 'ScheduleDetails' })

const props = defineProps({
  schedule: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['close', 'edit', 'delete'])
const $q = useQuasar()

const showDeleteDialog = ref(false)

// Computed Properties


const formattedDateRange = computed(() => {
  if (!props.schedule.startTime || !props.schedule.endTime) return 'N/A'

  const start = new Date(props.schedule.startTime)
  const end = new Date(props.schedule.endTime)

  const startDate = start.toLocaleDateString('es-ES', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })

  // Si es el mismo día
  if (start.toDateString() === end.toDateString()) {
    return startDate.charAt(0).toUpperCase() + startDate.slice(1)
  }

  const endDate = end.toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })

  return `${startDate} - ${endDate}`
})

const formattedTimeRange = computed(() => {
  if (!props.schedule.startTime || !props.schedule.endTime) return 'N/A'

  const start = new Date(props.schedule.startTime)
  const end = new Date(props.schedule.endTime)

  const startTime = start.toLocaleTimeString('es-ES', {
    hour: '2-digit',
    minute: '2-digit',
  })

  const endTime = end.toLocaleTimeString('es-ES', {
    hour: '2-digit',
    minute: '2-digit',
  })

  return `${startTime} - ${endTime}`
})

const showPaymentInfo = computed(() => {
  return props.schedule.employee && props.schedule.employee.hourlyRate
})

const paymentRate = computed(() => {
  if (!props.schedule.employee) return 'N/A'

  const employee = props.schedule.employee
  if (employee.paymentType === 'HOURLY') {
    return `$${employee.hourlyRate?.toFixed(2)}/hora`
  } else {
    return `Salario: $${employee.salary?.toFixed(2)}/mes`
  }
})

const overtimeRate = computed(() => {
  if (!props.schedule.employee?.paysOvertime) return 'No aplica'

  const hourlyRate = props.schedule.employee.hourlyRate || 0
  const overtimeType = props.schedule.employee.overtimeRateType

  if (overtimeType === 'FIFTY_PERCENT') {
    return `$${(hourlyRate * 1.5).toFixed(2)}/hora (50% extra)`
  } else if (overtimeType === 'DOUBLE') {
    return `$${(hourlyRate * 2).toFixed(2)}/hora (100% extra)`
  }

  return 'N/A'
})

// Métodos
const calculateDuration = () => {
  if (!props.schedule.startTime || !props.schedule.endTime) return 'N/A'

  const start = new Date(props.schedule.startTime)
  const end = new Date(props.schedule.endTime)
  const durationMs = end - start

  const hours = Math.floor(durationMs / (1000 * 60 * 60))
  const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60))

  if (hours === 0) return `${minutes} minutos`
  if (minutes === 0) return `${hours} horas`

  return `${hours}h ${minutes}m`
}

const formatCreationDate = (dateString) => {
  if (!dateString) return 'N/A'

  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const onClose = () => {
  emit('close')
}

const onEdit = () => {
  emit('edit', props.schedule)
}

const onDelete = () => {
  showDeleteDialog.value = true
}

const confirmDelete = () => {
  showDeleteDialog.value = false
  emit('delete', props.schedule.id)
}
</script>

<style lang="scss" scoped>
.schedule-details-card {
  background: $dark;
  border-radius: 15px;
}
</style>
