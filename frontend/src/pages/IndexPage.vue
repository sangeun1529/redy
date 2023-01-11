<template>
  <q-card bordered flat class="q-ma-sm">
    <q-card-section>
      <div :class="vs.theme.class.main.title">
        <q-icon :name="$t('menu.users.icon')" size="md"/>
        <span v-t="'common.title'"/>
      </div>
      <div :class="vs.theme.class.main.subTitle" v-t="'common.description'"/>
    </q-card-section>
    <q-separator/>
    <q-card-section class="flex flex-center">
      <dashboard-card class="q-ma-sm" title="today's" :value="0"/>
      <dashboard-card class="q-ma-sm" title="yesterday's" :value="200" />
    </q-card-section>
    <q-card-section>
      <apex-line />
    </q-card-section>
    <q-card-section class="flex flex-center">
      <img alt="Quasar logo" src="~assets/quasar-logo-vertical.svg" style="width: 200px; height: 200px"/>
    </q-card-section>
  </q-card>
</template>

<script setup>
import DashboardCard from 'components/DashboardCard'
import vs from 'utils/viewSupport'
import ApexLine from 'components/charts/ApexLine'
</script>
