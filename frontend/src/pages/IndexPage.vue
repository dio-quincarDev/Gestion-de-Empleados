<template>
  <q-page class="flex column flex-center q-pa-md animated-gradient">
    <div class="dashboard-container">
      <div class="text-h4 text-white q-mb-lg text-center">Bienvenido a la gestión de 1800</div>

      <!-- Sección de Métricas Clave -->
      <div class="q-gutter-md q-mb-xl metrics-grid">
        <q-card class="glass-card metric-card animated fadeInUp">
          <q-card-section class="text-center">
            <q-icon name="people" size="lg" color="primary" />
            <div class="text-h6 text-white">Empleados Activos</div>
            <div class="text-h5 text-primary">--</div>
          </q-card-section>
        </q-card>

        <q-card class="glass-card metric-card animated fadeInUp delay-1">
          <q-card-section class="text-center">
            <q-icon name="local_bar" size="lg" color="primary" />
            <div class="text-h6 text-white">Consumos del Día</div>
            <div class="text-h5 text-primary">--</div>
          </q-card-section>
        </q-card>

        <q-card class="glass-card metric-card animated fadeInUp delay-2">
          <q-card-section class="text-center">
            <q-icon name="schedule" size="lg" color="primary" />
            <div class="text-h6 text-white">Próximos Turnos</div>
            <div class="text-h5 text-primary">--</div>
          </q-card-section>
        </q-card>
      </div>

      <!-- Sección de Acciones Rápidas -->
      <div class="q-gutter-md actions-grid">
        <q-btn class="gradient-button full-width animated fadeInUp delay-3" label="Registrar Asistencia" icon="how_to_reg" size="lg" to="/main/attendance" />
        <q-btn class="gradient-button full-width animated fadeInUp delay-4" label="Añadir Consumo" icon="add_shopping_cart" size="lg" to="/main/consumptions" />
        <q-btn class="gradient-button full-width animated fadeInUp delay-5" label="Ver Empleados" icon="group" size="lg" to="/main/employees" />
        <q-btn class="gradient-button full-width animated fadeInUp delay-6" label="Planificar Horarios" icon="calendar_today" size="lg" to="/main/schedules" />
      </div>
    </div>
  </q-page>
</template>

<script setup>
defineOptions({
  name: 'IndexPage'
});
</script>

<style lang="scss" scoped>
.animated-gradient {
  background: linear-gradient(-45deg, $dark-page, $primary, $secondary, $dark-page);
  background-size: 400% 400%;
  animation: gradient 15s ease infinite;
}

@keyframes gradient {
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

.dashboard-container {
  max-width: 800px;
  width: 100%;
  padding: 20px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
}

.metric-card {
  background: rgba($dark, 0.7); // Usar variable SASS
  backdrop-filter: blur(10px);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 15px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba($primary, 0.3);
  }

  .text-h6 {
    font-weight: 600;
  }
}

.actions-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 15px;
}

.gradient-button {
  background: linear-gradient(to right, $primary, $secondary); // Usar variables SASS
  color: white;
  font-weight: bold;
  border: none;
  transition: transform 0.2s;
  border-radius: 8px; // Añadir bordes redondeados

  &:hover {
    transform: scale(1.02);
    box-shadow: 0 4px 15px rgba($primary, 0.4);
  }
}

/* Animaciones de entrada escalonadas */
.animated.fadeInUp {
  animation-duration: 0.6s;
  animation-fill-mode: both;
}
.animated.fadeInUp.delay-1 {
  animation-delay: 0.1s;
}
.animated.fadeInUp.delay-2 {
  animation-delay: 0.2s;
}
.animated.fadeInUp.delay-3 {
  animation-delay: 0.3s;
}
.animated.fadeInUp.delay-4 {
  animation-delay: 0.4s;
}
.animated.fadeInUp.delay-5 {
  animation-delay: 0.5s;
}
.animated.fadeInUp.delay-6 {
  animation-delay: 0.6s;
}

/* Responsive adjustments */
@media (min-width: 600px) {
  .actions-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }
}
</style>