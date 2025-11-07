<template>
  <q-page class="q-pa-md">
    <!-- Búsqueda de empleado -->
    <div class="row q-mb-md">
      <q-select
        outlined
        dark
        dense
        autofocus
        clearable
        v-model="selectedEmployee"
        use-input
        hide-selected
        fill-input
        input-debounce="500"
        label="Buscar Empleado por nombre..."
        :options="employeeOptions"
        :loading="isSearching"
        @filter="filterEmployees"
        style="width: 450px"
        bg-color="grey-9"
      >
        <template v-slot:prepend>
          <q-icon name="search" />
        </template>
        <template v-slot:no-option>
          <q-item>
            <q-item-section class="text-grey">
              <span v-if="isSearching">Buscando...</span>
              <span v-else>No se encontraron empleados.</span>
            </q-item-section>
          </q-item>
        </template>
      </q-select>
    </div>

    <!-- Sin empleado seleccionado -->
    <div v-if="!selectedEmployee" class="text-center q-pa-xl">
      <q-icon name="receipt_long" size="4em" color="grey-5" />
      <div class="text-h6 q-mt-md text-grey-5">Seleccione un empleado para gestionar consumos</div>
    </div>

    <!-- Vista principal -->
    <div v-else>
      <consumption-table
        :employee="selectedEmployee"
        :consumptions="filteredConsumptions"
        :total-amount="filteredTotalAmount"
        @create="showCreateForm"
        @view="showDetails"
      />

      <!-- Formulario -->
      <q-dialog v-model="showForm" persistent>
        <consumption-form
          :employee-id="selectedEmployee.value"
          :employee-name="selectedEmployee.label"
          @save="handleSave"
          @cancel="showForm = false"
        />
      </q-dialog>

      <!-- Detalles -->
      <q-dialog v-model="showDetailsModal" persistent>
        <consumption-details
          v-if="selectedConsumption"
          :consumption="selectedConsumption"
          :employee-name="selectedEmployee.label"
          @close="showDetailsModal = false"
        />
      </q-dialog>
    </div>


  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useConsumptionStore } from 'src/stores/consumption-module'
import { useEmployeeStore } from 'src/stores/employee-module'
import { EmployeeRole } from 'src/constants/roles'
import ConsumptionTable from 'src/components/consumption/ConsumptionTable.vue'
import ConsumptionForm from 'src/components/consumption/ConsumptionForm.vue'
import ConsumptionDetails from 'src/components/consumption/ConsumptionDetails.vue'

const $q = useQuasar()
const consumptionStore = useConsumptionStore()
const employeeStore = useEmployeeStore()

// Estado
const selectedEmployee = ref(null)
const showForm = ref(false)
const showDetailsModal = ref(false)
const selectedConsumption = ref(null)
const isSearching = ref(false)

// Cargar todos los consumos al montar la página
onMounted(() => {
  consumptionStore.ensureConsumptionsLoaded()
})

// Opciones de empleados para el select
const employeeOptions = computed(() => {
  return employeeStore.employees.map((emp) => ({
    label: `${emp.name} - ${EmployeeRole[emp.role] || emp.role}`,
    value: emp.id,
  }))
})

// Consumos filtrados por el empleado seleccionado
const filteredConsumptions = computed(() => {
  if (!selectedEmployee.value) {
    return []
  }
  return consumptionStore.consumptions.filter(
    (c) => c.employeeId === selectedEmployee.value.value
  )
})

// Total calculado de los consumos filtrados
const filteredTotalAmount = computed(() => {
  return filteredConsumptions.value.reduce((total, c) => total + c.amount, 0)
})

// Búsqueda dinámica de empleados
const filterEmployees = async (val, update) => {
  if (val.length < 2) {
    update(() => {})
    return
  }

  isSearching.value = true
  try {
    await employeeStore.searchEmployees({ name: val })
    update()
  } catch (error) {
    $q.notify({ type: 'negative', message: error.message || 'Error al buscar empleados' })
  } finally {
    isSearching.value = false
  }
}

// Abrir formulario de creación
const showCreateForm = () => {
  showForm.value = true
}

// Mostrar detalles de un consumo
const showDetails = (consumption) => {
  selectedConsumption.value = consumption
  showDetailsModal.value = true
}

// Guardar un nuevo consumo
const handleSave = async (payload) => {
  try {
    await consumptionStore.createConsumption(payload)
    $q.notify({ type: 'positive', message: 'Consumo registrado' })
    showForm.value = false
    // No es necesario recargar, el store se actualiza y los computeds reaccionan.
  } catch (error) {
    $q.notify({ type: 'negative', message: error.message || 'Error al registrar' })
  }
}
</script>
