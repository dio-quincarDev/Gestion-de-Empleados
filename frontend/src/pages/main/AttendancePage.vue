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
        option-value="value"
        @filter="filterEmployees"
        style="width: 400px"
        input-class="text-white"
        label-color="grey-5"
      >
        <template v-slot:no-option>
          <q-item>
            <q-item-section class="text-grey"> No se encontraron empleados </q-item-section>
          </q-item>
        </template>
      </q-select>
    </div>

    <!-- Mensaje cuando no hay empleado seleccionado -->
    <div v-if="!selectedEmployee && currentView === 'list'" class="text-center q-pa-xl">
      <q-icon name="person_search" size="4em" color="grey-5" />
      <div class="text-h6 q-mt-md text-grey-5">
        Seleccione un empleado para gestionar asistencias
      </div>
    </div>

    <!-- Vista Principal - Tabla de Asistencias -->
    <div v-else-if="currentView === 'list' && selectedEmployee">
      <attendance-table
        :employee="selectedEmployee"
        @create="showCreateForm"
        @view="showDetails"
        @edit="showEditForm"
        @delete="handleDelete"
      />
    </div>

    <!-- Vista de Crear/Editar Asistencia -->
    <div v-else-if="currentView === 'form'" class="row justify-center">
      <attendance-form
        :employee-id="selectedEmployee.value.value"
        :employee-name="selectedEmployee.value.label"
        @save="handleSaveAttendance"
        @cancel="showListView"
      />
    </div>

    <!-- Vista de Detalles -->
    <div v-else-if="currentView === 'details'" class="row justify-center">
      <attendance-details
        :attendance="selectedAttendance"
        @close="showListView"
        @edit="showEditForm"
        @delete="handleDelete"
      />
    </div>

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

const $q = useQuasar()
const attendanceStore = useAttendanceStore()
const employeeStore = useEmployeeStore()

// Estado de la página
const currentView = ref('list')
const selectedAttendance = ref(null)
const globalLoading = ref(false)
const selectedEmployee = ref(null)

// Computed
const employeeOptions = computed(() => {
  return employeeStore.employees.map((emp) => ({
    label: `${emp.name} - ${emp.role}`,
    value: emp.id,
  }))
})

// Manejo de vistas
const showListView = () => {
  currentView.value = 'list'
  selectedAttendance.value = null
}

const showCreateForm = () => {
  currentView.value = 'form'
  selectedAttendance.value = null
}

const showEditForm = (attendance) => {
  currentView.value = 'form'
  selectedAttendance.value = attendance
}

const showDetails = (attendance) => {
  currentView.value = 'details'
  selectedAttendance.value = attendance
}

// Búsqueda de empleados
const filterEmployees = async (val, update, abort) => {
  globalLoading.value = true // Start loading
  try {
    if (val.length < 2) {
      update(() => {
        // Mostrar empleados recientes o vacío
      })
      return
    }

    await employeeStore.searchEmployees({ name: val })
    // Asegurarse de que employeeStore.employees sea siempre un array
    employeeStore.employees = Array.isArray(employeeStore.employees) ? employeeStore.employees : []
    update(() => {
      // Las opciones se actualizan automáticamente por el computed
    })
  } catch (error) { // Catch error to stop loading
    $q.notify({ type: 'negative', message: error.message || 'Error al buscar empleados' })
    abort()
  } finally {
    globalLoading.value = false // Stop loading
  }
}

// Cargar asistencias cuando se selecciona un empleado
watch(selectedEmployee, async (newEmp) => {
  if (newEmp?.value) {
    globalLoading.value = true
    try {
      const endDate = new Date().toISOString().split('T')[0]
      const startDate = new Date()
      startDate.setDate(startDate.getDate() - 30)
      const startDateStr = startDate.toISOString().split('T')[0]

      await attendanceStore.loadAttendanceList({
        employeeId: newEmp.value.value,
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

// Operaciones CRUD
const handleSaveAttendance = async (attendanceData) => {
  globalLoading.value = true
  try {
    const payload = {
      employeeId: selectedEmployee.value.value,
      entryDateTime: attendanceData.entryDateTime,
      exitDateTime: attendanceData.exitDateTime,
    }

    if (attendanceData.id) {
      await attendanceStore.updateAttendance(attendanceData.id, payload)
    } else {
      await attendanceStore.createAttendance(payload)
    }

    $q.notify({
      type: 'positive',
      message: attendanceData.id ? 'Actualizada' : 'Registrada correctamente',
    })

    showListView()
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
    $q.notify({ type: 'negative', message: error.message || 'Error al guardar' })
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

    // Si estamos en la vista de detalles, volver a la lista
    if (currentView.value === 'details') {
      showListView()
    }
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

<style lang="scss" scoped>
.q-page {
  background: linear-gradient(135deg, $dark 0%, $grey-10 100%);
  min-height: 100vh;
}
</style>
