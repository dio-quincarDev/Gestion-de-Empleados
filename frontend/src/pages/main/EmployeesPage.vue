<template>
  <q-page class="q-pa-md">
    <div class="row justify-between items-center q-mb-md">
      <div class="text-h4 text-white page-title">Gestión de Empleados</div>
      <q-btn
        unelevated
        rounded
        color="primary"
        label="Crear Empleado"
        @click="openCreateForm"
      />
    </div>

    <!-- Search and Filter UI -->
    <q-card class="q-mb-md bg-dark">
      <q-card-section>
        <q-input
          v-model="searchName"
          label="Buscar por Nombre"
          outlined
          color="primary"
          label-color="grey-5"
          input-class="text-white"
          debounce="500"
          clearable
        >
          <template v-slot:prepend>
            <q-icon name="search" />
          </template>
        </q-input>
      </q-card-section>

      <q-expansion-item
        expand-separator
        icon="filter_list"
        label="Filtros Adicionales"
        class="bg-dark"
      >
        <q-card-section class="row q-col-gutter-md">
          <q-select
            v-model="filterRole"
            :options="roleOptions"
            label="Rol"
            emit-value
            map-options
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
            clearable
            class="col-12 col-sm-6"
          />
          <q-select
            v-model="filterStatus"
            :options="statusOptions"
            label="Estado"
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
            clearable
            class="col-12 col-sm-6"
          />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="Limpiar Filtros" @click="resetFilters" color="primary"/>
        </q-card-actions>
      </q-expansion-item>
    </q-card>

    <q-inner-loading :showing="employeeStore.loading">
      <q-spinner-dots size="50px" color="primary" />
    </q-inner-loading>

    <employee-table
      v-if="!employeeStore.loading"
      :employees="employees"
      @edit="handleEdit"
      @delete="handleDelete"
      @promote="handlePromote"
    />

    <q-dialog
      v-model="showFormDialog"
      transition-show="scale"
      transition-hide="scale"
      :full-width="$q.screen.lt.sm"
    >
      <employee-form :employee="editingEmployee" @save="handleSaveEmployee" @cancel="cancelForm" />
    </q-dialog>

    <!-- Password Dialog for Employee Promotion -->
    <q-dialog v-model="showPasswordDialog" persistent>
      <q-card style="min-width: 400px">
        <q-card-section>
          <div class="text-h6">Confirmar Promoción a Administrador</div>
          <div class="q-mt-md">Ingrese su contraseña para confirmar la promoción de <strong>{{ promotionEmployee?.name }}</strong> a administrador.</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-input
            v-model="promotionPassword"
            filled
            type="password"
            label="Contraseña"
            :rules="[val => val && val.length > 0 || 'La contraseña es requerida']"
            @keyup.enter="confirmPromotion"
          />
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancelar" color="primary" v-close-popup />
          <q-btn unelevated label="Promover" color="positive" @click="confirmPromotion" :disable="!promotionPassword" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { onMounted, computed, ref, watch } from 'vue'
import { useEmployeeStore } from 'src/stores/employee-module'
import EmployeeTable from 'src/components/employee/EmployeeTable.vue'
import EmployeeForm from 'src/components/employee/EmployeeForm.vue'
import { useQuasar } from 'quasar'
import { ROLES } from 'src/constants/roles'

defineOptions({
  name: 'EmployeesPage',
})

const $q = useQuasar()
const employeeStore = useEmployeeStore()

// State and Getters
const employees = computed(() => employeeStore.employees)
const pagination = computed(() => employeeStore.pagination)

// Filter and Search State
const searchName = ref('')
const filterRole = ref(null)
const filterStatus = ref(null)
const roleOptions = ROLES
const statusOptions = ['ACTIVE', 'INACTIVE']

// Dialog State
const showFormDialog = ref(false)
const editingEmployee = ref(null)

// Promotion Dialog State
const showPasswordDialog = ref(false)
const promotionEmployee = ref(null)
const promotionPassword = ref('')

