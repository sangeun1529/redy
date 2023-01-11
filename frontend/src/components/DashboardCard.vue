<template>
  <q-card class="bg-transparent no-shadow no-border">
    <q-card-section class="q-pa-none">
      <div class="row q-col-gutter-sm ">
        <q-item :style="`background-color: ${props.color1}`" class="q-pa-none">
          <q-item-section v-if="props.iconPosition === 'left'" side :style="`background-color: ${props.color2}`"
                          class="q-pa-lg q-mr-none text-white">
            <q-icon :name="props.icon" color="white" size="24px"></q-icon>
          </q-item-section>
          <q-item-section class=" q-pa-md q-ml-none text-white" :style="`width: ${props.width};`">
            <q-item-label class="text-white text-h6 text-weight-bolder"><animated-number :startAmount="0" :endAmount="props.value" :duration="1"/></q-item-label>
            <q-item-label>{{ props.title }}</q-item-label>
          </q-item-section>
          <q-item-section v-if="props.iconPosition === 'right'" side class="q-mr-md text-white">
            <q-icon :name="props.icon" color="white" size="44px"></q-icon>
          </q-item-section>
        </q-item>
      </div>
    </q-card-section>
  </q-card>
</template>

<script setup>
import AnimatedNumber from 'vue3-autocounter' //NOSONAR
import vs from 'utils/viewSupport'
import {getCssVar} from 'quasar'

const props = defineProps(
  {
    color1: {type: String, default: getCssVar('primary')},
    color2: {type: String, default: getCssVar('negative')},
    iconPosition: {type: String, default: 'left'},
    title: {type: String, default: 'default text'},
    value: {type: Number, default: 0},
    icon: {type: String, default: vs.theme.icon.dashboardCard},
    width: {type: String, default: '120px'}
  }
)
</script>
