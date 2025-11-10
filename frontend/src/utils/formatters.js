import { date } from 'quasar'

export function formatDate(isoString, format = 'DD/MM/YYYY') {
  if (!isoString) return '—'
  return date.formatDate(isoString, format)
}

export function formatTime(isoString, format = 'HH:mm') {
  if (!isoString) return '—'
  return date.formatDate(isoString, format)
}

export function formatDateTime(isoString, format = 'DD/MM/YYYY HH:mm') {
  if (!isoString) return '—'
  return date.formatDate(isoString, format)
}

export function formatAmount(amount) {
  return `$${Number(amount || 0).toFixed(2)}`
}
