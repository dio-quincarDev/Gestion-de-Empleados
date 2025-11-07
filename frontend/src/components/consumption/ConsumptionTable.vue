<template>
  <q-card class="bg-dark">
    <q-card-section class="bg-dark text-white">
      <div class="row items-center justify-between">
        <div class="text-h6">Consumos de {{ employee.label }}</div>
        <q-btn
          unelevated
          rounded
          color="primary"
          icon="add"
          label="Nuevo Consumo"
          @click="onCreate"
        />
      </div>
    </q-card-section>

    <q-separator dark />

    <q-card-section class="q-pa-none">
      <q-table
        :rows="consumptions"
        :columns="columns"
        row-key="id"
        flat
        dark
        :loading="loading"
        :pagination="{ rowsPerPage: 0 }"
        hide-pagination
      >
        <template v-slot:body="props">
          <q-tr :props="props">
            <q-td key="date">
              <div>{{ formatDate(props.row.date) }}</div>
              <div class="text-caption text-grey-5">{{ formatTime(props.row.date) }}</div>
            </q-td>
            <q-td key="description">
              {{ props.row.description || '—' }}
            </q-td>
            <q-td key="amount" class="text-right">
              <strong>${{ props.row.amount.toFixed(2) }}</strong>
            </q-td>
            <q-td key="actions" auto-width>
              <q-btn
                flat
                round
                icon="visibility"
                color="info"
                size="sm"
                @click.stop="onView(props.row)"
              >
                <q-tooltip>Ver</q-tooltip>
              </q-btn>
              <!-- Sin edit/delete porque no hay PUT/DELETE en API -->
            </q-td>
          </q-tr>
        </template>

        <template v-slot:no-data>
          <div class="full-width row flex-center text-grey q-pa-lg">
            <q-icon name="receipt_long" size="2em" class="q-mr-sm" />
            <span>No hay consumos registrados</span>
          </div>
        </template>
      </q-table>
    </q-card-section>

    <!-- Total -->
    <q-card-section class="bg-grey-9 text-white">
      <div class="row justify-between">
        <div class="text-subtitle1">Total consumido</div>
        <div class="text-h6">${{ totalAmount.toFixed(2) }}</div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'
import { date } from 'quasar'

const props = defineProps({
  employee: { type: Object, required: true },
  consumptions: { type: Array, required: true },
  totalAmount: { type: Number, required: true },
})

const emit = defineEmits(['create', 'view'])

const loading = computed(() => !props.consumptions) // Ejemplo de carga basada en props

const columns = [
  { name: 'date', label: 'Fecha', align: 'left' },
  { name: 'description', label: 'Descripción', align: 'left' },
  { name: 'amount', label: 'Monto', align: 'right' },
  { name: 'actions', label: '', align: 'right' },
]

const formatDate = (iso) => {
  return date.formatDate(iso, 'DD/MM/YYYY')
}

const formatTime = (iso) => {
  return date.formatDate(iso, 'HH:mm')
}

const onCreate = () => emit('create')
const onView = (row) => emit('view', row)
</script>
