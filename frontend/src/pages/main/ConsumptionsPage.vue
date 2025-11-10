<template>
  <q-page class="q-pa-md">
    <div class="row justify-between items-center q-mb-md">
      <div class="text-h4 text-white page-title">Gestión de Consumos</div>
    </div>

    <q-card class="q-mb-md bg-dark" dark>
      <q-card-section>
        <q-select
          v-model="selectedEmployee"
          :options="employeeOptions"
          label="Busque y seleccione un empleado para ver sus consumos"
          dark
          outlined
          color="primary"
          label-color="grey-5"
          input-class="text-white"
          @filter="filterEmployeeFn"
          use-input
          hide-selected
          fill-input
          option-label="name"
          @update:model-value="onEmployeeSelected"
          clearable
        >
          <template v-slot:prepend>
            <q-icon name="search" />
          </template>
          <template v-slot:no-option>
            <q-item>
              <q-item-section class="text-grey">
                {{ employeeStore.loading ? 'Buscando...' : 'Escriba al menos 2 caracteres' }}
              </q-item-section>
            </q-item>
          </template>
          <template v-slot:selected-item="scope">
            <div class="row items-center">
              <div class="text-weight-medium">{{ scope.opt.name }}</div>
              <div class="text-caption text-grey-5 q-ml-sm">- {{ scope.opt.role }}</div>
            </div>
          </template>
          <template v-slot:option="scope">
            <q-item v-bind="scope.itemProps">
              <q-item-section>
                <q-item-label>{{ scope.opt.name }}</q-item-label>
                <q-item-label caption>{{ scope.opt.role }} - {{ scope.opt.email }}</q-item-label>
              </q-item-section>
            </q-item>
          </template>
        </q-select>
      </q-card-section>
    </q-card>

    <!-- Selector de rango de fechas -->
    <q-card v-if="selectedEmployee" class="q-mb-md bg-dark" dark>
      <q-card-section class="row items-center q-gutter-md">
        <q-input
          v-model="dateRange.startDate"
          type="date"
          label="Fecha inicio"
          dark
          outlined
          dense
          @update:model-value="refreshConsumptions"
        />
        <q-input
          v-model="dateRange.endDate"
          type="date"
          label="Fecha fin"
          dark
          outlined
          dense
          @update:model-value="refreshConsumptions"
        />
      </q-card-section>
    </q-card>

    <consumption-table
      v-if="selectedEmployee"
      :employee="selectedEmployee"
      :consumptions="consumptions"
      :loading="loading"
      @create="showCreateForm"
      @view="showDetails"
      @edit="showEditFormDialog"
      @delete="handleDelete"
    />

    <div v-else class="text-center text-grey q-mt-xl">
      <q-icon name="receipt_long" size="4em" />
      <p class="q-mt-md">Por favor, busque y seleccione un empleado para comenzar.</p>
    </div>

    <!-- Formulario Crear -->
    <q-dialog v-model="showForm">
      <consumption-form
        :employee-id="selectedEmployee?.id"
        @save="handleCreate"
        @cancel="showForm = false"
      />
    </q-dialog>

    <!-- Formulario Editar -->
    <q-dialog v-model="showEditForm">
      <consumption-form
        :consumption="selectedConsumption"
        :employee-id="selectedEmployee?.id"
        @save="handleUpdate"
        @cancel="showEditForm = false"
      />
    </q-dialog>

    <!-- Detalles -->
    <q-dialog v-model="showDetailsModal">
      <consumption-details
        v-if="selectedConsumption"
        :consumption="selectedConsumption"
        @close="showDetailsModal = false"
        @edit="showEditFormFromDetails"
        @delete="handleDeleteFromDetails"
      />
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useConsumptionStore } from 'src/stores/consumption-module'
import { useEmployeeStore } from 'src/stores/employee-module'
import ConsumptionTable from 'src/components/consumption/ConsumptionTable.vue'
import ConsumptionForm from 'src/components/consumption/ConsumptionForm.vue'
import ConsumptionDetails from 'src/components/consumption/ConsumptionDetails.vue'

defineOptions({
  name: 'ConsumptionsPage',
})

