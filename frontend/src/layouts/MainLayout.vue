<template>
  <q-layout view="hHh LpR fFf">
    <main-header/>
    <nav-drawer/>
    <q-page-container>
      <q-page class="full-width q-pa-sm">
        <main-breadcrumbs class="flex justify-end q-pa-sm"/>
        <router-view />
      </q-page>
    </q-page-container>
    <main-footer/>
  </q-layout>
</template>

<script setup>
import MainHeader from './MainHeader'
import MainBreadcrumbs from './MainBreadcrumbs'
import NavDrawer from './NavDrawer'
import MainFooter from './MainFooter'
</script>
