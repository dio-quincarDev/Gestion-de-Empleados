<template>
  <div class="row q-mb-md q-gutter-sm items-center">
    <!-- Búsqueda de empleado -->
    <q-select
      v-model="selectedEmployee"
      :options="employeeOptions"
      label="Seleccionar Empleado"
      option-label="label"
      option-value="value"
      filled
      dark
      clearable
      use-input
      input-debounce="300"
      @filter="filterFn"
      style="min-width: 300px"
      class="q-mr-sm"
    >
      <template v-slot:no-option>
        <q-item>
          <q-item-section class="text-grey">Sin resultados</q-item-section>
        </q-item>
      </template>
    </q-select>

    <!-- Rango de fechas -->
    <q-date v-model="dateRange" range minimal dark color="primary" class="q-mx-sm" />

    <!-- Botón actualizar -->
    <q-btn flat round icon="refresh" color="white" @click="emit('load')" :loading="loading" />

    <!-- Botón descargar PDF -->
    <q-btn flat round icon="picture_as_pdf" color="red" @click="emit('downloadPdf')" :disable="loading" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useEmployeeStore } from 'src/stores/employee-module'

defineProps({
  loading: Boolean,
})

const emit = defineEmits(['load', 'downloadPdf'])

const employeeStore = useEmployeeStore()

const selectedEmployee = defineModel('selectedEmployee')
const dateRange = defineModel('dateRange', { required: true })

const employeeOptions = ref([])

const filterFn = async (val, update) => {
  if (val === '') {
    await employeeStore.searchEmployees({})
  } else {
    await employeeStore.searchEmployees({ name: val })
  }

  update(() => {
    employeeOptions.value = employeeStore.employees.map((emp) => ({
      label: emp.name,
      value: emp.id,
    }))
  })
}

onMounted(async () => {
  await employeeStore.searchEmployees({})
  employeeOptions.value = employeeStore.employees.map((emp) => ({
    label: emp.name,
    value: emp.id,
  }))
})

watch(selectedEmployee, () => emit('load'))
watch(dateRange, () => emit('load'), { deep: true })
</script>
