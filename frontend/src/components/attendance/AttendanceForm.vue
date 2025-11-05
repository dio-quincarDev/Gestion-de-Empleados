<!-- AttendanceForm.vue -->
<template>
  <q-card class="bg-dark text-white q-pa-md" style="max-width: 500px; width: 90vw">
    <q-card-section>
      <div class="text-h6">Registrar Asistencia</div>
    </q-card-section>

    <q-card-section>
      <q-form @submit.prevent="onSubmit" class="q-gutter-md">

        <!-- Empleado ya seleccionado (readonly) -->
        <q-input
          outlined
          :model-value="employeeName"
          label="Empleado"
          readonly
          dark
          input-class="text-white"
          label-color="grey-5"
        >
          <template #prepend>
            <q-icon name="person" />
          </template>
        </q-input>

        <q-input
          outlined
          v-model="entryDateTime"
          type="datetime-local"
          label="Hora de entrada"
          dark
          required
          input-class="text-white"
          label-color="grey-5"
        />

        <q-input
          outlined
          v-model="exitDateTime"
          type="datetime-local"
          label="Hora de salida"
          dark
          required
          input-class="text-white"
          label-color="grey-5"
        />

        <div class="row justify-end q-gutter-sm q-mt-md">
          <q-btn flat label="Cancelar" color="grey-5" @click="onCancel" />
          <q-btn
            unelevated
            color="primary"
            label="Registrar"
            type="submit"
            :loading="loading"
          />
        </div>
      </q-form>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { ref } from 'vue'
import { useQuasar } from 'quasar'

const props = defineProps({
  employeeId: { type: Number, required: true },
  employeeName: { type: String, required: true }
})

const emit = defineEmits(['save', 'cancel'])
const $q = useQuasar()

const entryDateTime = ref('')
const exitDateTime = ref('')
const loading = ref(false)

const onSubmit = () => {
  if (!entryDateTime.value || !exitDateTime.value) {
    $q.notify({ type: 'negative', message: 'Complete entrada y salida' })
    return
  }

  const payload = {
    employeeId: props.employeeId,
    entryDateTime: entryDateTime.value + ':00', // Asegurar formato ISO
    exitDateTime: exitDateTime.value + ':00'
  }

  loading.value = true
  emit('save', payload)
  loading.value = false
}

const onCancel = () => emit('cancel')
</script>