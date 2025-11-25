<template>
  <q-card class="bg-dark text-white" style="max-width: 500px; width: 90vw">
    <q-card-section>
      <div class="text-h6">Detalles del Consumo</div>
    </q-card-section>

    <q-separator dark />

    <q-card-section class="q-gutter-y-md">
      <div class="row items-start">
        <div class="col-5 text-grey-6">Fecha:</div>
        <div class="col-7">
          <div class="text-weight-medium">{{ formattedDate }}</div>
          <div class="text-caption text-grey-5">{{ formattedTime }}</div>
        </div>
      </div>

      <div class="row items-start">
        <div class="col-5 text-grey-6">Descripción:</div>
        <div class="col-7">
          <div :class="{ 'text-grey-5': !consumption.description }">
            {{ consumption.description || 'Sin descripción' }}
          </div>
        </div>
      </div>

      <div class="row items-center">
        <div class="col-5 text-grey-6">Monto:</div>
        <div class="col-7">
          <div class="text-h6 text-primary">${{ Number(consumption.amount || 0).toFixed(2) }}</div>
        </div>
      </div>

      <div class="row items-center">
        <div class="col-5 text-grey-6">Creado:</div>
        <div class="col-7">
          <div class="text-weight-medium">{{ formattedDate }}</div>
          <div class="text-caption text-grey-5">{{ formattedTime }}</div>
        </div>
      </div>
    </q-card-section>

    <q-separator dark />

    <q-card-actions align="right">
      <q-btn flat rounded label="Cerrar" color="grey-5" @click="onClose" />
      <q-btn flat rounded label="Editar" color="primary" @click="onEdit" />
      <q-btn flat rounded label="Eliminar" color="negative" @click="onDelete" />
    </q-card-actions>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'
import { useQuasar } from 'quasar'
import { formatDate, formatTime } from 'src/utils/formatters'

const $q = useQuasar()

const props = defineProps({
  consumption: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['close', 'edit', 'delete'])

const formattedDate = computed(() => {
  return formatDate(props.consumption.date)
})

const formattedTime = computed(() => {
  return formatTime(props.consumption.date)
})

const onEdit = () => emit('edit', props.consumption)

const onDelete = () => {
  emit('delete', props.consumption.id)
}

const onClose = () => emit('close')
</script>
