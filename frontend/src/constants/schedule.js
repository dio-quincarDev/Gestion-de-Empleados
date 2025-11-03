// src/constants/schedule.js
export const SCHEDULE_STATUS = {
  PENDING: 'PENDING',
  CONFIRMED: 'CONFIRMED',
  CANCELLED: 'CANCELLED',
}

export const SCHEDULE_TYPE = {
  REGULAR: 'REGULAR',
  OVERTIME: 'OVERTIME',
  HOLIDAY: 'HOLIDAY',
}

export const DAYS_OF_WEEK = [
  { label: 'Lunes', value: 'MONDAY' },
  { label: 'Martes', value: 'TUESDAY' },
  { label: 'Miércoles', value: 'WEDNESDAY' },
  { label: 'Jueves', value: 'THURSDAY' },
  { label: 'Viernes', value: 'FRIDAY' },
  { label: 'Sábado', value: 'SATURDAY' },
  { label: 'Domingo', value: 'SUNDAY' },
]
