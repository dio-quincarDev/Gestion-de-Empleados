<template>
  <q-page class="q-pa-md animated fadeIn">
    <div class="row justify-between items-center q-mb-md">
      <div class="text-h4 text-white page-title">Gestión de Empleados</div>
      <q-btn color="primary" label="Crear Empleado" @click="openCreateForm" class="q-mt-sm-md" />
    </div>

    <employee-table
      :employees="employees"
      @edit="handleEdit"
      @delete="handleDelete"
    />

    <q-dialog v-model="showFormDialog" transition-show="scale" transition-hide="scale">
      <employee-form
        :employee="editingEmployee"
        @save="handleSaveEmployee"
        @cancel="cancelForm"
      />
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useEmployeeStore } from 'src/stores/employee-module';
import EmployeeTable from 'src/components/employee/EmployeeTable.vue';
import EmployeeForm from 'src/components/employee/EmployeeForm.vue';
import { useQuasar } from 'quasar';

defineOptions({
  name: 'EmployeesPage'
});

const $q = useQuasar();
const employeeStore = useEmployeeStore();

const employees = computed(() => employeeStore.getAllEmployees);

onMounted(() => {
  employeeStore.fetchEmployees();
});

const showFormDialog = ref(false);
const editingEmployee = ref(null);

function openCreateForm() {
  editingEmployee.value = null;
  showFormDialog.value = true;
}

function handleEdit(employee) {
  editingEmployee.value = { ...employee }; // Copia para evitar mutación directa
  showFormDialog.value = true;
}

function cancelForm() {
  showFormDialog.value = false;
  editingEmployee.value = null;
}

async function handleSaveEmployee(employeeData) {
  try {
    let message = '';
    if (editingEmployee.value && editingEmployee.value.id) {
      await employeeStore.updateEmployee({ ...employeeData, id: editingEmployee.value.id });
      message = 'Empleado actualizado con éxito.';
    } else {
      await employeeStore.createEmployee(employeeData);
      message = 'Empleado creado con éxito.';
    }
    cancelForm();
    $q.notify({ type: 'positive', message });
  } catch (error) {
    console.error('Error saving employee:', error);
    $q.notify({ type: 'negative', message: 'Error al guardar el empleado.' });
  }
}

function handleDelete(employee) {
  $q.dialog({
    title: 'Confirmar',
    message: `¿Estás seguro de que quieres eliminar a ${employee.name}?`,
    cancel: true,
    persistent: true,
    dark: true,
  }).onOk(async () => {
    try {
      await employeeStore.deleteEmployee(employee.id);
      $q.notify({
        type: 'positive',
        message: 'Empleado eliminado con éxito.'
      });
    } catch (error) {
      console.error('Error deleting employee:', error);
      $q.notify({
        type: 'negative',
        message: 'Error al eliminar el empleado.'
      });
    }
  });
}
</script>

<style lang="scss" scoped>
.page-title {
  font-weight: 700;
  @media (max-width: $breakpoint-xs-max) {
    font-size: 1.8rem; // Ajustar tamaño en móviles
  }
}

.q-btn {
  // Asegurar que el botón se adapte bien en móviles
  @media (max-width: $breakpoint-xs-max) {
    width: 100%;
  }
}
</style>