const $q = useQuasar()
const consumptionStore = useConsumptionStore()
const employeeStore = useEmployeeStore()

const loading = ref(false)
const selectedEmployee = ref(null)
const employeeOptions = ref([])
const showForm = ref(false)
const showEditForm = ref(false)
const showDetailsModal = ref(false)
const selectedConsumption = ref(null)

const dateRange = ref({
  startDate: new Date(new Date().getFullYear(), 0, 1).toISOString().split('T')[0],
  endDate: new Date().toISOString().split('T')[0],
})

let debounceTimer = null

const consumptions = computed(() => consumptionStore.currentEmployeeConsumptions)

const filterEmployeeFn = async (val, update) => {
  if (debounceTimer) clearTimeout(debounceTimer)

  if (!val || val.length < 2) {
    update(() => {
      employeeOptions.value = []
    })
    return
  }

  debounceTimer = setTimeout(async () => {
    try {
      await employeeStore.searchEmployees({ name: val, status: 'ACTIVE', size: 15 })
      update(() => {
        employeeOptions.value = employeeStore.employees
      })
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: error.message || 'Error al buscar empleados.',
        position: 'top',
      })
      update(() => {
        employeeOptions.value = []
      })
    }
  }, 300)
}

const onEmployeeSelected = async (employee) => {
  if (employee && employee.id) {
    loading.value = true
    try {
      await consumptionStore.loadConsumptionsByEmployee(
        employee.id,
        dateRange.value.startDate,
        dateRange.value.endDate,
      )
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: error.message || 'Error al cargar consumos.',
        position: 'top',
      })
    } finally {
      loading.value = false
    }
  } else {
    consumptionStore.currentEmployeeConsumptions = []
  }
}

const refreshConsumptions = async () => {
  if (selectedEmployee.value) {
    await onEmployeeSelected(selectedEmployee.value)
  }
}

const showCreateForm = () => {
  selectedConsumption.value = null
  showForm.value = true
}

const handleCreate = async (payload) => {
  try {
    await consumptionStore.createConsumption(payload)
    $q.notify({ type: 'positive', message: 'Consumo registrado.', position: 'top' })
    showForm.value = false

    if (selectedEmployee.value) {
      await refreshConsumptions()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.message || 'Error al registrar consumo.',
      position: 'top',
    })
  }
}

const showDetails = (consumption) => {
  selectedConsumption.value = consumption
  showDetailsModal.value = true
}

const showEditFormDialog = (consumption) => {
  selectedConsumption.value = consumption
  showEditForm.value = true
}

const showEditFormFromDetails = () => {
  showDetailsModal.value = false
  showEditForm.value = true
}

const handleUpdate = async (payload) => {
  if (!selectedConsumption.value?.id) return

  try {
    await consumptionStore.updateConsumption(selectedConsumption.value.id, payload)
    $q.notify({ type: 'positive', message: 'Consumo actualizado.', position: 'top' })
    showEditForm.value = false

    if (selectedEmployee.value) {
      await refreshConsumptions()
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.message || 'Error al actualizar consumo.',
      position: 'top',
    })
  }
}

const handleDelete = (consumptionId) => {
  $q.dialog({
    title: 'Confirmar',
    message: '¿Estás seguro de que quieres eliminar este consumo?',
    cancel: true,
    persistent: true,
    dark: true,
  }).onOk(async () => {
    try {
      await consumptionStore.deleteConsumption(consumptionId)
      $q.notify({ type: 'positive', message: 'Consumo eliminado.', position: 'top' })

      if (selectedEmployee.value) {
        await refreshConsumptions()
      }
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: error.message || 'Error al eliminar consumo.',
        position: 'top',
      })
    }
  })
}

const handleDeleteFromDetails = (consumptionId) => {
  showDetailsModal.value = false
  handleDelete(consumptionId)
}

onMounted(() => {
  // Inicializar cualquier dato necesario
})
</script>

<style lang="scss" scoped>
.page-title {
  font-weight: 700;
  @media (max-width: $breakpoint-xs-max) {
    font-size: 1.8rem;
  }
}
</style>
