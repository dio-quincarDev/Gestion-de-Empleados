<!-- AttendanceForm.vue - VERSIÓN CORREGIDA -->
<template>
  <q-card class="bg-dark text-white q-pa-md" style="max-width: 500px; width: 90vw">
    <q-card-section>
      <div class="text-h6">{{ isEditMode ? 'Editar' : 'Registrar' }} Asistencia</div>
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
            :label="isEditMode ? 'Actualizar' : 'Registrar'"
            type="submit"
            :loading="loading"
          />
        </div>
      </q-form>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useQuasar } from 'quasar'

const props = defineProps({
  employeeId: { type: Number, required: true },
  employeeName: { type: String, required: true },
  // NUEVO: Prop para modo edición
  editData: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['save', 'update', 'cancel'])
const $q = useQuasar()

const entryDateTime = ref('')
const exitDateTime = ref('')
const loading = ref(false)

// NUEVO: Computed para saber si estamos en modo edición
const isEditMode = computed(() => props.editData !== null)

// NUEVO: Función para formatear fecha para input datetime-local
const formatDateForInput = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toISOString().slice(0, 16) // Formato: YYYY-MM-DDTHH:mm
}

// NUEVO: Cargar datos cuando hay edición
watch(
  () => props.editData,
  (newEditData) => {
    if (newEditData) {
      entryDateTime.value = formatDateForInput(newEditData.entryDateTime)
      exitDateTime.value = formatDateForInput(newEditData.exitDateTime)
    } else {
      entryDateTime.value = ''
      exitDateTime.value = ''
    }
  },
  { immediate: true },
)

const onSubmit = () => {
  if (!entryDateTime.value || !exitDateTime.value) {
    $q.notify({ type: 'negative', message: 'Complete entrada y salida' })
    return
  }

  const payload = {
    employeeId: props.employeeId,
    entryDateTime: entryDateTime.value + ':00',
    exitDateTime: exitDateTime.value + ':00',
  }

  // NUEVO: En modo edición, agregar el ID
  if (isEditMode.value) {
    payload.id = props.editData.id
  }

  loading.value = true

  // NUEVO: Emitir evento diferente según el modo
  if (isEditMode.value) {
    emit('update', payload)
  } else {
    emit('save', payload)
  }

  loading.value = false
}

const onCancel = () => emit('cancel')
</script>
