export const EmployeeRole = {
  SECURITY: 'Seguridad',
  WAITER: 'Salonero/a',
  CASHIER: 'Cajero/a',
  BARTENDER: 'Bartender',
  CHEF: 'Cocinero/a Principal',
  CHEF_ASSISTANT: 'Ayudante de Cocina',
  STOCKER: 'Bodeguero/a',
  MAINTENANCE: 'Mantenimiento',
  ADMIN: 'Administrador/a',
  MANAGER: 'Gerente',
  HOST: 'Anfitrión/a',
  DJ: 'DJ',
}

export const ROLES = Object.keys(EmployeeRole).map((key) => ({
  label: EmployeeRole[key],
  value: key,
}))
