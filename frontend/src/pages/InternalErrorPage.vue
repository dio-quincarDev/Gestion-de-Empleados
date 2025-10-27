<template>
  <div class="fullscreen bg-error text-white text-center q-pa-md flex flex-center">
    <div class="error-container">
      <div class="error-icon q-mb-lg">
        <q-icon name="error_outline" size="30vh" />
      </div>

      <div class="text-h2 q-mb-sm" style="opacity: 0.9">500 - Error del Servidor</div>

      <p class="text-h6 q-mb-lg" style="opacity: 0.7">
        Algo salió mal en nuestro servidor. Estamos trabajando para solucionarlo.
      </p>

      <div class="q-mb-xl">
        <q-linear-progress
          indeterminate
          color="white"
          style="height: 4px; max-width: 300px; margin: 0 auto"
        />
        <div class="text-caption q-mt-sm" style="opacity: 0.6">Intentando reconectar...</div>
      </div>

      <div class="q-gutter-md">
        <q-btn
          color="white"
          text-color="dark"
          unelevated
          @click="reloadPage"
          label="Reintentar"
          no-caps
          icon="refresh"
          size="lg"
        />
        <q-btn
          color="white"
          text-color="dark"
          outline
          to="/"
          label="Ir al Inicio"
          no-caps
          icon="home"
          size="lg"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useQuasar } from 'quasar'

const $q = useQuasar()

const reloadPage = () => {
  $q.loading.show({
    message: 'Reintentando conexión...',
  })

  setTimeout(() => {
    $q.loading.hide()
    window.location.reload()
  }, 1500)
}
</script>

<style scoped>
.bg-error {
  background: linear-gradient(-45deg, #ff6b35, #d63031, #1a1a1a, #000000);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
}

.error-container {
  max-width: 500px;
}

.error-icon {
  opacity: 0.9;
  animation: pulse 2s infinite;
  color: #ffffff;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.9;
  }
  50% {
    transform: scale(1.05);
    opacity: 1;
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
