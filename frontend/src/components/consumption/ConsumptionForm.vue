<template>
  <q-card class="bg-dark text-white q-pa-md" style="max-width: 500px; width: 90vw">
    <q-card-section>
      <div class="text-h6">Registrar Consumo</div>
    </q-card-section>

    <q-card-section>
      <q-form @submit.prevent="onSubmit" class="q-gutter-md">
        <!-- Empleado (solo lectura) -->
        <q-input filled :model-value="employeeName" label="Empleado" readonly dark color="primary">
          <template #prepend>
            <q-icon name="person" />
          </template>
        </q-input>

        <!-- Fecha y hora -->
        <q-input filled v-model="date" type="datetime-local" label="Fecha y Hora" dark required />

        <!-- Descripción -->
        <q-input
          filled
          v-model="description"
          type="textarea"
          label="Descripción"
          dark
          :rules="[(val) => val.length <= 255 || 'Máximo 255 caracteres']"
        />

        <!-- Monto -->
        <q-input
          filled
          v-model.number="amount"
          type="number"
          step="0.01"
          min="0.01"
          label="Monto ($)"
          dark
          required
        />

        <!-- Botones -->
        <div class="row justify-end q-gutter-sm q-mt-md">
          <q-btn flat label="Cancelar" color="grey-5" @click="onCancel" />
          <q-btn
            unelevated
            color="primary"
            label="Registrar"
            type="submit"
            :loading="loading"
            :disable="!isValid"
          />
        </div>
      </q-form>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  employeeId: { type: Number, required: true },
  employeeName: { type: String, required: true },
})

const emit = defineEmits(['save', 'cancel'])

const date = ref('')
const description = ref('')
const amount = ref(null)
const loading = ref(false)

// Validación local
const isValid = computed(() => {
  return date.value && description.value.length <= 255 && amount.value > 0
})

const onSubmit = () => {
  if (!isValid.value) return

  const payload = {
    employeeId: props.employeeId,
    date: date.value + ':00', // ISO 8601 con segundos
    description: description.value,
    amount: parseFloat(amount.value),
  }

  loading.value = true
  emit('save', payload)
  loading.value = false
}

const onCancel = () => {
  date.value = ''
  description.value = ''
  amount.value = null
  emit('cancel')
}
</script>
