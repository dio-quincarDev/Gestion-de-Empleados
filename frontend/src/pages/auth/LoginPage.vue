<template>
  <q-page class="flex flex-center animated-gradient">
    <q-card class="q-pa-md shadow-2 glass-card" style="width: 400px">
      <q-card-section class="text-center">
        <div class="text-h4 text-white q-mb-md josefin-sans">Iniciar Sesión</div>
      </q-card-section>

      <q-card-section>
        <q-form @submit.prevent="handleLogin">
          <q-input
            dark
            dense
            v-model="form.email"
            label="Email"
            type="email"
            lazy-rules
            :rules="[ val => val && val.length > 0 || 'El email es requerido']"
            class="q-mb-sm"
          />

          <q-input
            dark
            dense
            v-model="form.password"
            label="Contraseña"
            :type="isPwd ? 'password' : 'text'"
            lazy-rules
            :rules="[ val => val && val.length > 0 || 'La contraseña es requerida']"
            class="q-mb-md"
          >
            <template v-slot:append>
              <q-icon
                :icon="isPwd ? 'visibility_off' : 'visibility'"
                @click="isPwd = !isPwd"
                class="cursor-pointer"
                color="white"
              />
            </template>
          </q-input>

          <q-btn
            type="submit"
            label="Ingresar"
            class="full-width gradient-button josefin-sans"
            :loading="loading"
          />
        </q-form>
      </q-card-section>

       <q-card-section class="text-center q-pt-none">
        <div class="text-grey-5">
          ¿No tienes una cuenta? 
          <router-link to="/auth/register" class="text-white text-weight-bold" style="text-decoration: none">Regístrate</router-link>
        </div>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue';
import { useQuasar } from 'quasar';
import { useRouter } from 'vue-router';
import authService from 'src/service/auth.service.js';

defineOptions({
  name: 'LoginPage'
});

const $q = useQuasar();
const router = useRouter();

const form = ref({
  email: '',
  password: ''
});

const isPwd = ref(true);

const loading = ref(false);

const handleLogin = async () => {
  loading.value = true;
  try {
    await authService.login(form.value);
    $q.notify({
      color: 'positive',
      position: 'top',
      message: 'Inicio de sesión exitoso.',
      icon: 'check'
    });
    router.push('/main'); // Redirigir a la página principal
  } catch (error) {
    $q.notify({
      color: 'negative',
      position: 'top',
      message: error.response?.data?.message || 'Error en el inicio de sesión.',
      icon: 'report_problem'
    });
  } finally {
    loading.value = false;
  }
};
</script>

<style lang="scss" scoped>
@import url('https://fonts.googleapis.com/css2?family=Josefin+Sans:wght@300;400;700&display=swap');

.josefin-sans {
  font-family: 'Josefin Sans', sans-serif;
}

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

.glass-card {
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.gradient-button {
  background: linear-gradient(to right, #C04000, #FF8C00);
  color: white;
  font-weight: bold;
  border: none;
  transition: transform 0.2s;
}

.gradient-button:hover {
  transform: scale(1.05);
}

:deep(.q-field__label) {
  color: rgba(255, 255, 255, 0.7) !important;
}

:deep(.q-field__native),
:deep(.q-field__input) {
  color: white !important;
}

:deep(.q-field--dark .q-field__control:before) {
  border-color: rgba(255, 255, 255, 0.3) !important;
}
</style>
