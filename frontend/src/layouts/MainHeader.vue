<template>
  <q-header reveal bordered class="bg-primary text-white">
    <q-toolbar>
      <q-btn dense flat round :icon="vs.theme.icon.menu" @click="toggleLeftDrawer"/>

      <q-toolbar-title>
        <q-avatar>
          <img src="~assets/logo-mono-white.svg" alt="logo">
        </q-avatar>
        {{$t('app.title')}}
      </q-toolbar-title>

      <q-btn round dense flat color="white" :icon="vs.getFullscreenIcon($q.fullscreen.isActive)"
             @click="$q.fullscreen.toggle()"
             v-if="$q.screen.gt.sm">
        <q-tooltip>{{vs.getFullscreenLabel($q.fullscreen.isActive)}}</q-tooltip>
      </q-btn>

      <q-btn v-if="authStore.user" flat :label="authStore.user.displayName">
        <q-menu auto-close fit>
          <q-list>
            <q-item clickable>
              <q-item-section @click="showDialog = true">{{$t('common.profile')}}</q-item-section>
            </q-item>
            <q-item clickable>
              <q-item-section @click="logout">{{$t('logout.text')}}</q-item-section>
            </q-item>
          </q-list>
        </q-menu>
      </q-btn>
    </q-toolbar>
  </q-header>
  <update-me v-if="authStore.user" v-model:show="showDialog" :title="authStore.user.displayName" :item="authStore.user" @update="v => authStore.user.displayName = v.displayName" />
</template>

<script setup>
import {useAuthStore} from 'stores/authStore'
import {useAppStore} from 'stores/appStore'
import vs from 'utils/viewSupport'
import UpdateMe from 'pages/settings/UpdateMe'
import {onMounted, ref} from 'vue'

const authStore = useAuthStore()
const appStore = useAppStore()
const showDialog = ref(false)

function logout() {
  authStore.logout()
}

function toggleLeftDrawer() {
  appStore.toggleLeftDrawer()
}

onMounted(async () => {
  await authStore.getMe()
})

</script>
