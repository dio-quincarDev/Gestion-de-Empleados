<template>
  <q-card
    class="employee-form-card"
    :style="$q.screen.lt.sm ? 'width: 95vw;' : 'width: 800px; max-width: 90vw;'"
  >
    <q-card-section class="bg-dark text-white">
      <div class="text-h6">{{ props.employee ? 'Editar Empleado' : 'Nuevo Empleado' }}</div>
    </q-card-section>

    <q-separator dark />

    <q-card-section>
      <q-stepper v-model="step" ref="stepper" color="primary" animated dark class="bg-transparent">
        <q-step :name="1" title="Información Personal" icon="person" :done="step > 1">
          <q-input
            v-model="formData.name"
            label="Nombre Completo *"
            dark
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
            class="q-mb-md"
            :rules="[(val) => !!val || 'Campo requerido']"
          />
          <q-input
            v-model="formData.email"
            label="Email *"
            type="email"
            dark
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
            class="q-mb-md"
            :rules="[(val) => !!val || 'Campo requerido']"
          />
          <q-input
            v-model="formData.contactPhone"
            label="Teléfono de Contacto *"
            dark
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
            class="q-mb-md"
            :rules="[(val) => !!val || 'Campo requerido']"
          />
          <div class="row q-col-gutter-md">
            <q-select
              v-model="formData.role"
              :options="roleOptions"
              label="Rol *"
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
              label="Estado *"
              dark
              outlined
              color="primary"
              label-color="grey-5"
              input-class="text-white"
              class="col-xs-12 col-sm-6"
            />
          </div>
        </q-step>

        <q-step :name="2" title="Información de Pago" icon="payment" :done="step > 2">
          <q-select
            v-model="formData.paymentType"
            :options="PAYMENT_TYPE_OPTIONS"
            label="Tipo de Pago *"
            emit-value
            map-options
            @update:model-value="onPaymentTypeChange"
            dark
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
            class="q-mb-md"
          />
          <q-input
            v-model.number="formData.hourlyRate"
            label="Tarifa por Hora *"
            type="number"
            :disable="formData.paymentType !== 'HOURLY'"
            dark
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
            class="q-mb-md"
            min="0"
            step="0.01"
            prefix="$"
            :rules="[
              (val) => val > 0 || 'Debe ser mayor a 0',
              (val) => !isNaN(val) || 'Solo números permitidos',
            ]"
          />

          <q-input
            v-model.number="formData.salary"
            label="Salario Mensual *"
            type="number"
            :disable="formData.paymentType !== 'SALARIED'"
            dark
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
            class="q-mb-md"
            min="0"
            step="0.01"
            prefix="$"
            :rules="[
              (val) => val > 0 || 'Debe ser mayor a 0',
              (val) => !isNaN(val) || 'Solo números permitidos',
            ]"
          />
          <q-toggle
            v-model="formData.paysOvertime"
            label="Paga Horas Extra"
            dark
            color="primary"
            class="q-mb-md"
            @update:model-value="onOvertimeChange"
          />
          <q-select
            v-if="formData.paysOvertime"
            v-model="formData.overtimeRateType"
            :options="OVERTIME_RATE_TYPE_OPTIONS"
            label="Tipo de Tarifa Extra *"
            dark
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
          />
        </q-step>

        <q-step :name="3" title="Método de Pago" icon="credit_card">
          <q-select
            v-model="formData.paymentMethod.type"
            :options="PAYMENT_METHOD_TYPE_OPTIONS"
            label="Método *"
            dark
            outlined
            color="primary"
            label-color="grey-5"
            input-class="text-white"
            class="q-mb-md"
            @update:model-value="onPaymentMethodChange"
            :rules="[(val) => !!val || 'Seleccione un método de pago']"
          />

          <!-- ACH Transfer -->
          <div v-if="formData.paymentMethod.type === 'ACH'" class="row q-col-gutter-md">
            <q-input
              v-model="formData.paymentMethod.bankName"
              label="Nombre del Banco *"
              dark
              outlined
              color="primary"
              label-color="grey-5"
              input-class="text-white"
              class="col-xs-12 col-sm-6"
              :rules="[(val) => !!val || 'Campo requerido']"
            />
            <q-input
              v-model="formData.paymentMethod.accountNumber"
              label="Número de Cuenta *"
              dark
              outlined
              color="primary"
              label-color="grey-5"
              input-class="text-white"
              class="col-xs-12 col-sm-6"
              :rules="[
                (val) => !!val || 'Campo requerido',
                (val) => /^\d+$/.test(val) || 'Solo números',
              ]"
              mask="#########"
            />
            <q-select
              v-model="formData.paymentMethod.bankAccountType"
              :options="BANK_ACCOUNT_TYPE_OPTIONS"
              label="Tipo de Cuenta *"
              dark
              outlined
              color="primary"
              label-color="grey-5"
              input-class="text-white"
              class="col-xs-12 q-mt-md"
              :rules="[(val) => !!val || 'Seleccione tipo de cuenta']"
            />
          </div>

          <!-- Yappy -->
          <div v-if="formData.paymentMethod.type === 'YAPPY'">
            <q-input
              v-model="formData.paymentMethod.phoneNumber"
              label="Número de Yappy *"
              dark
              outlined
              color="primary"
              label-color="grey-5"
              input-class="text-white"
              mask="+507 ####-####"
              :rules="[
                (val) => !!val || 'Campo requerido',
                (val) => val.replace(/[^0-9]/g, '').length === 11 || 'Número inválido',
              ]"
            />
          </div>

          <!-- CASH - Mensaje informativo -->
          <div
            v-if="formData.paymentMethod.type === 'CASH'"
            class="text-caption text-grey-5 q-mt-md"
          >
            El pago se realizará en efectivo directamente al empleado.
          </div>
        </q-step>

        <template v-slot:navigation>
          <q-stepper-navigation class="row justify-end q-pt-md">
            <q-btn
              v-if="step > 1"
              flat
              color="primary"
              @click="$refs.stepper.previous()"
              label="Atrás"
              class="q-mr-sm"
            />
            <q-btn
              unelevated
              rounded
              color="primary"
              @click="step === 3 ? onSave() : $refs.stepper.next()"
              :label="step === 3 ? 'Guardar' : 'Siguiente'"
            />
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
  overtimeRateType: null,
  paymentMethod: {
    type: 'CASH',
    bankName: null,
    accountNumber: null,
    bankAccountType: null,
    phoneNumber: null,
  },
})

