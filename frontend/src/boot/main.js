import { boot } from 'quasar/wrappers'
import { i18n } from '@/locale'
import { Quasar } from 'quasar'
import axios from 'axios'
import mdiIconSet from 'quasar/icon-set/mdi-v6'
import VueApexCharts from 'vue3-apexcharts'

/* setup Axios default */
function setupAxios() {
  axios.defaults.baseURL = '/ajax'
  axios.defaults.headers.common.Accept = 'application/json'
  axios.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest'
}

/* setup Quasar */
async function customizeQuasar(ssrContext) {
  try {
    const lang = i18n.global.locale.value === 'ko' ? 'ko-KR' : i18n.global.locale.value
    /* webpackInclude: /(de|en-US)\.js$/ */
    await import('quasar/lang/' + lang ).then(l => {
      Quasar.lang.set(l.default, ssrContext)
    })
  } catch (err) {
    console.log('set locale failed [' + i18n.global.locale.value + '] :' + JSON.stringify(err))
  }
  Quasar.iconSet.set(mdiIconSet, ssrContext)  // quasar.config.js, app.scss, viewSupport.js
}

// "async" is optional;
// more info on params: https://v2.quasar.dev/quasar-cli/boot-files
export default boot(async ( { app, ssrContext } ) => {
  app.use(i18n) // locale.
  app.use(VueApexCharts)  // https://apexcharts.com/docs/vue-charts/
  await customizeQuasar(ssrContext)
  setupAxios()
})
