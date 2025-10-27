<template>
  <q-card class="employee-form-card" style="width: 700px; max-width: 80vw;">
    <q-card-section>
      <div class="text-h6">Nuevo Empleado</div>
    </q-card-section>

    <q-separator />

    <q-card-section class="q-gutter-md">
      <q-input v-model="formData.name" label="Nombre Completo" dark outlined />
      <q-input v-model="formData.email" label="Email" type="email" dark outlined />
      <q-input v-model="formData.contactPhone" label="Teléfono de Contacto" dark outlined />

      <div class="row q-gutter-md">
        <q-select
          v-model="formData.role"
          :options="roleOptions"
          label="Rol"
          dark
          outlined
          class="col"
        />
        <q-select
          v-model="formData.status"
          :options="statusOptions"
          label="Estado"
          dark
          outlined
          class="col"
        />
      </div>

      <q-select
        v-model="formData.paymentType"
        :options="paymentTypeOptions"
        label="Tipo de Pago"
        dark
        outlined
      />

      <q-input
        v-if="formData.paymentType === 'HOURLY'"
        v-model.number="formData.hourlyRate"
        label="Tarifa por Hora"
        type="number"
        dark
        outlined
      />
      <q-input
        v-if="formData.paymentType === 'SALARIED'"
        v-model.number="formData.salary"
        label="Salario Mensual"
        type="number"
        dark
        outlined
      />

      <q-toggle v-model="formData.paysOvertime" label="Paga Horas Extra" dark />

      <q-select
        v-if="formData.paysOvertime"
        v-model="formData.overtimeRateType"
        :options="overtimeRateTypeOptions"
        label="Tipo de Tarifa Extra"
        dark
        outlined
      />

      <q-card flat bordered dark class="q-pa-md">
        <div class="text-subtitle1 q-mb-sm">Método de Pago</div>
        <q-select
          v-model="formData.paymentMethod.type"
          :options="paymentMethodTypeOptions"
          label="Método"
          dark
          outlined
        />
        <div v-if="formData.paymentMethod.type === 'ACH'" class="q-mt-md q-gutter-md">
          <q-input v-model="formData.paymentMethod.bankName" label="Nombre del Banco" dark outlined />
          <q-input v-model="formData.paymentMethod.accountNumber" label="Número de Cuenta" dark outlined />
          <q-select
            v-model="formData.paymentMethod.bankAccountType"
            :options="bankAccountTypeOptions"
            label="Tipo de Cuenta"
            dark
            outlined
          />
        </div>
        <div v-if="formData.paymentMethod.type === 'YAPPY'" class="q-mt-md">
          <q-input v-model="formData.paymentMethod.phoneNumber" label="Número de Yappy" dark outlined />
        </div>
      </q-card>

    </q-card-section>

    <q-separator />

    <q-card-actions align="right">
      <q-btn flat label="Cancelar" color="white" @click="onCancel" />
      <q-btn flat label="Guardar" color="primary" @click="onSave" />
    </q-card-actions>
  </q-card>
</template>

<script setup>
import { ref, watch } from 'vue';

defineOptions({ name: 'EmployeeForm' });

const props = defineProps({
  employee: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['save', 'cancel']);

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
  background-color: #1e1e1e;
}
</style>
