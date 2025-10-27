<template>
  <q-page class="q-pa-md">
    <div class="row items-center q-mb-md">
      <q-btn
        icon="arrow_back"
        flat
        round
        dense
        @click="$router.push('/main/employees')"
      />
      <div class="text-h5 q-ml-md">Detalles del Empleado</div>
    </div>

    <employee-details :employee="selectedEmployee" />

  </q-page>
</template>

<script setup>
import { onMounted, computed } from 'vue';
import { useEmployeeStore } from 'src/stores/employee-module';
import EmployeeDetails from 'src/components/employee/EmployeeDetails.vue';

defineOptions({
  name: 'EmployeeDetailPage'
});

const props = defineProps({
  id: {
    type: String,
    required: true
  }
});

const employeeStore = useEmployeeStore();

const selectedEmployee = computed(() => employeeStore.getEmployeeById);

onMounted(() => {
  employeeStore.fetchEmployeeById(props.id);
});

</script>