const formData = ref(initialFormData())

watch(
  () => props.employee,
  (newVal) => {
    if (newVal) {
      const employeeData = JSON.parse(JSON.stringify(newVal))
      const defaults = initialFormData()

      // Manually map flattened backend fields to nested frontend structure
      const mappedPaymentMethod = {
        type: employeeData.paymentMethodType || defaults.paymentMethod.type,
        bankName: employeeData.bankName || defaults.paymentMethod.bankName,
        accountNumber: employeeData.accountNumber || defaults.paymentMethod.accountNumber,
        bankAccountType: employeeData.bankAccountType || defaults.paymentMethod.bankAccountType,
        phoneNumber: employeeData.phoneNumber || defaults.paymentMethod.phoneNumber,
      }

      // Remove the flattened fields from employeeData to avoid conflicts
      delete employeeData.paymentMethodType
      delete employeeData.phoneNumber
      delete employeeData.bankName
      delete employeeData.accountNumber
      delete employeeData.bankAccountType

      formData.value = {
        ...defaults,
        ...employeeData,
        paymentMethod: mappedPaymentMethod,
      }
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

const onPaymentTypeChange = (value) => {
  if (value === 'HOURLY') {
    formData.value.salary = 0
  } else if (value === 'SALARIED') {
    formData.value.hourlyRate = 0
  }
}

const onOvertimeChange = (value) => {
  if (!value) {
    formData.value.overtimeRateType = null
  } else {
    formData.value.overtimeRateType = 'FIFTY_PERCENT'
  }
}

const onPaymentMethodChange = (value) => {
  // Mantener los valores existentes, solo cambiar el tipo
  formData.value.paymentMethod.type = value

  // Solo resetear campos que no correspondan al nuevo tipo
  if (value !== 'ACH') {
    formData.value.paymentMethod.bankName = null
    formData.value.paymentMethod.accountNumber = null
    formData.value.paymentMethod.bankAccountType = null
  }

  if (value !== 'YAPPY') {
    formData.value.paymentMethod.phoneNumber = null
  }

  // Setear valor por defecto solo si es ACH y no tiene valor
  if (value === 'ACH' && !formData.value.paymentMethod.bankAccountType) {
    formData.value.paymentMethod.bankAccountType = 'SAVINGS'
  }
}

const onSave = () => {
  // Crear copia limpia del formData
  const cleanData = JSON.parse(JSON.stringify(formData.value))

  // Limpiar campos según tipo de pago
  if (cleanData.paymentType === 'HOURLY') {
    cleanData.salary = 0
  } else {
    cleanData.hourlyRate = 0
  }

  // Limpiar overtimeRateType si no paga horas extra
  if (!cleanData.paysOvertime) {
    cleanData.overtimeRateType = null
  }

  // Limpiar campos de paymentMethod según tipo
  if (cleanData.paymentMethod.type === 'CASH') {
    cleanData.paymentMethod = { type: 'CASH' }
  } else if (cleanData.paymentMethod.type === 'YAPPY') {
    cleanData.paymentMethod = {
      type: 'YAPPY',
      phoneNumber: cleanData.paymentMethod.phoneNumber,
    }
  } else if (cleanData.paymentMethod.type === 'ACH') {
    cleanData.paymentMethod = {
      type: 'ACH',
      bankName: cleanData.paymentMethod.bankName,
      accountNumber: cleanData.paymentMethod.accountNumber,
      bankAccountType: cleanData.paymentMethod.bankAccountType,
    }
  }

  emit('save', cleanData)
}

const onCancel = () => {
  emit('cancel')
}
</script>

<style lang="scss" scoped>
.employee-form-card {
  background: $dark;
  border-radius: 15px;
}
</style>
