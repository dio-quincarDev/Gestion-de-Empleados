<template>
  <q-page class="page-container">
    <div class="content-wrapper">
      <!-- Logo desde assets -->
      <div class="logo-container">
        <img src="~assets/1800-pic.jpeg" alt="Logo 1800" class="animated-logo" />
      </div>

      <h1 class="main-title">1800</h1>
      <p class="subtitle">Gestión de Colaboradores</p>
      <q-btn to="/auth/login" label="Ingresar" class="glass-button" flat />
    </div>
  </q-page>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'PresentationPage',
})
</script>

<style lang="scss" scoped>
@import url('https://fonts.googleapis.com/css2?family=Josefin+Sans:wght@300;400;600;700&display=swap');

.page-container {
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  font-family: 'Josefin Sans', sans-serif;
  color: white;
  overflow: hidden;
  min-height: 100vh;
  background: linear-gradient(-45deg, #ff6b35, #ff8e53, #1a1a1a, #000000);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
  position: relative;
}

.page-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 20% 80%, rgba(255, 107, 53, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 142, 83, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(26, 26, 26, 0.2) 0%, transparent 50%);
  pointer-events: none;
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

.content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
  position: relative;
  z-index: 1;
}

.logo-container {
  width: 180px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: float 6s ease-in-out infinite;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  padding: 15px;
}

.animated-logo {
  width: 100%;
  height: 100%;
  object-fit: contain;
  filter: drop-shadow(0 0 15px rgba(255, 142, 83, 0.4));
  border-radius: 15px;
}

.main-title {
  font-size: 5rem;
  font-weight: 700;
  margin: 0;
  letter-spacing: 3px;
  background: linear-gradient(45deg, #ffffff, #ffc3a0, #ffafbd);
  background-size: 200% 200%;
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  animation:
    gradientText 3s ease infinite,
    fadeInUp 1s ease-out 0.5s backwards;
  text-shadow: 0 0 30px rgba(255, 142, 83, 0.3);
}

@keyframes gradientText {
  0%,
  100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.subtitle {
  font-size: 1.3rem;
  font-weight: 300;
  letter-spacing: 2px;
  color: rgba(255, 255, 255, 0.9);
  margin-top: -0.5rem;
  animation: fadeInUp 1s ease-out 1s backwards;
  text-transform: uppercase;
  font-weight: 400;
}

.glass-button {
  margin-top: 2.5rem;
  padding: 14px 45px;
  font-size: 1.1rem;
  letter-spacing: 1.5px;
  color: white;
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 50px;
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  transition: all 0.4s ease;
  animation: fadeInUp 1s ease-out 1.5s backwards;
  position: relative;
  overflow: hidden;
  font-weight: 400;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
    transition: left 0.5s;
  }

  &:hover {
    background: rgba(255, 255, 255, 0.25);
    box-shadow:
      0 0 25px rgba(255, 142, 83, 0.4),
      0 0 50px rgba(255, 107, 53, 0.2);
    transform: translateY(-3px) scale(1.05);

    &::before {
      left: 100%;
    }
  }
}

// Enhanced entry animations
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px) scale(1);
  }
  50% {
    transform: translateY(-10px) scale(1.02);
  }
}

// Responsive adjustments
@media (max-width: 768px) {
  .main-title {
    font-size: 3.5rem;
    letter-spacing: 2px;
  }

  .logo-container {
    width: 140px;
    height: 140px;
  }

  .subtitle {
    font-size: 1.1rem;
    letter-spacing: 1px;
  }

  .glass-button {
    padding: 12px 35px;
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .main-title {
    font-size: 2.8rem;
  }

  .logo-container {
    width: 120px;
    height: 120px;
  }

  .subtitle {
    font-size: 1rem;
  }

  .content-wrapper {
    gap: 1rem;
  }
}
</style>
