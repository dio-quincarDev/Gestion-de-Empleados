<template>
  <q-table
    title="Empleados"
    :rows="props.employees"
    :columns="columns"
    row-key="id"
    dark
    flat
    class="employee-table animated fadeIn"
    :grid="$q.screen.xs"
    hide-header
  >
    <template v-slot:item="props">
      <div class="q-pa-xs col-xs-12 col-sm-6 col-md-4 col-lg-3 grid-style-transition">
        <q-card dark bordered flat class="employee-card animated fadeInUp">
          <q-card-section>
            <div class="text-h6 text-primary">{{ props.row.name }}</div>
            <div class="text-subtitle2">{{ props.row.email }}</div>
          </q-card-section>
          <q-separator dark />
          <q-card-section class="flex justify-between items-center">
            <div>
              <div class="text-caption text-grey-5">Rol: {{ props.row.role }}</div>
              <div class="text-caption text-grey-5">Teléfono: {{ props.row.contactPhone }}</div>
              <div class="text-caption text-grey-5">Estado: {{ props.row.status }}</div>
            </div>
            <div class="q-gutter-xs">
              <q-btn
                dense
                round
                flat
                icon="visibility"
                :to="`/main/employee/${props.row.id}`"
                color="primary"
              ></q-btn>
              <q-btn dense round flat icon="edit" @click="onEdit(props.row)" color="secondary"></q-btn>
              <q-btn dense round flat icon="delete" @click="onDelete(props.row)" color="negative"></q-btn>
            </div>
          </q-card-section>
        </q-card>
      </div>
    </template>

    <template v-slot:body-cell-actions="props">
      <q-td :props="props">
        <q-btn
          dense
          round
          flat
          icon="visibility"
          :to="`/main/employee/${props.row.id}`"
          color="primary"
        ></q-btn>
        <q-btn dense round flat icon="edit" @click="onEdit(props.row)" color="secondary"></q-btn>
        <q-btn dense round flat icon="delete" @click="onDelete(props.row)" color="negative"></q-btn>
      </q-td>
    </template>
  </q-table>
</template>

<script setup>
import { useQuasar } from 'quasar';

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
const $q = useQuasar(); // Inyectar Quasar para acceder a $q.screen

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
  background-color: $dark; // Usar la variable SASS
  border-radius: 12px;
  overflow: hidden; // Asegurar que el border-radius se aplique correctamente
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);

  .q-table__top,
  .q-table__bottom,
  thead tr:first-child th {
    /* bg color is important for th; just specify one */
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

  /* this is when the loading indicator appears */
  &.q-table--loading thead tr:last-child th {
    /* height of all previous header rows */
    top: 48px;
  }

  /* prevent scrolling behind header */
  tbody {
    /* height of all previous header rows */
    scroll-margin-top: 48px;
  }
}

.employee-card {
  background: rgba(26, 26, 26, 0.8); // Fondo semitransparente oscuro
  backdrop-filter: blur(5px); // Efecto glassmorphism
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(255, 107, 53, 0.2);
  }

  .text-h6 {
    font-weight: 600;
  }
}

.grid-style-transition {
  transition: transform 0.2s ease-in-out;
}

.animated.fadeInUp {
  animation-duration: 0.5s;
}

.animated.fadeIn {
  animation-duration: 0.5s;
}
</style>
