<template>
  <q-table
    title="Empleados"
    :rows="props.employees"
    :columns="columns"
    row-key="id"
    dark
    flat
    class="employee-table"
  >
            <template v-slot:body-cell-actions="props">
              <q-td :props="props">
                <q-btn
                  dense
                  round
                  flat
                  icon="visibility"
                  :to="`/main/employee/${props.row.id}`"
                ></q-btn>
                <q-btn dense round flat icon="edit" @click="onEdit(props.row)"></q-btn>
                <q-btn dense round flat icon="delete" @click="onDelete(props.row)"></q-btn>
              </q-td>
            </template>  </q-table>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

defineOptions({
  name: 'EmployeeTable'
});

const props = defineProps({
  employees: {
    type: Array,
    required: true
  }
});

const emit = defineEmits(['edit', 'delete']);

const columns = [
  { name: 'name', required: true, label: 'Nombre', align: 'left', field: row => row.name, sortable: true },
  { name: 'role', label: 'Rol', align: 'left', field: 'role', sortable: true },
  { name: 'email', label: 'Email', align: 'left', field: 'email', sortable: true },
  { name: 'contactPhone', label: 'Teléfono', align: 'left', field: 'contactPhone' },
  { name: 'status', label: 'Estado', align: 'center', field: 'status', sortable: true },
  { name: 'actions', label: 'Acciones', align: 'center', field: 'id' }
];

const onEdit = (employee) => {
  emit('edit', employee);
};

const onDelete = (employee) => {
  emit('delete', employee);
};
</script>

<style lang="scss" scoped>
.employee-table {
  background-color: #1e1e1e;
  border-radius: 8px;
}
</style>
