<template>
  <div class="fullscreen bg-primary text-white text-center q-pa-md flex flex-center" style="overflow: auto;">
    <div>
      <div style="font-size: 30vh">
        404
      </div>

      <div class="text-h2" style="opacity:.4" v-t="'error.http.notFound'">
      </div>

      <q-btn class="q-mt-xl" color="white" text-color="blue" unelevated to="/" :label="$t('common.toHome')" no-caps/>
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'ErrorNotFound'
})
</script>
