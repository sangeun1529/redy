import { defineStore } from 'pinia';

export const useAppStore = defineStore('app', {
  persist: true,
  state: () => {
    return {
      leftDrawerOpen: true,
      router: null
    }
  },

  actions: {
    toggleLeftDrawer () {
      this.leftDrawerOpen = !this.leftDrawerOpen
    }
  }
})
