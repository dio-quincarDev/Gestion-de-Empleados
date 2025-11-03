<template>
  <q-card
    class="schedule-form-card"
    :style="$q.screen.lt.sm ? 'width: 95vw;' : 'width: 700px; max-width: 90vw;'"
  >
    <q-card-section class="bg-dark text-white">
      <div class="text-h6">{{ props.schedule ? 'Editar Horario' : 'Nuevo Horario' }}</div>
    </q-card-section>

    <q-separator dark />

    <q-card-section>
      <q-form @submit="onSave" class="q-gutter-md">
        <!-- Selección de Empleado -->
        <q-select
          v-model="formData.employeeId"
          :options="employeeOptions"
          label="Empleado *"
          emit-value
          map-options
          dark
          outlined
          color="primary"
          label-color="grey-5"
          input-class="text-white"
          :rules="[(val) => !!val || 'Seleccione un empleado']"
          :disable="!!props.employeeId && !props.schedule"
        />

        <div class="row q-col-gutter-md">
          <!-- Fecha de Inicio -->
          <div class="col-12 col-sm-6">
            <q-input
              v-model="formData.startDate"
              label="Fecha Inicio *"
              type="date"
              dark
              outlined
              color="primary"
              label-color="grey-5"
              input-class="text-white"
              :rules="[(val) => !!val || 'Fecha requerida']"
            />
          </div>

          <!-- Hora de Inicio -->
          <div class="col-12 col-sm-6">
            <q-input
              v-model="formData.startTime"
              label="Hora Inicio *"
              type="time"
              dark
              outlined
              color="primary"
              label-color="grey-5"
              input-class="text-white"
              :rules="[(val) => !!val || 'Hora requerida']"
            />
          </div>
        </div>

        <div class="row q-col-gutter-md">
          <!-- Fecha de Fin -->
          <div class="col-12 col-sm-6">
            <q-input
              v-model="formData.endDate"
              label="Fecha Fin *"
              type="date"
              dark
              outlined
              color="primary"
              label-color="grey-5"
              input-class="text-white"
              :rules="[(val) => !!val || 'Fecha requerida']"
            />
          </div>

          <!-- Hora de Fin -->
          <div class="col-12 col-sm-6">
            <q-input
              v-model="formData.endTime"
              label="Hora Fin *"
              type="time"
              dark
              outlined
              color="primary"
              label-color="grey-5"
              input-class="text-white"
              :rules="[(val) => !!val || 'Hora requerida']"
            />
          </div>
        </div>

        <!-- Tipo de Horario -->
        <q-select
          v-model="formData.scheduleType"
          :options="SCHEDULE_TYPE_OPTIONS"
          label="Tipo de Horario *"
          emit-value
          map-options
          dark
          outlined
          color="primary"
          label-color="grey-5"
          input-class="text-white"
          :rules="[(val) => !!val || 'Seleccione el tipo']"
        />

        <!-- Estado -->
        <q-select
          v-model="formData.status"
          :options="SCHEDULE_STATUS_OPTIONS"
          label="Estado *"
          emit-value
          map-options
          dark
          outlined
          color="primary"
          label-color="grey-5"
          input-class="text-white"
          :rules="[(val) => !!val || 'Seleccione el estado']"
        />

        <!-- Notas/Observaciones -->
        <q-input
          v-model="formData.notes"
          label="Notas"
          type="textarea"
          dark
          outlined
          color="primary"
          label-color="grey-5"
          input-class="text-white"
          rows="3"
        />

        <!-- Validación de fechas -->
        <div v-if="dateError" class="text-negative text-caption q-mt-sm">
          {{ dateError }}
        </div>
      </q-form>
    </q-card-section>

    <q-separator dark />

    <q-card-actions align="right" class="bg-dark">
      <q-btn flat rounded label="Cancelar" color="grey-5" @click="onCancel" />
      <q-btn unelevated rounded label="Guardar" color="primary" @click="onSave" />
    </q-card-actions>
  </q-card>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useEmployeeStore } from 'src/stores/employee-module.js'
import { SCHEDULE_STATUS, SCHEDULE_TYPE } from 'src/constants/schedule.js'

defineOptions({ name: 'ScheduleForm' })

const props = defineProps({
  schedule: {
    type: Object,
    default: null,
  },
  employeeId: {
    type: [String, Number],
    default: null,
  },
})

const emit = defineEmits(['save', 'cancel'])
const $q = useQuasar()
const employeeStore = useEmployeeStore()

// Constantes para opciones
const SCHEDULE_STATUS_OPTIONS = [
  { label: 'Pendiente', value: SCHEDULE_STATUS.PENDING },
  { label: 'Confirmado', value: SCHEDULE_STATUS.CONFIRMED },
  { label: 'Cancelado', value: SCHEDULE_STATUS.CANCELLED },
]

