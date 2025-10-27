<template>
  <div class="fullscreen bg-warning text-white text-center q-pa-md flex flex-center">
    <div class="error-container">
      <div class="error-shield q-mb-lg">
        <q-icon name="security" size="25vh" />
      </div>

      <div class="text-h2 q-mb-sm" style="opacity: 0.9">403 - Acceso Denegado</div>

      <p class="text-h6 q-mb-lg" style="opacity: 0.7">
        No tienes permisos para acceder a esta página.
      </p>

      <div class="access-info q-mb-xl">
        <q-chip color="white" text-color="dark" icon="info"> Se requiere autenticación </q-chip>
      </div>

      <div class="q-gutter-md">
        <q-btn
          color="white"
          text-color="dark"
          unelevated
          to="/auth/login"
          label="Iniciar Sesión"
          no-caps
          icon="login"
          size="lg"
        />
        <q-btn
          color="white"
          text-color="dark"
          outline
          to="/"
          label="Página Principal"
          no-caps
          icon="home"
          size="lg"
        />
        <q-btn
          color="white"
          text-color="dark"
          flat
          @click="contactSupport"
          label="Soporte"
          no-caps
          icon="support"
          size="lg"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useQuasar } from 'quasar'

const $q = useQuasar()

const contactSupport = () => {
  $q.dialog({
    title: 'Contactar Soporte',
    message: '¿Necesitas ayuda para acceder a esta página?',
    cancel: true,
    persistent: true,
  }).onOk(() => {
    // Lógica para contactar soporte
    $q.notify({
      message: 'Redirigiendo al soporte...',
      color: 'warning',
    })
  })
}
</script>

<style scoped>
.bg-warning {
  background: linear-gradient(-45deg, #ff8e53, #fdcb6e, #1a1a1a, #000000);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
}

.error-container {
  max-width: 500px;
}

.error-shield {
  animation: shieldGlow 3s infinite;
  color: #ffffff;
}

@keyframes shieldGlow {
  0%,
  100% {
    filter: drop-shadow(0 0 20px rgba(255, 255, 255, 0.5));
  }
  50% {
    filter: drop-shadow(0 0 30px rgba(255, 255, 255, 0.8));
  }
}

.access-info {
  animation: fadeIn 1s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes gradientBG {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}
</style>
