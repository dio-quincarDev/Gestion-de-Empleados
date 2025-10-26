<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn flat dense round icon="menu" aria-label="Menu" @click="toggleLeftDrawer" />

        <q-toolbar-title> 1800 Gestión </q-toolbar-title>

        <q-btn flat dense round icon="logout" aria-label="Logout" @click="handleLogout" />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item-label header> Menú Principal </q-item-label>

        <EssentialLink v-for="link in linksList" :key="link.title" v-bind="link" />
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import EssentialLink from 'components/EssentialLink.vue'
import authService from 'src/service/auth.service.js'

const router = useRouter()

const linksList = [
  {
    title: 'Dashboard',
    caption: 'Vista principal',
    icon: 'dashboard',
    link: '/main'
  },
  {
    title: 'Empleados',
    caption: 'Gestión de personal',
    icon: 'people',
    link: '/main/employees'
  },
  {
    title: 'Consumos',
    caption: 'Control de consumos internos',
    icon: 'local_bar',
    link: '/main/consumptions'
  },
  {
    title: 'Asistencia',
    caption: 'Registro de asistencia',
    icon: 'how_to_reg',
    link: '/main/attendance'
  },
  {
    title: 'Horarios',
    caption: 'Planificación de turnos',
    icon: 'schedule',
    link: '/main/schedules'
  },
  {
    title: 'Reportes',
    caption: 'Generación de informes',
    icon: 'description',
    link: '/main/reports'
  }
]

const leftDrawerOpen = ref(false)

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value
}

function handleLogout() {
  authService.logout()
  router.push('/auth/login')
}
</script>