// Main data fetching function
async function onRequest() {
  await employeeStore.searchEmployees({
    name: searchName.value,
    role: filterRole.value,
    status: filterStatus.value,
    page: pagination.value.page > 0 ? pagination.value.page - 1 : 0,
    size: pagination.value.rowsPerPage,
  })
}

// Watch for filter changes to trigger a new search
watch([searchName, filterRole, filterStatus], () => {
  // Reset to first page on new filter criteria
  employeeStore.pagination.page = 1
  onRequest()
})

// Initial data load
onMounted(() => {
  onRequest()
})

function resetFilters() {
  searchName.value = ''
  filterRole.value = null
  filterStatus.value = null
  // Watcher will automatically trigger onRequest
}

// Form and Dialog Logic
function openCreateForm() {
  editingEmployee.value = null
  showFormDialog.value = true
}

function handleEdit(employee) {
  editingEmployee.value = { ...employee }
  showFormDialog.value = true
}

function cancelForm() {
  showFormDialog.value = false
  editingEmployee.value = null
}

async function handleSaveEmployee(employeeData) {
  try {
    let message = ''
    if (editingEmployee.value && editingEmployee.value.id) {
      await employeeStore.updateEmployee({ ...employeeData, id: editingEmployee.value.id })
      message = 'Empleado actualizado con éxito.'
    } else {
      await employeeStore.createEmployee(employeeData)
      message = 'Empleado creado con éxito.'
    }
    cancelForm()
    $q.notify({ type: 'positive', message })
    // No need to call onRequest here as create/update actions already refetch.
  } catch (error) {
    console.error('Error saving employee:', error)
    $q.notify({ type: 'negative', message: 'Error al guardar el empleado.' })
  }
}

function handleDelete(employee) {
  $q.dialog({
    title: 'Confirmar',
    message: `¿Estás seguro de que quieres eliminar a ${employee.name}?`,
    cancel: true,
    persistent: true,
  }).onOk(async () => {
    try {
      await employeeStore.deleteEmployee(employee.id)
      $q.notify({
        type: 'positive',
        message: 'Empleado eliminado con éxito.',
      })
      // No need to call onRequest here as delete action already refetches.
    } catch (error) {
      console.error('Error deleting employee:', error)
      $q.notify({
        type: 'negative',
        message: 'Error al eliminar el empleado.',
      })
    }
  })
}

function handlePromote(employee) {
  promotionEmployee.value = employee
  promotionPassword.value = ''
  showPasswordDialog.value = true
}

async function confirmPromotion() {
  try {
    await userService.promoteEmployeeToAdmin(promotionEmployee.value.id, promotionPassword.value)
    $q.notify({
      type: 'positive',
      message: `Empleado ${promotionEmployee.value.name} promovido a administrador exitosamente.`
    })
    showPasswordDialog.value = false
    promotionEmployee.value = null
    promotionPassword.value = ''
    // Refresh the employee list to reflect the role change
    await onRequest()
  } catch (error) {
    console.error('Error promoting employee:', error)
    let message = 'Error al promover el empleado.'
    if (error.response?.status === 400) {
      message = 'Contraseña inválida.'
    } else if (error.response?.status === 403) {
      message = 'No tiene permisos suficientes para realizar esta acción.'
    } else if (error.response?.status === 404) {
      message = 'Empleado no encontrado.'
    } else if (error.message) {
      message = error.message
    }
    $q.notify({
      type: 'negative',
      message: message
    })
  }
}
</script>

<style lang="scss" scoped>
.page-title {
  font-weight: 700;
  @media (max-width: $breakpoint-xs-max) {
    font-size: 1.8rem;
  }
}

.q-btn {
  @media (max-width: $breakpoint-xs-max) {
    width: 100%;
  }
}

.skeleton-container {
  .q-skeleton {
    border-radius: 8px;
  }
}
</style>
