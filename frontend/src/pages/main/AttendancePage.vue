<template>
  <q-page class="q-pa-md">
    <div class="row q-mb-md">
      <q-select
        outlined
        v-model="selectedEmployee"
        use-input
        hide-selected
        fill-input
        input-debounce="500"
        label="Buscar Empleado"
        :options="employeeOptions"
        option-label="label"
        @filter="filterEmployees"
        class="col-12 col-sm-8 col-md-6"
      >
        <template v-slot:no-option>
          <q-item>
            <q-item-section class="text-grey"> No se encontraron empleados </q-item-section>
          </q-item>
        </template>
      </q-select>
    </div>

    <!-- Mensaje cuando no hay empleado seleccionado -->
    <div v-if="!selectedEmployee" class="text-center q-pa-xl">
      <q-icon name="person_search" size="4em" color="grey-5" />
      <div class="text-h6 q-mt-md text-grey-5">
        Seleccione un empleado para gestionar asistencias
      </div>
    </div>

    <!-- Vista Principal - Tabla de Asistencias -->
    <div v-else>
      <attendance-table
        :employee="selectedEmployee"
        @create="showCreateDialog = true"
        @view="handleView"
        @edit="handleEdit"
        @delete="handleDelete"
      />
    </div>

    <!-- Diálogo para Crear Asistencia -->
    <q-dialog v-model="showCreateDialog" persistent :full-width="$q.screen.lt.sm">
      <attendance-form
        :employee-id="selectedEmployee.value"
        :employee-name="selectedEmployee.label"
        @save="handleCreateAttendance"
        @cancel="showCreateDialog = false"
      />
    </q-dialog>

    <!-- Diálogo para Editar Asistencia -->
    <q-dialog v-model="showEditDialog" persistent :full-width="$q.screen.lt.sm">
      <attendance-form
        :employee-id="selectedEmployee.value"
        :employee-name="selectedEmployee.label"
        :edit-data="selectedAttendance"
        @update="handleUpdateAttendance"
        @cancel="showEditDialog = false"
      />
    </q-dialog>

    <!-- Diálogo para Ver Detalles -->
    <q-dialog v-model="showDetailsDialog" persistent :full-width="$q.screen.lt.sm">
      <attendance-details
        :attendance="selectedAttendance"
        @close="showDetailsDialog = false"
        @edit="handleEditFromDetails"
        @delete="handleDelete"
      />
    </q-dialog>

    <!-- Loading Overlay Global -->
    <q-inner-loading :showing="globalLoading" label="Cargando..." label-class="text-white" />
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useQuasar } from 'quasar'
import { useAttendanceStore } from 'src/stores/attendance-module'
import { useEmployeeStore } from 'src/stores/employee-module'
import AttendanceTable from 'src/components/attendance/AttendanceTable.vue'
import AttendanceForm from 'src/components/attendance/AttendanceForm.vue'
import AttendanceDetails from 'src/components/attendance/AttendanceDetails.vue'
import { EmployeeRole } from 'src/constants/roles'

const $q = useQuasar()
const attendanceStore = useAttendanceStore()
const employeeStore = useEmployeeStore()

// Estado de la página
const globalLoading = ref(false)
const selectedEmployee = ref(null)
const selectedAttendance = ref(null)

// Diálogos
const showCreateDialog = ref(false)
const showEditDialog = ref(false)
const showDetailsDialog = ref(false)

// Computed
const employeeOptions = computed(() => {
  return employeeStore.employees.map((emp) => ({
    label: `${emp.name} - ${EmployeeRole[emp.role] || emp.role}`,
    value: emp.id,
  }))
})

// MÉTODOS PARA MANEJAR EVENTOS
const handleView = (attendance) => {
  selectedAttendance.value = attendance
  showDetailsDialog.value = true
}

const handleEdit = (attendance) => {
  selectedAttendance.value = attendance
  showEditDialog.value = true
}

const handleEditFromDetails = () => {
  showDetailsDialog.value = false
  showEditDialog.value = true
}

