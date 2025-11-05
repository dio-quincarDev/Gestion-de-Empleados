<template>
  <q-card class="bg-dark text-white q-pa-md" style="max-width: 500px; width: 90vw">
    <q-card-section>
      <div class="text-h6">Registrar Asistencia</div>
    </q-card-section>

    <q-card-section>
      <q-form @submit.prevent="onSubmit" class="q-gutter-md">
        <q-input
          filled
          v-model="searchQuery"
          label="Buscar empleado"
          debounce="500"
          :loading="searchLoading"
          @update:model-value="onSearch"
          color="primary"
          dark
        >
          <template #append>
            <q-icon name="search" />
          </template>
        </q-input>

        <q-select
          filled
          v-model="selectedEmployee"
          :options="employeeOptions"
          label="Seleccionar empleado"
          option-label="name"
          option-value="id"
          color="primary"
          dark
          use-input
          input-debounce="0"
          behavior="menu"
        />

        <q-input
          filled
          v-model="entryDateTime"
          type="datetime-local"
          label="Hora de entrada"
          color="primary"
          dark
          required
        />

        <q-input
          filled
          v-model="exitDateTime"
          type="datetime-local"
          label="Hora de salida"
          color="primary"
          dark
          required
        />

        <div class="row justify-end q-gutter-sm q-mt-md">
          <q-btn flat label="Cancelar" color="grey-5" @click="onCancel" />
          <q-btn
            unelevated
            color="primary"
            label="Registrar"
            type="submit"
            :loading="loading"
            :disable="!selectedEmployee"
          />
        </div>
      </q-form>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { ref } from 'vue'
import { useQuasar } from 'quasar'
import { employeeService } from 'src/service/employee.service.js'

defineOptions({ name: 'AttendanceForm' })

const emit = defineEmits(['save', 'cancel'])
const $q = useQuasar()

const searchQuery = ref('')
const employeeOptions = ref([])
const selectedEmployee = ref(null)
const entryDateTime = ref('')
const exitDateTime = ref('')
const loading = ref(false)
const searchLoading = ref(false)

const onSearch = async (query) => {
  if (!query) {
    employeeOptions.value = []
    return
  }

  searchLoading.value = true
  try {
    const response = await employeeService.searchEmployees({ name: query, size: 5 })
    employeeOptions.value = response.content || response
  } catch (error) {
    console.error('Error al buscar empleados:', error)
    $q.notify({ type: 'negative', message: 'Error al buscar empleados' })
  } finally {
    searchLoading.value = false
  }
}

const onSubmit = () => {
  if (!selectedEmployee.value) return

  const payload = {
    employeeId: selectedEmployee.value,
    entryDateTime: entryDateTime.value,
    exitDateTime: exitDateTime.value,
  }

  emit('save', payload)
}

const onCancel = () => {
  emit('cancel')
}
</script>

<style scoped>
.q-card {
  border-radius: 15px;
}
</style>
