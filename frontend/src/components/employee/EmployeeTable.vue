<template>
  <q-table
    title="Empleados"
    :rows="props.employees"
    :columns="columns"
    row-key="id"
    dark
    flat
    class="employee-table"
    :grid="$q.screen.xs"
  >
    <template v-slot:item="props">
      <div class="q-pa-xs col-xs-12 col-sm-6 col-md-4 col-lg-3 grid-style-transition">
        <q-card dark bordered flat class="employee-card">
          <q-card-section>
            <div class="text-h6 text-primary">{{ props.row.name }}</div>
            <div class="text-subtitle2">{{ props.row.email }}</div>
          </q-card-section>
          <q-separator dark />
          <q-card-section class="flex justify-between items-center">
            <div>
              <div class="text-caption text-grey-5">Rol: {{ EmployeeRole[props.row.role] }}</div>
              <div class="text-caption text-grey-5">Teléfono: {{ props.row.contactPhone }}</div>
              <div class="text-caption text-grey-5">Estado: {{ props.row.status }}</div>
            </div>
            <div class="q-gutter-xs">
              <q-btn
                unelevated
                rounded
                dense
                icon="visibility"
                :to="`/main/employee/${props.row.id}`"
                color="primary"
              ></q-btn>
              <q-btn
                unelevated
                rounded
                dense
                icon="edit"
                @click="onEdit(props.row)"
                color="secondary"
              ></q-btn>
              <q-btn
                v-if="canDelete(props.row)"
                unelevated
                rounded
                dense
                icon="delete"
                @click="onDelete(props.row)"
                color="negative"
              ></q-btn>
            </div>
          </q-card-section>
        </q-card>
      </div>
    </template>

    <template v-slot:body-cell-actions="props">
      <q-td :props="props">
        <q-btn
          unelevated
          rounded
          dense
          icon="visibility"
          :to="`/main/employee/${props.row.id}`"
          color="primary"
        ></q-btn>
        <q-btn unelevated rounded dense icon="edit" @click="onEdit(props.row)" color="secondary"></q-btn>
        <q-btn
          v-if="canDelete(props.row)"
          unelevated
          rounded
          dense
          icon="delete"
          @click="onDelete(props.row)"
          color="negative"
        ></q-btn>
      </q-td>
    </template>
  </q-table>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import { useQuasar } from 'quasar'
import authService from 'src/service/auth.service'
import { EmployeeRole } from 'src/constants/roles'

defineOptions({
  name: 'EmployeeTable',
})

const props = defineProps({
  employees: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['edit', 'delete'])
const $q = useQuasar()

const columns = [
  {
    name: 'name',
    required: true,
    label: 'Nombre',
    align: 'left',
    field: (row) => row.name,
    sortable: true,
  },
  {
    name: 'role',
    label: 'Rol',
    align: 'left',
    field: 'role',
    format: (val) => EmployeeRole[val],
    sortable: true,
  },
  { name: 'email', label: 'Email', align: 'left', field: 'email', sortable: true },
  { name: 'contactPhone', label: 'Teléfono', align: 'left', field: 'contactPhone' },
  { name: 'status', label: 'Estado', align: 'center', field: 'status', sortable: true },
  { name: 'actions', label: 'Acciones', align: 'center', field: 'id' },
]

const currentUser = authService.getCurrentUser()

const canDelete = (employee) => {
  if (!currentUser) return false

  const userRoles = currentUser.roles
  const targetRole = employee.role

  if (userRoles.includes('MANAGER')) {
    return true
  }

  if (userRoles.includes('ADMIN')) {
    if (targetRole === 'ADMIN' || targetRole === 'MANAGER') {
      return false
    }
    return true
  }

  return false
}

const onEdit = (employee) => {
  emit('edit', employee)
}

const onDelete = (employee) => {
  emit('delete', employee)
}
</script>

<style lang="scss" scoped>
.employee-table {
  background-color: $dark;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);

  .q-table__top,
  .q-table__bottom,
  thead tr:first-child th {
    background-color: $dark-page;
    color: white;
  }

  thead tr th {
    position: sticky;
    z-index: 1;
  }

  thead tr:first-child th {
    top: 0;
  }

  &.q-table--loading thead tr:last-child th {
    top: 48px;
  }

  tbody {
    scroll-margin-top: 48px;
  }
}

.employee-card {
  background: rgba(26, 26, 26, 0.8);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 30px rgba(255, 107, 53, 0.2);
  }

  .text-h6 {
    font-weight: 600;
  }
}

.grid-style-transition {
  transition: transform 0.15s ease-in-out;
}
</style>
