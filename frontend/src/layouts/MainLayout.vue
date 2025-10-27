<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="bg-dark-page text-white">
      <q-toolbar>
        <q-btn flat dense round icon="menu" aria-label="Menu" @click="toggleLeftDrawer" />

        <q-toolbar-title class="app-title"> 1800 Gestión </q-toolbar-title>

        <q-btn flat dense round icon="logout" aria-label="Logout" @click="handleLogout" />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered class="bg-dark text-white">
      <q-list>
        <q-item-label header class="text-primary"> Menú Principal </q-item-label>

        <EssentialLink v-for="link in linksList" :key="link.title" v-bind="link" />
      </q-list>
    </q-drawer>

    <q-page-container class="main-page-container">
      <router-view v-slot="{ Component }">
        <transition
          appear
          enter-active-class="animated fadeIn"
          leave-active-class="animated fadeOut"
          mode="out-in"
        >
          <component :is="Component" />
        </transition>
      </router-view>
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

<style lang="scss" scoped>
.q-header {
  background-color: $dark-page; // Usar el color oscuro de la paleta
}

.q-drawer {
  background-color: $dark; // Usar el color oscuro para el drawer
  .q-item-label.header {
    font-weight: bold;
    color: $primary; // Usar el color primario para el encabezado del menú
  }
}

.main-page-container {
  background: linear-gradient(to bottom right, $dark-page 0%, $dark 100%); // Degradado sutil para el fondo
}

.app-title {
  font-size: 1.5rem;
  font-weight: 600;
  @media (max-width: $breakpoint-xs-max) {
    font-size: 1.2rem; // Reducir tamaño en móviles
  }
}

/* Animaciones de Quasar ya están disponibles globalmente */
</style>
