<template>
  <q-page class="flex column flex-center q-pa-md animated-gradient">
    <div class="dashboard-container">
      <div class="text-h4 text-white q-mb-lg josefin-sans text-center">Bienvenido a la gestión de 1800</div>

      <!-- Sección de Métricas Clave -->
      <div class="q-gutter-md q-mb-xl metrics-grid">
        <q-card class="glass-card metric-card">
          <q-card-section class="text-center">
            <q-icon name="people" size="lg" color="white" />
            <div class="text-h6 text-white josefin-sans">Empleados Activos</div>
            <div class="text-h5 text-orange-6 josefin-sans">--</div>
          </q-card-section>
        </q-card>

        <q-card class="glass-card metric-card">
          <q-card-section class="text-center">
            <q-icon name="local_bar" size="lg" color="white" />
            <div class="text-h6 text-white josefin-sans">Consumos del Día</div>
            <div class="text-h5 text-orange-6 josefin-sans">--</div>
          </q-card-section>
        </q-card>

        <q-card class="glass-card metric-card">
          <q-card-section class="text-center">
            <q-icon name="schedule" size="lg" color="white" />
            <div class="text-h6 text-white josefin-sans">Próximos Turnos</div>
            <div class="text-h5 text-orange-6 josefin-sans">--</div>
          </q-card-section>
        </q-card>
      </div>

      <!-- Sección de Acciones Rápidas -->
      <div class="q-gutter-md actions-grid">
        <q-btn class="gradient-button full-width josefin-sans" label="Registrar Asistencia" icon="how_to_reg" size="lg" />
        <q-btn class="gradient-button full-width josefin-sans" label="Añadir Consumo" icon="add_shopping_cart" size="lg" />
        <q-btn class="gradient-button full-width josefin-sans" label="Ver Empleados" icon="group" size="lg" />
        <q-btn class="gradient-button full-width josefin-sans" label="Planificar Horarios" icon="calendar_today" size="lg" />
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
@import url('https://fonts.googleapis.com/css2?family=Josefin+Sans:wght@300;400;700&display=swap');

.josefin-sans {
  font-family: 'Josefin Sans', sans-serif;
}

.animated-gradient {
  background: linear-gradient(-45deg, #000000, #C04000, #FF8C00, #000000);
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
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 15px;
}

.actions-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 15px;
}

.gradient-button {
  background: linear-gradient(to right, #C04000, #FF8C00);
  color: white;
  font-weight: bold;
  border: none;
  transition: transform 0.2s;
}

.gradient-button:hover {
  transform: scale(1.02);
}

/* Responsive adjustments */
@media (min-width: 600px) {
  .actions-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }
}
</style>