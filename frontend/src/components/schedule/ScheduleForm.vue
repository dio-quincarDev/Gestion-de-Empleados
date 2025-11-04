<template>
  <q-card
    class="schedule-form-card"
    :style="$q.screen.lt.sm ? 'width: 95vw;' : 'width: 600px; max-width: 90vw;'"
  >
    <q-card-section class="bg-dark text-white">
      <div class="text-h6">{{ props.schedule ? 'Editar Horario' : 'Nuevo Horario' }}</div>
    </q-card-section>

    <q-separator dark />

    <q-card-section>
      <q-form @submit.prevent="onSave" class="q-gutter-md">
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
          :disable="!!props.employeeId"
        />

        <div class="row q-col-gutter-md">
          <!-- Fecha y Hora de Inicio -->
          <div class="col-12">
            <q-input
              v-model="formData.entryDateTime"
              label="Fecha y Hora de Entrada *"
              type="datetime-local"
              dark
              outlined
              color="primary"
              label-color="grey-5"
              input-class="text-white"
              :rules="[(val) => !!val || 'Fecha y hora requeridas']"
            />
          </div>

          <!-- Fecha y Hora de Fin -->
          <div class="col-12">
            <q-input
              v-model="formData.exitDateTime"
              label="Fecha y Hora de Salida *"
              type="datetime-local"
              dark
              outlined
              color="primary"
              label-color="grey-5"
              input-class="text-white"
              :rules="[(val) => !!val || 'Fecha y hora requeridas']"
            />
          </div>
        </div>

        <!-- Duración calculada -->
        <div v-if="calculatedDuration" class="text-caption text-grey-5">
          Duración: {{ calculatedDuration }}
        </div>

        <!-- Validación de fechas -->
        <div v-if="dateError" class="text-negative text-caption">
          {{ dateError }}
        </div>
      </q-form>
    </q-card-section>

    <q-separator dark />

    <q-card-actions align="right" class="bg-dark">
      <q-btn flat rounded label="Cancelar" color="grey-5" @click="onCancel" />
      <q-btn
        unelevated
        rounded
        label="Guardar"
        color="primary"
        @click="onSave"
        :disable="!!dateError"
      />
    </q-card-actions>
  </q-card>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useEmployeeStore } from 'src/stores/employee-module.js'

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

const formData = ref({
  employeeId: null,
  entryDateTime: '',
  exitDateTime: '',
})

const dateError = ref('')

const employeeOptions = computed(() => {
  return employeeStore.employees
    .filter((emp) => emp.status === 'ACTIVE')
    .map((emp) => ({
      label: `${emp.name} - ${emp.role}`,
      value: emp.id,
    }))
})

const calculatedDuration = computed(() => {
  if (!formData.value.entryDateTime || !formData.value.exitDateTime) return null

  const start = new Date(formData.value.entryDateTime)
  const end = new Date(formData.value.exitDateTime)
  const durationMs = end - start

  if (durationMs <= 0) return null

  const hours = Math.floor(durationMs / (1000 * 60 * 60))
  const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60))

  if (hours === 0) return `${minutes}m`
  if (minutes === 0) return `${hours}h`
  return `${hours}h ${minutes}m`
})

const formatDateTimeForInput = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  // Formato YYYY-MM-DDTHH:mm para input type=datetime-local
  return date.toISOString().slice(0, 16)
}

const resetForm = () => {
  const now = new Date()
  const endTime = new Date(now.getTime() + 8 * 60 * 60 * 1000) // +8 horas

  formData.value = {
    employeeId: props.employeeId || null,
    entryDateTime: formatDateTimeForInput(now),
    exitDateTime: formatDateTimeForInput(endTime),
  }
  dateError.value = ''
}

const loadScheduleData = (schedule) => {
  if (!schedule) return

  formData.value = {
    employeeId: schedule.employee?.id || schedule.employeeId,
    entryDateTime: formatDateTimeForInput(schedule.entryDateTime || schedule.startTime),
    exitDateTime: formatDateTimeForInput(schedule.exitDateTime || schedule.endTime),
  }
}

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

watch([() => formData.value.entryDateTime, () => formData.value.exitDateTime], () => {
  validateDates()
})

const validateDates = () => {
  if (!formData.value.entryDateTime || !formData.value.exitDateTime) {
    dateError.value = ''
    return true
  }

  const start = new Date(formData.value.entryDateTime)
  const end = new Date(formData.value.exitDateTime)

  if (end <= start) {
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

  const scheduleData = {
    employeeId: formData.value.employeeId,
    startTime: new Date(formData.value.entryDateTime).toISOString().slice(0, 19),
    endTime: new Date(formData.value.exitDateTime).toISOString().slice(0, 19),
  }

  if (props.schedule?.id) {
    scheduleData.id = props.schedule.id
  }

  emit('save', scheduleData)
}

const onCancel = () => {
  emit('cancel')
}

onMounted(async () => {
  if (employeeStore.employees.length === 0) {
    try {
      await employeeStore.searchEmployees({ status: 'ACTIVE' })
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: error.message || 'Error al cargar empleados.',
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
