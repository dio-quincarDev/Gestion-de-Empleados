<template>
  <q-card class="employee-form-card" :style="$q.screen.lt.sm ? 'width: 95vw;' : 'width: 800px; max-width: 90vw;'">
    <q-card-section class="bg-dark text-white">
      <div class="text-h6">{{ props.employee ? 'Editar Empleado' : 'Nuevo Empleado' }}</div>
    </q-card-section>

    <q-separator dark />

    <q-card-section>
      <q-stepper
        v-model="step"
        ref="stepper"
        color="primary"
        animated
        dark
        class="bg-transparent"
      >
        <q-step
          :name="1"
          title="Información Personal"
          icon="person"
          :done="step > 1"
        >
          <q-input v-model="formData.name" label="Nombre Completo" dark outlined color="primary" label-color="grey-5" input-class="text-white" class="q-mb-md" />
          <q-input v-model="formData.email" label="Email" type="email" dark outlined color="primary" label-color="grey-5" input-class="text-white" class="q-mb-md" />
          <q-input v-model="formData.contactPhone" label="Teléfono de Contacto" dark outlined color="primary" label-color="grey-5" input-class="text-white" class="q-mb-md" />
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
        </q-step>

        <q-step
          :name="2"
          title="Información de Pago"
          icon="payment"
          :done="step > 2"
        >
          <q-select
            v-model="formData.paymentType"
            :options="PAYMENT_TYPE_OPTIONS"
            label="Tipo de Pago"
            dark
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
            class="q-mb-md"
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
            class="q-mb-md"
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
            class="q-mb-md"
          />
          <q-toggle v-model="formData.paysOvertime" label="Paga Horas Extra" dark color="primary" class="q-mb-md" />
          <q-select
            v-if="formData.paysOvertime"
            v-model="formData.overtimeRateType"
            :options="OVERTIME_RATE_TYPE_OPTIONS"
            label="Tipo de Tarifa Extra"
            dark
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
          />
        </q-step>

        <q-step
          :name="3"
          title="Método de Pago"
          icon="credit_card"
        >
          <q-select
            v-model="formData.paymentMethod.type"
            :options="PAYMENT_METHOD_TYPE_OPTIONS"
            label="Método"
            dark
            outlined
            color="primary" 
            label-color="grey-5"
            input-class="text-white"
            class="q-mb-md"
            @update:model-value="logPaymentMethod"
          />
          <div v-if="formData.paymentMethod.type === 'ACH'" class="row q-col-gutter-md">
            <q-input v-model="formData.paymentMethod.bankName" label="Nombre del Banco" dark outlined color="primary" label-color="grey-5" input-class="text-white" class="col-xs-12 col-sm-6" />
            <q-input v-model="formData.paymentMethod.accountNumber" label="Número de Cuenta" dark outlined color="primary" label-color="grey-5" input-class="text-white" class="col-xs-12 col-sm-6" />
            <q-select
              v-model="formData.paymentMethod.bankAccountType"
              :options="BANK_ACCOUNT_TYPE_OPTIONS"
              label="Tipo de Cuenta"
              dark
              outlined
              color="primary"
              label-color="grey-5"
              input-class="text-white"
              class="col-xs-12 q-mt-md"
            />
          </div>
          <div v-if="formData.paymentMethod.type === 'YAPPY'" class="q-mt-md">
            <q-input v-model="formData.paymentMethod.phoneNumber" label="Número de Yappy" dark outlined color="primary" label-color="grey-5" input-class="text-white" />
          </div>
        </q-step>

        <template v-slot:navigation>
          <q-stepper-navigation class="row justify-end q-pt-md">
            <q-btn v-if="step > 1" flat color="primary" @click="$refs.stepper.previous()" label="Atrás" class="q-mr-sm" />
            <q-btn unelevated rounded color="primary" @click="step === 3 ? onSave() : $refs.stepper.next()" :label="step === 3 ? 'Guardar' : 'Siguiente'" />
          </q-stepper-navigation>
        </template>
      </q-stepper>
    </q-card-section>

    <q-separator dark />

    <q-card-actions align="right" class="bg-dark">
      <q-btn flat rounded label="Cancelar" color="grey-5" @click="onCancel" />
    </q-card-actions>
  </q-card>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useQuasar } from 'quasar'
import { ROLES } from 'src/constants/roles'
import authService from 'src/service/auth.service'
import {
  PAYMENT_TYPE_OPTIONS,
  PAYMENT_METHOD_TYPE_OPTIONS,
  BANK_ACCOUNT_TYPE_OPTIONS,
  OVERTIME_RATE_TYPE_OPTIONS,
} from 'src/constants/payment'

defineOptions({ name: 'EmployeeForm' })

const props = defineProps({
  employee: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['save', 'cancel'])
const $q = useQuasar()
const step = ref(1)

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
    phoneNumber: '',
  },
})

const formData = ref(initialFormData())

watch(
  () => props.employee,
  (newVal) => {
    if (newVal) {
      formData.value = JSON.parse(JSON.stringify(newVal)) // Deep copy
    } else {
      formData.value = initialFormData()
    }
  },
  { immediate: true },
)

const currentUser = authService.getCurrentUser()

const roleOptions = computed(() => {
  if (!props.employee || !currentUser) return ROLES

  const isEditingSelf = props.employee.id === currentUser.id
  const userIsAdmin = currentUser.roles.includes('ADMIN')

  if (isEditingSelf && userIsAdmin) {
    return ROLES.filter((role) => role.value !== 'MANAGER')
  }

  return ROLES
})

const statusOptions = ['ACTIVE', 'INACTIVE']

const onSave = () => {
  emit('save', formData.value)
}

const onCancel = () => {
  emit('cancel')
}

const logPaymentMethod = (value) => {
  console.log('Payment method type:', value)
  console.log('Show ACH fields:', value === 'ACH')
  console.log('Show Yappy fields:', value === 'YAPPY')
}
</script>

<style lang="scss" scoped>
.employee-form-card {
  background: $dark;
  border-radius: 15px;
}
</style>