const SCHEDULE_TYPE_OPTIONS = [
  { label: 'Regular', value: SCHEDULE_TYPE.REGULAR },
  { label: 'Horas Extra', value: SCHEDULE_TYPE.OVERTIME },
  { label: 'Feriado', value: SCHEDULE_TYPE.HOLIDAY },
]

// Estado del formulario
const formData = ref({
  employeeId: null,
  startDate: '',
  startTime: '09:00',
  endDate: '',
  endTime: '17:00',
  scheduleType: SCHEDULE_TYPE.REGULAR,
  status: SCHEDULE_STATUS.PENDING,
  notes: '',
})

const dateError = ref('')

// Computed properties
const employeeOptions = computed(() => {
  return employeeStore.employees
    .filter((emp) => emp.status === 'ACTIVE')
    .map((emp) => ({
      label: `${emp.name} - ${emp.role}`,
      value: emp.id,
    }))
})

// Watchers
watch(
  () => props.schedule,
  (newVal) => {
    if (newVal) {
      loadScheduleData(newVal)
    } else {
      resetForm()
    }
  },
  { immediate: true },
)

// Watch para validación de fechas
watch(
  [
    () => formData.value.startDate,
    () => formData.value.startTime,
    () => formData.value.endDate,
    () => formData.value.endTime,
  ],
  () => {
    validateDates()
  },
)

// Métodos
const loadScheduleData = (schedule) => {
  if (!schedule) return

  const startDateTime = new Date(schedule.startTime)
  const endDateTime = new Date(schedule.endTime)

  formData.value = {
    employeeId: schedule.employee?.id || schedule.employeeId,
    startDate: formatDateForInput(startDateTime),
    startTime: formatTimeForInput(startDateTime),
    endDate: formatDateForInput(endDateTime),
    endTime: formatTimeForInput(endDateTime),
    scheduleType: schedule.scheduleType || SCHEDULE_TYPE.REGULAR,
    status: schedule.status || SCHEDULE_STATUS.PENDING,
    notes: schedule.notes || '',
  }
}

const resetForm = () => {
  const today = new Date()

  formData.value = {
    employeeId: props.employeeId, // Pre-seleccionar el empleado del padre
    startDate: formatDateForInput(today),
    startTime: '09:00',
    endDate: formatDateForInput(today),
    endTime: '17:00',
    scheduleType: SCHEDULE_TYPE.REGULAR,
    status: SCHEDULE_STATUS.PENDING,
    notes: '',
  }
  dateError.value = ''
}

const formatDateForInput = (date) => {
  return date.toISOString().split('T')[0]
}

const formatTimeForInput = (date) => {
  return date.toTimeString().slice(0, 5)
}

const validateDates = () => {
  if (!formData.value.startDate || !formData.value.endDate) {
    dateError.value = ''
    return true
  }

  const startDateTime = new Date(`${formData.value.startDate}T${formData.value.startTime}`)
  const endDateTime = new Date(`${formData.value.endDate}T${formData.value.endTime}`)

  if (endDateTime <= startDateTime) {
    dateError.value = 'La fecha/hora de fin debe ser posterior a la de inicio'
    return false
  }

  dateError.value = ''
  return true
}

const onSave = () => {
  if (!formData.value.employeeId) {
    $q.notify({
      type: 'negative',
      message: 'Debe seleccionar un empleado.',
    })
    return
  }

  if (!validateDates()) {
    $q.notify({
      type: 'negative',
      message: 'Corrija los errores en las fechas antes de guardar',
    })
    return
  }

  // Construir objeto para enviar al backend
  const scheduleData = {
    employeeId: formData.value.employeeId,
    startTime: `${formData.value.startDate}T${formData.value.startTime}:00`,
    endTime: `${formData.value.endDate}T${formData.value.endTime}:00`,
    scheduleType: formData.value.scheduleType,
    status: formData.value.status,
    notes: formData.value.notes,
  }

  // Si estamos editando, agregar el ID
  if (props.schedule?.id) {
    scheduleData.id = props.schedule.id
  }

  emit('save', scheduleData)
}

const onCancel = () => {
  emit('cancel')
}

// Cargar empleados al montar el componente
onMounted(async () => {
  if (employeeStore.employees.length === 0) {
    try {
      await employeeStore.searchEmployees({})
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: error.message || 'Error al cargar la lista de empleados para el formulario.',
        position: 'top',
      })
    }
  }
})
</script>

<style lang="scss" scoped>
.schedule-form-card {
  background: $dark;
  border-radius: 15px;
}
</style>
