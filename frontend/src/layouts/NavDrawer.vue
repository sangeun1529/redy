<template>
  <q-drawer show-if-above v-model="leftDrawerOpen" side="left" bordered :width="250">
    <q-list>
      <q-item dense clickable v-ripple to="/">
        <q-item-section avatar>
          <q-icon :name="$t('menu.home.icon')"></q-icon>
        </q-item-section>
        <q-item-section> {{ $t('menu.home.text') }}</q-item-section>
      </q-item>

      <q-item dense clickable v-ripple v-for="item in authorizedUser" :key="item.text" :to="item.routerPath">
        <q-item-section avatar>
          <q-icon :name="item.icon"></q-icon>
        </q-item-section>
        <q-item-section> {{ item.text }}</q-item-section>
      </q-item>

      <q-expansion-item dense :label="$t('menu.systemSettings.text')" :icon="$t('menu.systemSettings.icon')">
        <q-item dense clickable v-ripple v-for="item in authorizedSettings" :key="item.text" :to="item.routerPath"
                class="q-ml-md">
          <q-item-section avatar>
            <q-icon :name="item.icon"></q-icon>
          </q-item-section>
          <q-item-section> {{ item.text }}</q-item-section>
        </q-item>
      </q-expansion-item>
    </q-list>
  </q-drawer>
</template>

<script>
import {mapWritableState, mapState, mapActions} from 'pinia'
import {useAppStore} from 'stores/appStore'
import {useAuthStore} from 'stores/authStore'

// a sample of option API.
export default {
  name: 'NavDrawer',
  data() {
    return {
      authStore: useAuthStore(),
      menuIndex: 0,
      userItems: [],
      settingsItems: [
        {
          text: this.$t('menu.loginHistory.text'),
          icon: this.$t('menu.loginHistory.icon'),
          routerPath: '/settings/loginHistory',
          authority: 'SYS_ACCOUNT'
        },
        {
          text: this.$t('menu.users.text'),
          icon: this.$t('menu.users.icon'),
          routerPath: '/settings/users',
          authority: 'SYS_ACCOUNT'
        },
        {
          text: this.$t('menu.roleGroup.text'),
          icon: this.$t('menu.roleGroup.icon'),
          routerPath: '/settings/roleGroup',
          authority: 'SYS_ROLE'
        },
        {
          text: this.$t('menu.property.text'),
          icon: this.$t('menu.property.icon'),
          routerPath: '/settings/property',
          authority: 'SYS_CODE'
        },
        {
          text: this.$t('menu.commonCode.text'),
          icon: this.$t('menu.commonCode.icon'),
          routerPath: '/settings/commonCode',
          authority: 'SYS_CODE'
        },
        {
          text: this.$t('menu.fileUpload.text'),
          icon: this.$t('menu.fileUpload.icon'),
          routerPath: '/settings/fileUpload',
          authority: 'SYS_CODE'
        }
      ]
    }
  },
  methods: {
    filterItem(items) {
      return items.filter(e => e.authority === undefined || this.authStore.hasAuthority(e.authority))
    }
  },
  computed: {
    ...mapActions(useAuthStore, ['hasAuthority', 'hasNotAuthority']),
    ...mapWritableState(useAppStore, ['leftDrawerOpen']),
    ...mapState(useAuthStore, ['user', 'userRoles']),
    authorizedUser() {
      return this.filterItem(this.userItems)
    },
    authorizedSettings() {
      return this.filterItem(this.settingsItems)
    }
  }
}
</script>
