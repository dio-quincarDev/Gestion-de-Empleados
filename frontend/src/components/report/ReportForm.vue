<template>
  <q-card class="bg-dark text-white q-pa-md" style="max-width: 450px; width: 90vw">
    <q-card-section>
      <div class="text-h6">Generar Reporte</div>
    </q-card-section>

    <q-separator dark />

    <q-card-section class="q-gutter-md">
      <!-- Búsqueda de empleado -->
      <q-select
        v-model="selectedEmployee"
        :options="employeeOptions"
        label="Seleccionar Empleado"
        option-label="label"
        option-value="value"
        outlined
        dark
        clearable
        use-input
        input-debounce="300"
        @filter="filterFn"
      >
        <template v-slot:no-option>
          <q-item>
            <q-item-section class="text-grey">Sin resultados</q-item-section>
          </q-item>
        </template>
      </q-select>

      <!-- Rango de fechas -->
      <q-input
        v-model="dateRange.from"
        type="date"
        label="Fecha Inicio"
        outlined
        dark
        stack-label
      />
      <q-input
        v-model="dateRange.to"
        type="date"
        label="Fecha Fin"
        outlined
        dark
        stack-label
      />
    </q-card-section>

    <q-separator dark />

    <q-card-actions align="right">
      <!-- Botón actualizar -->
      <q-btn flat label="Actualizar" color="primary" @click="emit('load')" :loading="loading" />

      <!-- Botón descargar PDF -->
      <q-btn flat label="Descargar PDF" color="red" @click="emit('downloadPdf')" :disable="loading" />
    </q-card-actions>
  </q-card>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useEmployeeStore } from 'src/stores/employee-module'
import { date } from 'quasar' // Import Quasar date utility

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

  // Initialize dateRange if not already set
  if (!dateRange.value.from || !dateRange.value.to) {
    dateRange.value = {
      from: date.formatDate(date.subtractFromDate(new Date(), { days: 30 }), 'YYYY-MM-DD'),
      to: date.formatDate(new Date(), 'YYYY-MM-DD'),
    }
  }
})

watch(selectedEmployee, () => emit('load'))
watch(dateRange, () => emit('load'), { deep: true })
</script>
