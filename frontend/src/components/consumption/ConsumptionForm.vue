<template>
  <q-card class="bg-dark text-white" style="max-width: 400px; width: 90vw">
    <q-card-section>
      <div class="text-h6">{{ isEditMode ? 'Editar Consumo' : 'Nuevo Consumo' }}</div>
    </q-card-section>

    <q-separator dark />

    <q-card-section>
      <q-form @submit.prevent="onSubmit" class="q-gutter-md">
        <q-input
          outlined
          v-model="formData.date"
          type="datetime-local"
          label="Fecha y Hora *"
          dark
          :rules="[(val) => !!val || 'Campo requerido']"
        />

        <q-input
          outlined
          v-model="formData.description"
          type="textarea"
          label="Descripción"
          dark
          :rules="[(val) => !val || val.length <= 255 || 'Máximo 255 caracteres']"
        />

        <q-input
          outlined
          v-model.number="formData.amount"
          type="number"
          step="0.01"
          min="0.01"
          label="Monto ($) *"
          dark
          :rules="[(val) => val > 0 || 'El monto debe ser mayor a 0']"
        />

        <q-card-actions align="right">
          <q-btn flat label="Cancelar" color="grey-5" @click="onCancel" />
          <q-btn
            unelevated
            color="primary"
            :label="isEditMode ? 'Guardar' : 'Crear'"
            type="submit"
            :disable="!isValid"
          />
        </q-card-actions>
      </q-form>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  consumption: {
    type: Object,
    default: null,
  },
  employeeId: {
    type: Number,
    required: true,
  },
})

const emit = defineEmits(['save', 'cancel'])

const isEditMode = computed(() => !!props.consumption)

const formData = ref({
  date: '',
  description: '',
  amount: 0,
})

// Agregar esta función helper
const toLocalISOString = (date) => {
  const offset = date.getTimezoneOffset() * 60000
  const localISOTime = new Date(date - offset).toISOString()
  return localISOTime.slice(0, 16)
}

// Actualizar el watch
watch(
  () => props.consumption,
  (newVal) => {
    if (newVal) {
      formData.value = {
        date: toLocalISOString(new Date(newVal.date)),
        description: newVal.description || '',
        amount: Number(newVal.amount || 0),
      }
    } else {
      formData.value = {
        date: toLocalISOString(new Date()),
        description: '',
        amount: 0,
      }
    }
  },
  { immediate: true },
)

const isValid = computed(() => {
  return (
    formData.value.date &&
    formData.value.amount > 0 &&
    (!formData.value.description || formData.value.description.length <= 255)
  )
})

const formatDateForBackend = (dateString) => {
  const date = new Date(dateString)
  return date.toISOString().slice(0, 19) // "2025-10-26T21:10:00"
}

const onSubmit = () => {
  if (!isValid.value) return

  const payload = {
    employeeId: props.employeeId,
    date: formatDateForBackend(formData.value.date),
    description: formData.value.description || null,
    amount: parseFloat(formData.value.amount),
  }

  emit('save', payload)
}
const onCancel = () => emit('cancel')
</script>