// Búsqueda de empleados
const filterEmployees = async (val, update, abort) => {
  globalLoading.value = true
  try {
    if (val.length < 2) {
      update(() => {})
      return
    }

    await employeeStore.searchEmployees({ name: val })
    employeeStore.employees = Array.isArray(employeeStore.employees) ? employeeStore.employees : []
    update(() => {})
  } catch (error) {
    $q.notify({ type: 'negative', message: error.message || 'Error al buscar empleados' })
    abort()
  } finally {
    globalLoading.value = false
  }
}

// Cargar asistencias cuando se selecciona un empleado
watch(selectedEmployee, async (newEmp) => {
  if (newEmp) {
    globalLoading.value = true
    try {
      const endDate = new Date().toISOString().split('T')[0]
      const startDate = new Date()
      startDate.setDate(startDate.getDate() - 30)
      const startDateStr = startDate.toISOString().split('T')[0]

      await attendanceStore.loadAttendanceList({
        employeeId: newEmp.value,
        startDate: startDateStr,
        endDate,
      })
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: error.message || 'Error al cargar la asistencia',
        position: 'top',
        timeout: 5000,
      })
    } finally {
      globalLoading.value = false
    }
  } else {
    attendanceStore.attendances = []
  }
})

// MÉTODOS PARA CREAR Y ACTUALIZAR
const handleCreateAttendance = async (attendanceData) => {
  globalLoading.value = true
  try {
    const payload = {
      employeeId: selectedEmployee.value.value,
      entryDateTime: attendanceData.entryDateTime,
      exitDateTime: attendanceData.exitDateTime,
    }

    await attendanceStore.createAttendance(payload)
    $q.notify({
      type: 'positive',
      message: 'Asistencia registrada correctamente',
    })

    showCreateDialog.value = false

    // Recargar lista
    const endDate = new Date().toISOString().split('T')[0]
    const startDate = new Date()
    startDate.setDate(startDate.getDate() - 30)
    await attendanceStore.loadAttendanceList({
      employeeId: selectedEmployee.value.value,
      startDate: startDate.toISOString().split('T')[0],
      endDate,
    })
  } catch (error) {
    $q.notify({ type: 'negative', message: error.message || 'Error al crear asistencia' })
  } finally {
    globalLoading.value = false
  }
}

const handleUpdateAttendance = async (attendanceData) => {
  globalLoading.value = true
  try {
    const payload = {
      employeeId: selectedEmployee.value.value,
      entryDateTime: attendanceData.entryDateTime,
      exitDateTime: attendanceData.exitDateTime,
    }

    await attendanceStore.updateAttendance(attendanceData.id, payload)
    $q.notify({
      type: 'positive',
      message: 'Asistencia actualizada correctamente',
    })

    showEditDialog.value = false

    // Recargar lista
    const endDate = new Date().toISOString().split('T')[0]
    const startDate = new Date()
    startDate.setDate(startDate.getDate() - 30)
    await attendanceStore.loadAttendanceList({
      employeeId: selectedEmployee.value.value,
      startDate: startDate.toISOString().split('T')[0],
      endDate,
    })
  } catch (error) {
    $q.notify({ type: 'negative', message: error.message || 'Error al actualizar asistencia' })
  } finally {
    globalLoading.value = false
  }
}

const handleDelete = async (attendanceId) => {
  globalLoading.value = true
  try {
    await attendanceStore.deleteAttendance(attendanceId)
    $q.notify({
      type: 'positive',
      message: 'Asistencia eliminada correctamente',
      position: 'top',
      timeout: 3000,
    })

    showDetailsDialog.value = false

    // Recargar lista
    const endDate = new Date().toISOString().split('T')[0]
    const startDate = new Date()
    startDate.setDate(startDate.getDate() - 30)
    await attendanceStore.loadAttendanceList({
      employeeId: selectedEmployee.value.value,
      startDate: startDate.toISOString().split('T')[0],
      endDate,
    })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.message || 'Error al eliminar la asistencia',
      position: 'top',
      timeout: 5000,
    })
  } finally {
    globalLoading.value = false
  }
}

onMounted(() => {
  // Vacío: estrategia de búsqueda dinámica
})

watch(
  () => employeeStore.employees,
  (newVal) => {
    console.log('👀 Cambio detectado en employeeStore.employees:', newVal)
  },
  { deep: true },
)
</script>


