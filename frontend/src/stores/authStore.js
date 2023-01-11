import { defineStore } from 'pinia'
import api from '@/api'
import { useAppStore } from './appStore'

export const useAuthStore = defineStore('auth', {
  persist:true,
  state: () => {
    return {
      user: null,
      userRoles: null,
      authenticated: false,
    }
  },

  getters: {
    hasAuthority: (state) => (role) => state.userRoles != null && state.userRoles.includes(role),
    hasNotAuthority : (state) => (role) => state.userRoles == null || !state.userRoles.includes(role),
  },

  actions: {
    logout() {1
      this.$patch({
        user: null,
        userRoles: null,
        authenticated: false,
      })
      api.auth.logout()
      useAppStore().router.push('/login').then(() => {})

    },
    async getMe() {
      const data = await api.auth.me()
      this.$patch({
        user: data.data.user,
        userRoles: data.data.roleGroup.userRoles,
      })
    },
    async checkAuth() {
      const res = await api.auth.getAuth()
      this.$patch({
        authenticated: res.data.authenticated,
      })
    }
  }
})
