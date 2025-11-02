export const PaymentType = {
  HOURLY: 'Por Hora',
  SALARIED: 'Asalariado',
};

export const PaymentMethodType = {
  ACH: 'ACH',
  YAPPY: 'Yappy',
  CASH: 'Efectivo',
};

export const BankAccountType = {
  CHECKING: 'Cuenta Corriente',
  SAVINGS: 'Cuenta de Ahorro',
};

export const OvertimeRateType = {
  FIFTY_PERCENT: '50% Adicional',
  ONE_HUNDRED_PERCENT: '100% Adicional',
  FIXED: 'Fijo',
};

export const PAYMENT_TYPE_OPTIONS = Object.keys(PaymentType).map((key) => ({
  label: PaymentType[key],
  value: key,
}));

export const PAYMENT_METHOD_TYPE_OPTIONS = Object.keys(PaymentMethodType).map((key) => ({
  label: PaymentMethodType[key],
  value: key,
}));

export const BANK_ACCOUNT_TYPE_OPTIONS = Object.keys(BankAccountType).map((key) => ({
  label: BankAccountType[key],
  value: key,
}));

export const OVERTIME_RATE_TYPE_OPTIONS = Object.keys(OvertimeRateType).map((key) => ({
  label: OvertimeRateType[key],
  value: key,
}));
