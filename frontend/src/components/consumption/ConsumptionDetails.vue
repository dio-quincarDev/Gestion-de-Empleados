<template>
  <q-card class="bg-dark text-white" style="max-width: 500px; width: 90vw">
    <q-card-section>
      <div class="text-h6">Detalles del Consumo</div>
    </q-card-section>

    <q-separator dark />

    <q-card-section class="q-gutter-y-lg">
      <!-- Empleado -->
      <div class="row items-start">
        <div class="col-5 text-grey-6">Empleado:</div>
        <div class="col-7">
          <div class="text-weight-medium">{{ employeeName }}</div>
          <div class="text-caption text-grey-5">ID: {{ consumption.employeeId }}</div>
        </div>
      </div>

      <!-- Fecha y Hora -->
      <div class="row items-start">
        <div class="col-5 text-grey-6">Fecha:</div>
        <div class="col-7">
          <div class="text-weight-medium">{{ formattedDate }}</div>
          <div class="text-caption text-grey-5">{{ formattedTime }}</div>
        </div>
      </div>

      <!-- Descripción -->
      <div class="row items-start">
        <div class="col-5 text-grey-6">Descripción:</div>
        <div class="col-7">
          <div>{{ consumption.description || '—' }}</div>
        </div>
      </div>

      <!-- Monto -->
      <div class="row items-center">
        <div class="col-5 text-grey-6">Monto:</div>
        <div class="col-7">
          <div class="text-h6 text-primary">${{ consumption.amount.toFixed(2) }}</div>
        </div>
      </div>
    </q-card-section>

    <q-card-actions align="right" class="bg-dark">
      <q-btn flat label="Cerrar" color="grey-5" @click="onClose" />
    </q-card-actions>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'
import { date } from 'quasar'

const props = defineProps({
  consumption: { type: Object, required: true },
  employeeName: { type: String, required: true },
})

const emit = defineEmits(['close'])

const formattedDate = computed(() => {
  return date.formatDate(props.consumption.date, 'DD MMMM YYYY', { locale: 'es-ES' })
})

const formattedTime = computed(() => {
  return date.formatDate(props.consumption.date, 'HH:mm')
})

const onClose = () => emit('close')
</script>
