<template>
  <q-card class="employee-form-card animated fadeIn" :style="$q.screen.lt.sm ? 'width: 95vw;' : 'width: 700px; max-width: 80vw;'">
    <q-card-section class="bg-dark-page text-white">
      <div class="text-h6">{{ props.employee ? 'Editar Empleado' : 'Nuevo Empleado' }}</div>
    </q-card-section>

    <q-separator dark />

    <q-card-section class="q-gutter-md">
      <q-input v-model="formData.name" label="Nombre Completo" dark outlined color="primary" label-color="grey-5" input-class="text-white" />
      <q-input v-model="formData.email" label="Email" type="email" dark outlined color="primary" label-color="grey-5" input-class="text-white" />
      <q-input v-model="formData.contactPhone" label="Teléfono de Contacto" dark outlined color="primary" label-color="grey-5" input-class="text-white" />

      <div class="row q-col-gutter-md">
        <q-select
          v-model="formData.role"
          :options="roleOptions"
          label="Rol"
          dark
          outlined
          color="primary"
          label-color="grey-5"
          input-class="text-white"
          class="col-xs-12 col-sm-6"
        />
        <q-select
          v-model="formData.status"
          :options="statusOptions"
          label="Estado"
          dark
          outlined
          color="primary"
          label-color="grey-5"
          input-class="text-white"
          class="col-xs-12 col-sm-6"
        />
      </div>

      <q-select
        v-model="formData.paymentType"
        :options="paymentTypeOptions"
        label="Tipo de Pago"
        dark
        outlined
        color="primary"
        label-color="grey-5"
        input-class="text-white"
      />

      <q-input
        v-if="formData.paymentType === 'HOURLY'"
        v-model.number="formData.hourlyRate"
        label="Tarifa por Hora"
        type="number"
        dark
        outlined
        color="primary"
        label-color="grey-5"
        input-class="text-white"
        class="animated fadeIn"
      />
      <q-input
        v-if="formData.paymentType === 'SALARIED'"
        v-model.number="formData.salary"
        label="Salario Mensual"
        type="number"
        dark
        outlined
        color="primary"
        label-color="grey-5"
        input-class="text-white"
        class="animated fadeIn"
      />

      <q-toggle v-model="formData.paysOvertime" label="Paga Horas Extra" dark color="primary" class="animated fadeIn" />

      <q-select
        v-if="formData.paysOvertime"
        v-model="formData.overtimeRateType"
        :options="overtimeRateTypeOptions"
        label="Tipo de Tarifa Extra"
        dark
        outlined
        color="primary"
        label-color="grey-5"
        input-class="text-white"
        class="animated fadeIn"
      />

      <q-card flat bordered dark class="q-pa-md glass-card animated fadeIn">
        <div class="text-subtitle1 q-mb-sm text-primary">Método de Pago</div>
        <q-select
          v-model="formData.paymentMethod.type"
          :options="paymentMethodTypeOptions"
          label="Método"
          dark
          outlined
          color="primary"
          label-color="grey-5"
          input-class="text-white"
        />
        <div v-if="formData.paymentMethod.type === 'ACH'" class="q-mt-md q-col-gutter-md row">
          <q-input v-model="formData.paymentMethod.bankName" label="Nombre del Banco" dark outlined color="primary" label-color="grey-5" input-class="text-white" class="col-xs-12 col-sm-6" />
          <q-input v-model="formData.paymentMethod.accountNumber" label="Número de Cuenta" dark outlined color="primary" label-color="grey-5" input-class="text-white" class="col-xs-12 col-sm-6" />
          <q-select
            v-model="formData.paymentMethod.bankAccountType"
            :options="bankAccountTypeOptions"
            label="Tipo de Cuenta"
            dark
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
            class="col-xs-12"
          />
        </div>
        <div v-if="formData.paymentMethod.type === 'YAPPY'" class="q-mt-md animated fadeIn">
          <q-input v-model="formData.paymentMethod.phoneNumber" label="Número de Yappy" dark outlined color="primary" label-color="grey-5" input-class="text-white" />
        </div>
      </q-card>

    </q-card-section>

    <q-separator dark />

    <q-card-actions align="right" class="bg-dark-page">
      <q-btn flat label="Cancelar" color="grey-5" @click="onCancel" />
      <q-btn flat label="Guardar" color="primary" @click="onSave" />
    </q-card-actions>
  </q-card>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useQuasar } from 'quasar'; // Importar useQuasar

defineOptions({ name: 'EmployeeForm' });

const props = defineProps({
  employee: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['save', 'cancel']);
const $q = useQuasar(); // Inyectar useQuasar

const initialFormData = () => ({
  name: '',
  email: '',
  contactPhone: '',
  role: 'WAITER',
  status: 'ACTIVE',
  paymentType: 'HOURLY',
  hourlyRate: 0,
  salary: 0,
  paysOvertime: false,
  overtimeRateType: 'FIFTY_PERCENT',
  paymentMethod: {
    type: 'CASH',
    bankName: '',
    accountNumber: '',
    bankAccountType: 'SAVINGS',
    phoneNumber: ''
  }
});

const formData = ref(initialFormData());

watch(() => props.employee, (newVal) => {
  if (newVal) {
    formData.value = JSON.parse(JSON.stringify(newVal)); // Deep copy
  } else {
    formData.value = initialFormData();
  }
}, { immediate: true });

const roleOptions = ['SECURITY', 'WAITER', 'CASHIER', 'BARTENDER', 'CHEF', 'CHEF_ASSISTANT', 'STOCKER', 'MAINTENANCE', 'ADMIN', 'HOST', 'DJ'];
const statusOptions = ['ACTIVE', 'INACTIVE'];
const paymentTypeOptions = ['HOURLY', 'SALARIED'];
const overtimeRateTypeOptions = ['FIFTY_PERCENT', 'ONE_HUNDRED_PERCENT', 'FIXED'];
const paymentMethodTypeOptions = ['ACH', 'YAPPY', 'CASH'];
const bankAccountTypeOptions = ['CHECKING', 'SAVINGS'];

const onSave = () => {
  emit('save', formData.value);
};

const onCancel = () => {
  emit('cancel');
};
</script>

<style lang="scss" scoped>
.employee-form-card {
  background: rgba(26, 26, 26, 0.8); // Fondo semitransparente oscuro
  backdrop-filter: blur(10px); // Efecto glassmorphism
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.4);
  color: white; // Asegurar que el texto dentro de la tarjeta sea blanco

  .q-card-section {
    color: white;
  }

  .q-field__label {
    color: $grey-5; // Color de las etiquetas de los inputs
  }

  .q-field__control {
    color: white; // Color del texto de los inputs
  }

  .q-toggle__label {
    color: white;
  }
}

.glass-card {
  background: rgba(26, 26, 26, 0.6); // Fondo semitransparente oscuro para tarjetas internas
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 10px;
}

.animated.fadeIn {
  animation-duration: 0.5s;
}
</style>
