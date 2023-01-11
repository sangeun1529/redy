<template>
  <q-breadcrumbs>
    <q-breadcrumbs-el v-for="path in paths" :key="path.path" :label="path.text" :to="path.to.path" :disable="path.disable === undefined ? true : path.disable"></q-breadcrumbs-el>
  </q-breadcrumbs>
</template>

<script setup>
import {ref, watch} from 'vue'
import {useRoute} from 'vue-router'
import {useI18n} from 'vue-i18n'

const {t} = useI18n()
const route = useRoute()
const paths = ref(route.meta.breadcrumb)

watch(
  () => route.meta.breadcrumb,
  br => {
    paths.value = br
  }
)
</script>
