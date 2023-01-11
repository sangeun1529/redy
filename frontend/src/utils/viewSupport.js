import {date, Dialog, Notify} from 'quasar'
import {i18n} from '@/locale'

const {t} = i18n.global
export default {
  /* Render Utils */
  renderEnabled(enabled) {
    return enabled === 'YES' ? t('common.yes') : t('common.no')
  },

  enabledOptions: [
    {label: t('common.yes'), value: 'YES'},
    {label: t('common.no'), value: 'NO'}
  ],

  renderServerDatetime(datetime) {
    return date.formatDate(Date.parse(datetime), t('common.format.dateTime')) // common.format.server.dateTime 으로 parsing 하는 부분이 필요함
  },

  getFullscreenIcon(active) {
    return active ? this.theme.icon.fullscreenExit : this.theme.icon.fullscreen
  },

  getFullscreenLabel(active) {
    return active ? t('common.fullscreenExit') : t('common.fullscreen')
  },

  getTableFullscreenLabel(active) {
    return active ? t('common.tableFullscreenExit') : t('common.tableFullscreen')
  },

  /* Notify Utils */
  notifyError(errorMessage) {
    Notify.create({
      message: errorMessage,
      color: 'red',
      icon: this.theme.icon.notify.error,
      position: 'top-right'
    })
  },
  notifyInfo(infoMessage) {
    Notify.create({
      message: infoMessage,
      color: 'primary',
      icon: this.theme.icon.notify.info,
      position: 'top-right'
    })
  },

  showAlert(infoMessage, title = t('common.alert')) {
    Dialog.create({
      title: title,
      message: infoMessage,
      persistent: true
    })
  },

  showConfirm(confirmMessage, title = t('common.confirm.title')) {
    return Dialog.create({
      title: title,
      message: confirmMessage,
      persistent: true,
      cancel: true
    })
  },

  /** css class & view default */
  theme: {
    class: {
      main: {
        title: 'text-h6 text-indigo-8 no-wrap',
        subTitle: 'text-subtitle3 text-grey'
      },
    },
    icon: {
      menu: 'mdi-menu',
      search: 'mdi-magnify',
      edit: 'mdi-pencil',
      delete: 'mdi-delete',
      download: 'mdi-download',
      fullscreenExit: 'mdi-fullscreen-exit',
      fullscreen: 'mdi-fullscreen',
      notify: {
        error: 'mdi-alert-circle-outline',
        info: 'mdi-information-variant'
      },
      dashboardCard : 'mdi-robot-outline',
      dateRangePicker: {
        calendar: 'mdi-calendar'
      },
      login: {
        username: 'mdi-account',
        password: 'mdi-key'
      }
    },
    datatable: {
      footer: {
        itemsPerPageOptions: [20, 50, 100],
        itemsPerPageDefault: 20
      }
    },
    chart: {
      locales: [require('apexcharts/dist/locales/en.json'), require('apexcharts/dist/locales/ko.json')],
      defaultLocale: ( i18n.global.locale === 'ko-KR' || i18n.global.locale === 'ko') ? 'ko' : 'en'
    }
  }
}
