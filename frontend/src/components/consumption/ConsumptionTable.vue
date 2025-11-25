<template>
  <q-card class="bg-dark">
    <q-card-section class="bg-dark text-white">
      <div class="row items-center justify-between">
        <div class="text-h6">Consumos de {{ employee?.name || 'N/A' }}</div>
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
        :rows="safeConsumptions"
        :columns="columns"
        row-key="id"
        flat
        dark
        :loading="loading"
        :pagination="{ rowsPerPage: 0 }"
        hide-pagination
      >
        <template #body="props">
          <q-tr :props="props">
            <q-td key="date">
              {{ formatDate(props.row.date) }}
            </q-td>
            <q-td key="description">
              {{ props.row.description || '—' }}
            </q-td>
            <q-td key="amount" class="text-right">
              <strong>{{ formatAmount(props.row.amount) }}</strong>
            </q-td>
            <q-td key="actions" auto-width>
              <q-btn
                flat
                round
                dense
                icon="visibility"
                color="info"
                size="sm"
                @click.stop="onView(props.row)"
              >
                <q-tooltip>Ver</q-tooltip>
              </q-btn>
              <q-btn
                flat
                round
                dense
                icon="edit"
                color="warning"
                size="sm"
                @click.stop="onEdit(props.row)"
              >
                <q-tooltip>Editar</q-tooltip>
              </q-btn>
              <q-btn
                flat
                round
                dense
                icon="delete"
                color="negative"
                size="sm"
                @click.stop="onDelete(props.row.id)"
              >
                <q-tooltip>Eliminar</q-tooltip>
              </q-btn>
            </q-td>
          </q-tr>
        </template>

        <template #no-data>
          <div class="full-width row flex-center text-grey q-pa-lg">
            <q-icon
              :name="loading ? 'hourglass_empty' : 'receipt_long'"
              size="2em"
              class="q-mr-sm"
              :class="{ 'q-spinner': loading }"
            />
            <span>{{ loading ? 'Cargando consumos...' : 'No hay consumos registrados' }}</span>
          </div>
        </template>
      </q-table>
    </q-card-section>

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
import { formatDate, formatTime, formatAmount } from 'src/utils/formatters'

const props = defineProps({
  employee: {
    type: Object,
    required: true,
  },
  consumptions: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['create', 'view', 'edit', 'delete'])

const safeConsumptions = computed(() => props.consumptions || [])

const totalAmount = computed(() => {
  return safeConsumptions.value.reduce((sum, c) => sum + Number(c.amount || 0), 0)
})

const columns = [
  {
    name: 'date',
    label: 'Fecha',
    align: 'left',
    field: (row) => row.date,
    sortable: true,
  },
  {
    name: 'description',
    label: 'Descripción',
    align: 'left',
    field: (row) => row.description,
    sortable: true,
  },
  {
    name: 'amount',
    label: 'Monto',
    align: 'right',
    field: (row) => row.amount,
    sortable: true,
  },
  {
    name: 'actions',
    label: '',
    align: 'right',
  },
]

const onCreate = () => emit('create')
const onView = (row) => emit('view', row)
const onEdit = (row) => emit('edit', row)

const onDelete = (id) => {
  emit('delete', id)
}
</script>
