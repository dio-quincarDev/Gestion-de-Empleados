<template>
  <q-page class="q-pa-md">
    <div class="row justify-between items-center q-mb-md">
      <div class="text-h4 text-white page-title">Gestión de Horarios</div>
      <q-btn
        unelevated
        rounded
        color="primary"
        label="Crear Horario"
        @click="openCreateForm"
        :disable="!selectedEmployee"
      />
    </div>

    <!-- Selector de Empleado con Búsqueda Dinámica -->
    <q-card class="q-mb-md bg-dark" dark>
      <q-card-section>
        <q-select
          v-model="selectedEmployee"
          :options="employeeOptions"
          label="Busque y seleccione un empleado para ver sus horarios"
          dark
          outlined
          color="primary"
          label-color="grey-5"
          input-class="text-white"
          @filter="filterEmployeeFn"
          use-input
          hide-selected
          fill-input
          @update:model-value="onEmployeeSelected"
          clearable
          option-value="id"
          option-label="name"
        >
          <template v-slot:prepend>
            <q-icon name="search" />
          </template>
          <template v-slot:no-option>
            <q-item>
              <q-item-section class="text-grey">
                {{
                  employeeStore.loading ? 'Buscando...' : 'No hay resultados. Escriba para buscar.'
                }}
              </q-item-section>
            </q-item>
          </template>
        </q-select>
      </q-card-section>
    </q-card>

    <!-- Tabla de Horarios (condicional) -->
    <schedule-table
      v-if="selectedEmployee"
      :schedules="schedules"
      @view="showDetails"
      @edit="handleEdit"
      @delete="handleDelete"
    />

    <!-- Mensaje cuando no hay empleado seleccionado -->
    <div v-else class="text-center text-grey q-mt-xl">
      <q-icon name="group" size="4em" />
      <p class="q-mt-md">Por favor, busque y seleccione un empleado para comenzar.</p>
    </div>

    <!-- Dialogo para Formulario de Crear/Editar -->
    <q-dialog v-model="showFormDialog" transition-show="scale" transition-hide="scale">
      <schedule-form
        :schedule="editingSchedule"
        :employee-id="selectedEmployee?.id"
        @save="handleSaveSchedule"
        @cancel="cancelForm"
      />
    </q-dialog>

    <!-- Dialogo para Detalles -->
    <q-dialog v-model="showDetailsDialog">
      <schedule-details
        v-if="selectedSchedule"
        :schedule="selectedSchedule"
        @close="showDetailsDialog = false"
        @edit="handleEditFromDetails"
        @delete="handleDeleteFromDetails"
      />
    </q-dialog>

    <q-inner-loading :showing="loading" label="Cargando..." />
  </q-page>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useQuasar } from 'quasar'
import { useScheduleStore } from 'src/stores/schedule-module'
import { useEmployeeStore } from 'src/stores/employee-module'
import ScheduleTable from 'src/components/schedule/ScheduleTable.vue'
import ScheduleForm from 'src/components/schedule/ScheduleForm.vue'
import ScheduleDetails from 'src/components/schedule/ScheduleDetails.vue'

defineOptions({
  name: 'SchedulesPage',
})

const $q = useQuasar()
const scheduleStore = useScheduleStore()
const employeeStore = useEmployeeStore()

// Local State
const loading = ref(false)
const selectedEmployee = ref(null) // Ahora es el objeto completo
const employeeOptions = ref([])
const showFormDialog = ref(false)
const showDetailsDialog = ref(false)
const editingSchedule = ref(null)
const selectedSchedule = ref(null)

// Computed Properties
const schedules = computed(() => scheduleStore.schedules)

// Methods
const filterEmployeeFn = async (val, update) => {
  if (!val) {
    update(() => {
      employeeOptions.value = []
    })
    return
  }

  try {
    await employeeStore.searchEmployees({ name: val, size: 15 })
    update(() => {
      // Guardar los objetos completos para el selector
      employeeOptions.value = employeeStore.employees
    })
  } catch ({ message }) {
    $q.notify({
      type: 'negative',
      message: message || 'Error al buscar empleados.',
      position: 'top',
    })
    update(() => {
      employeeOptions.value = []
    })
  }
}

const onEmployeeSelected = async (employee) => {
  if (employee && employee.id) {
    loading.value = true
    try {
      await scheduleStore.loadSchedulesByEmployee(employee)
    } catch ({ message }) {
      $q.notify({
        type: 'negative',
        message: message || 'Error al cargar los horarios del empleado.',
        position: 'top',
      })
    } finally {
      loading.value = false
    }
  } else {
    scheduleStore.schedules = [] // Limpiar la tabla si se deselecciona el empleado
  }
}

const openCreateForm = () => {
  editingSchedule.value = null
  showFormDialog.value = true
}

const handleEdit = (schedule) => {
  editingSchedule.value = { ...schedule }
  showFormDialog.value = true
}

const cancelForm = () => {
  showFormDialog.value = false
  editingSchedule.value = null
}

const showDetails = (schedule) => {
  selectedSchedule.value = schedule
  showDetailsDialog.value = true
}

const handleEditFromDetails = (schedule) => {
  showDetailsDialog.value = false
  handleEdit(schedule)
}

const handleDeleteFromDetails = (scheduleId) => {
  showDetailsDialog.value = false
  handleDelete(scheduleId)
}

const handleSaveSchedule = async (scheduleData) => {
  const isUpdating = editingSchedule.value && editingSchedule.value.id
  const action = isUpdating
    ? () => scheduleStore.updateSchedule(editingSchedule.value.id, scheduleData)
    : () => scheduleStore.createSchedule(scheduleData)
  const successMessage = isUpdating ? 'Horario actualizado.' : 'Horario creado.'

  try {
    await action()
    cancelForm()
    $q.notify({ type: 'positive', message: successMessage, position: 'top' })
    if (selectedEmployee.value) {
      await scheduleStore.loadSchedulesByEmployee(selectedEmployee.value)
    }
  } catch ({ message }) {
    $q.notify({
      type: 'negative',
      message: message || 'Error al guardar el horario.',
      position: 'top',
    })
  }
}

const handleDelete = (scheduleId) => {
  $q.dialog({
    title: 'Confirmar',
    message: '¿Estás seguro de que quieres eliminar este horario?',
    cancel: true,
    persistent: true,
    dark: true,
  }).onOk(async () => {
    try {
      await scheduleStore.deleteSchedule(scheduleId)
      $q.notify({ type: 'positive', message: 'Horario eliminado.', position: 'top' })
      if (selectedEmployee.value) {
        await scheduleStore.loadSchedulesByEmployee(selectedEmployee.value)
      }
    } catch ({ message }) {
      $q.notify({
        type: 'negative',
        message: message || 'Error al eliminar el horario.',
        position: 'top',
      })
    }
  })
}
</script>

<style lang="scss" scoped>
.page-title {
  font-weight: 700;
  @media (max-width: $breakpoint-xs-max) {
    font-size: 1.8rem;
  }
}
</style>
