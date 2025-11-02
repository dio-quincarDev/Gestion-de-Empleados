<template>
  <q-page class="q-pa-md bg-dark-page text-white">
    <div class="row items-center q-mb-md">
      <q-btn
        unelevated
        rounded
        icon="arrow_back"
        flat
        dense
        @click="$router.push('/main/employees')"
      />
      <div class="text-h5 q-ml-md">Detalles del Empleado</div>
    </div>

    <div v-if="selectedEmployee" class="q-gutter-md">
      <q-card class="bg-dark glass-effect">
        <q-card-section class="row items-center">
          <div class="col-xs-12 col-sm-4 text-center">
            <q-icon name="person" color="primary" size="150px" />
            <div class="text-h6 q-mt-md">{{ selectedEmployee.name }}</div>
            <div class="text-subtitle2 text-grey-5">{{ EmployeeRole[selectedEmployee.role] }}</div>
            <q-badge :color="selectedEmployee.status === 'ACTIVE' ? 'positive' : 'negative'" class="q-mt-sm">
              {{ selectedEmployee.status }}
            </q-badge>
          </div>

          <div class="col-xs-12 col-sm-8">
            <q-tabs v-model="tab" class="text-grey-5" active-color="primary" indicator-color="primary" align="justify" narrow-indicator>
              <q-tab name="contact" label="Contacto" />
              <q-tab name="payment" label="Pago" />
              <q-tab name="payment_method" label="Método de Pago" />
            </q-tabs>

            <q-separator dark />

            <q-tab-panels v-model="tab" animated class="bg-transparent">
              <q-tab-panel name="contact">
                <q-list dense dark>
                  <q-item>
                    <q-item-section avatar>
                      <q-icon name="email" color="primary" />
                    </q-item-section>
                    <q-item-section>{{ selectedEmployee.email }}</q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section avatar>
                      <q-icon name="phone" color="primary" />
                    </q-item-section>
                    <q-item-section>{{ selectedEmployee.contactPhone }}</q-item-section>
                  </q-item>
                </q-list>
              </q-tab-panel>

              <q-tab-panel name="payment">
                <q-list dense dark>
                  <q-item>
                    <q-item-section avatar>
                      <q-icon name="work" color="primary" />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label>{{ PaymentType[selectedEmployee.paymentType] }}</q-item-label>
                      <q-item-label caption class="text-grey-5" v-if="selectedEmployee.paymentType === 'HOURLY'">Tarifa: ${{ selectedEmployee.hourlyRate.toFixed(2) }}</q-item-label>
                      <q-item-label caption class="text-grey-5" v-if="selectedEmployee.paymentType === 'SALARIED'">Salario: ${{ selectedEmployee.salary.toFixed(2) }}</q-item-label>
                      <q-item-label caption class="text-grey-5" v-if="selectedEmployee.paysOvertime">Paga Horas Extra: {{ OvertimeRateType[selectedEmployee.overtimeRateType] }}</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-tab-panel>

              <q-tab-panel name="payment_method">
                <q-list dense dark>
                  <q-item>
                    <q-item-section avatar>
                      <q-icon name="paid" color="primary" />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label>Método de Pago: {{ PaymentMethodType[selectedEmployee.paymentMethod.type] }}</q-item-label>
                      <q-item-label caption class="text-grey-5" v-if="selectedEmployee.paymentMethod.type === 'ACH'">
                        {{ selectedEmployee.paymentMethod.bankName }} - {{ selectedEmployee.paymentMethod.accountNumber }} ({{ BankAccountType[selectedEmployee.paymentMethod.bankAccountType] }})
                      </q-item-label>
                      <q-item-label caption class="text-grey-5" v-if="selectedEmployee.paymentMethod.type === 'YAPPY'">
                        Tel: {{ selectedEmployee.paymentMethod.phoneNumber }}
                      </q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-tab-panel>
            </q-tab-panels>
          </div>
        </q-card-section>
      </q-card>
    </div>

    <div v-else class="text-center q-pa-md">
      <q-spinner-dots color="primary" size="40px" />
      <div class="q-mt-md">Cargando datos del empleado...</div>
    </div>

  </q-page>
</template>

<script setup>
import { onMounted, computed, ref } from 'vue';
import { useEmployeeStore } from 'src/stores/employee-module';
import { EmployeeRole } from 'src/constants/roles';
import {
  PaymentType,
  PaymentMethodType,
  BankAccountType,
  OvertimeRateType,
} from 'src/constants/payment';

defineOptions({
  name: 'EmployeeDetailPage'
});

const props = defineProps({
  id: {
    type: String,
    required: true
  }
});

const employeeStore = useEmployeeStore();
const tab = ref('contact');

const selectedEmployee = computed(() => employeeStore.getEmployeeById);

onMounted(() => {
  employeeStore.fetchEmployeeById(props.id);
});

</script>

<style lang="scss" scoped>
.glass-effect {
  background: rgba(26, 26, 26, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 15px;
}
</style>

