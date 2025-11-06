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
                <div class="text-caption text-grey-5">{{ formattedRole }}</div>
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
import { EmployeeRole } from 'src/constants/roles.js'

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
  return employeeStore.employees.find(e => e.id === props.attendance.employeeId) || {}
})

const formattedRole = computed(() => {
  if (!employee.value?.role) return ''
  return EmployeeRole[employee.value.role] || employee.value.role
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

</script>

<style lang="scss" scoped>
.attendance-details-card {
  background: $dark;
  border-radius: 15px;
}
</style>
