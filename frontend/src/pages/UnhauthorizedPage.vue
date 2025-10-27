<template>
  <div class="fullscreen bg-unauthorized text-white text-center q-pa-md flex flex-center">
    <div class="error-container">
      <div class="error-lock q-mb-lg">
        <q-icon name="lock" size="25vh" />
      </div>

      <div class="text-h2 q-mb-sm" style="opacity: 0.9">401 - No Autorizado</div>

      <p class="text-h6 q-mb-lg" style="opacity: 0.7">
        Necesitas autenticarte para acceder a este recurso.
      </p>

      <div class="auth-info q-mb-xl">
        <q-chip color="white" text-color="dark" icon="vpn_key"> Credenciales requeridas </q-chip>
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
          to="/auth/register"
          label="Crear Cuenta"
          no-caps
          icon="person_add"
          size="lg"
        />
        <q-btn
          color="white"
          text-color="dark"
          flat
          @click="forgotPassword"
          label="¿Olvidaste tu contraseña?"
          no-caps
          icon="help"
          size="lg"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useQuasar } from 'quasar'

const $q = useQuasar()

const forgotPassword = () => {
  $q.dialog({
    title: 'Recuperar Contraseña',
    message: '¿Necesitas restablecer tu contraseña?',
    cancel: true,
    persistent: true,
  }).onOk(() => {
    $q.notify({
      message: 'Redirigiendo a recuperación de contraseña...',
      color: 'white',
      textColor: 'dark',
    })
    // Redirigir a página de recuperación
    // $router.push('/auth/forgot-password')
  })
}
</script>

<style scoped>
.bg-unauthorized {
  background: linear-gradient(-45deg, #ff6b35, #74b9ff, #1a1a1a, #000000);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
}

.error-container {
  max-width: 500px;
}

.error-lock {
  animation: lockShake 3s infinite;
  color: #ffffff;
}

@keyframes lockShake {
  0%,
  100% {
    transform: rotate(0deg);
  }
  25% {
    transform: rotate(-5deg);
  }
  75% {
    transform: rotate(5deg);
  }
}

.auth-info {
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
