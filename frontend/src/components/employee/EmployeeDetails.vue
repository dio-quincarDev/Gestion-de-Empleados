<template>
  <q-card v-if="employee" class="employee-details-card" flat bordered dark>
    <q-card-section>
      <div class="text-h6">{{ employee.name }}</div>
      <div class="text-subtitle2">{{ employee.role }}</div>
    </q-card-section>

    <q-separator />

    <q-card-section>
      <q-list dense>
        <q-item>
          <q-item-section avatar>
            <q-icon name="email" />
          </q-item-section>
          <q-item-section>{{ employee.email }}</q-item-section>
        </q-item>
        <q-item>
          <q-item-section avatar>
            <q-icon name="phone" />
          </q-item-section>
          <q-item-section>{{ employee.contactPhone }}</q-item-section>
        </q-item>
        <q-item>
          <q-item-section avatar>
            <q-icon name="work" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ employee.paymentType === 'HOURLY' ? 'Por Hora' : 'Asalariado' }}</q-item-label>
            <q-item-label caption v-if="employee.paymentType === 'HOURLY'">Tarifa: ${{ employee.hourlyRate.toFixed(2) }}</q-item-label>
            <q-item-label caption v-if="employee.paymentType === 'SALARIED'">Salario: ${{ employee.salary.toFixed(2) }}</q-item-label>
          </q-item-section>
        </q-item>
         <q-item>
          <q-item-section avatar>
            <q-icon name="paid" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Método de Pago: {{ employee.paymentMethod.type }}</q-item-label>
             <q-item-label caption v-if="employee.paymentMethod.type === 'ACH'">
              {{ employee.paymentMethod.bankName }} - {{ employee.paymentMethod.accountNumber }}
            </q-item-label>
             <q-item-label caption v-if="employee.paymentMethod.type === 'YAPPY'">
              Tel: {{ employee.paymentMethod.phoneNumber }}
            </q-item-label>
          </q-item-section>
        </q-item>
        <q-item>
          <q-item-section avatar>
            <q-icon name="toggle_on" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Estado: {{ employee.status }}</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </q-card-section>
  </q-card>
  <div v-else class="text-center q-pa-md">
    <q-spinner-dots color="primary" size="40px" />
    <div class="q-mt-md">Cargando datos del empleado...</div>
  </div>
</template>

<script setup>
defineOptions({
  name: 'EmployeeDetails'
});

defineProps({
  employee: {
    type: Object,
    default: null
  }
});
</script>

<style lang="scss" scoped>
.employee-details-card {
  background-color: #1e1e1e;
}
</style>
