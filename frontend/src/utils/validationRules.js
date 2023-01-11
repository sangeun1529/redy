import {i18n} from '@/locale'

const { t } = i18n.global

export function Required() { return (v) => !!v || t('validate.required') }
export function Between(min, max) { return (v) => (v && v.length >= min && v.length <= max) || t('validate.between', [min, max]) }
export function AlphaOnly() { return (v) => (v && /^[a-zA-Z]*$/.test(v)) || t('validate.alphaOnly') }
export function DigitOnly() { return (v) => (v === 0 || (v && /^\d*$/.test(v))) || t('validate.digitOnly') }
export function AlphaDigitOnly() {return (v) => (v && /^[a-zA-Z\d]*$/.test(v)) || t("validate.alphaDigitOnly")}
export function NullOrMax(max) { return (v) => (!!v) ? ((v && v.length <= max) || t('validate.max', [max])) : true }
export function NullOrMin(min) { return (v) => (!!v) ? ((v && v.length >= min) || t('validate.min', [min])) : true }
export function MinMax(min, max) { return (v) => ((min === 0 && v === 0) || (v && v >= min && v <= max)) || t('validate.minMax', [min, max])}
export function MinMaxValue(min, max) { return (v) => ((min === 0 && v === 0) || (v && v >= min && v <= max)) || t("validate.minMaxValue", [min, max])}
export function NullOrMinValue(min) { return (v) => (v && v !== '') ? ((v && v >= min) || t("validate.minValue", [min])) : true }
export function PositiveNumber() { return (v) => (v === 0 || (v && /^\d*$/.test(v))) || t("validate.positiveNumber") }
export function Email() { return (v) => (/^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,5})+$/.test(v)) || t('validate.email')}
export function Url() { return (v) => (/^((ftp|http|https):\/\/)?(www.)?(?!.*(ftp|http|https|www.))[\w-]+(\.[a-zA-Z]+)+((\/)[\w#]+)*(\/\w+\?\w+=\w+(&\w+=\w+)*)?$/.test(v))
  || t('validate.url')}
export function Choose() { return (v) => v!=null && v.length!==0 || t('validate.choose') }
export function PhoneNumber() { return (v) => ((v && /^0\d{2}-\d{4}-\d{4}$/i.test(v)) || i18n.t("validate.phoneNumber"))}

export default {
  loginUsername: [
    Required(),
    Between(4, 50)
  ],
  loginPassword: [
    Required(),
    Between(4, 50)
  ],
  userDisplayName: [
    Required(),
    Between(1, 120)
  ],
  generalName: [
    Required(),
    AlphaOnly(),
    Between(2, 50)
  ],
  roleGroupName: [
    Required(),
    v => (v && /^[a-zA-Z\_]*$/.test(v)) || t("validate.alphaUnderBarOnly"),
    Between(2, 50)
  ],
  generalDescription: [
    NullOrMax(2048)
  ],
  generalRequiredText: [
    Required(),
    Between(1, 255)
  ],
  propertyKey: [
    Required(),
    Between(4,50),
    v => (v && /^[a-zA-Z\d.-]*$/.test(v)) || t("validate.key")
  ],
  propertyValue: [
    NullOrMax(255)
  ],
  commonCode: [
    Required(),
    Between(1,20)
  ],
  commonCodeName: [
    Required(),
    Between(1,50)
  ]
}
