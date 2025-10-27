<template>
  <div class="fullscreen bg-primary text-white text-center q-pa-md flex flex-center">
    <div class="error-container">
      <div class="error-code animate-bounce">404</div>

      <div class="text-h2 q-mb-lg" style="opacity: 0.8">¡Ups! Página no encontrada</div>

      <p class="text-h6 q-mb-xl" style="opacity: 0.6">
        La página que buscas parece haberse perdido en el espacio...
      </p>

      <div class="q-gutter-md">
        <q-btn
          color="white"
          text-color="primary"
          unelevated
          to="/"
          label="Volver al Inicio"
          no-caps
          icon="home"
          size="lg"
        />
        <q-btn
          color="white"
          text-color="primary"
          outline
          @click="$router.go(-1)"
          label="Regresar"
          no-caps
          icon="arrow_back"
          size="lg"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
// Lógica específica de la página 404
</script>

<style scoped>
.bg-primary {
  background: linear-gradient(-45deg, #ff6b35, #ff8e53, #1a1a1a, #000000);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
}

.error-container {
  max-width: 500px;
}

.error-code {
  font-size: 25vh;
  font-weight: 900;
  line-height: 1;
  margin-bottom: 2rem;
  background: linear-gradient(45deg, #ffffff, #ffc3a0, #ffafbd);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.animate-bounce {
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%,
  20%,
  50%,
  80%,
  100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-30px);
  }
  60% {
    transform: translateY(-15px);
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
