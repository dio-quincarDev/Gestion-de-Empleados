<template>
  <q-item clickable :to="props.link" class="essential-link" active-class="essential-link--active">
    <q-item-section v-if="props.icon" avatar>
      <q-icon :name="props.icon" />
    </q-item-section>

    <q-item-section>
      <q-item-label>{{ props.title }}</q-item-label>
      <q-item-label caption>{{ props.caption }}</q-item-label>
    </q-item-section>
  </q-item>
</template>

<script setup>
const props = defineProps({
  title: {
    type: String,
    required: true,
  },

  caption: {
    type: String,
    default: '',
  },

  link: {
    type: String,
    default: '#',
  },

  icon: {
    type: String,
    default: '',
  },
})
</script>

<style lang="scss" scoped>
.essential-link {
  color: $grey-4; // Color por defecto de texto
  transition: all 0.3s ease;
  border-radius: 8px; // Bordes redondeados
  margin: 4px 8px;

  .q-icon {
    color: $grey-5;
    transition: color 0.3s ease;
  }

  .q-item__label--caption {
    color: $grey-6;
    transition: color 0.3s ease;
  }

  &:hover {
    background-color: rgba($primary, 0.1);
    color: $primary;
    transform: translateX(5px); // Animación de hover

    .q-icon {
      color: $primary;
    }

    .q-item__label--caption {
      color: $primary;
    }
  }

  &--active {
    background-color: $primary; // Color para el elemento activo
    color: white; // Texto blanco para el activo
    font-weight: bold;
    box-shadow: 0 4px 15px rgba($primary, 0.3); // Sombra sutil

    .q-icon {
      color: white;
    }

    .q-item__label--caption {
      color: rgba(255, 255, 255, 0.8);
    }

    &::before {
      content: '';
      position: absolute;
      left: 0; // Indicador visual izquierdo
      top: 50%;
      transform: translateY(-50%);
      width: 4px;
      height: 70%;
      background-color: white;
      border-radius: 0 3px 3px 0;
    }
  }
}
</style>
