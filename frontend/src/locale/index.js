import { createI18n } from 'vue-i18n'
import koKR from './messages/ko-KR'

export const i18n = createI18n({
  legacy: false,
  locale: navigator.language,
  fallbackLocale: 'ko-KR',
  messages: {
    'ko': koKR,
    'ko-KR': koKR
  }
})
