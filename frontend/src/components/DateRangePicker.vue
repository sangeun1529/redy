<template>
  <q-input dense readonly v-model="renderRange" :placeholder="renderPlaceholder" style="width:220px;">
    <template v-slot:prepend>
      <q-btn dense flat :icon="vs.theme.icon.dateRangePicker.calendar">
        <q-popup-proxy transition-show="jump-down" transition-hide="jump-up" @before-show="updateProxy">
          <q-date v-model="rangeProxy"
            range :mask="$t('common.format.date')"
          >
            <div class="row items-center justify-end q-gutter-sm">
              <q-btn :label="$t('common.cancel')" color="primary" flat v-close-popup />
              <q-btn :label="$t('common.reset')" color="primary" flat @click="reset"/>
              <q-btn :label="$t('common.ok')" color="primary" flat @click="save" v-close-popup />
            </div>
          </q-date>
        </q-popup-proxy>
      </q-btn>
    </template>
  </q-input>
</template>

<script setup>
import {computed, ref} from 'vue'
import {useI18n} from 'vue-i18n'
import vs from 'utils/viewSupport'

// you cannot use any function in defineProps
const props = defineProps({
  range: {
    type: Object, default() {
      return {from: null, to: null}
    }
  },
  title: {type: String, default: null},
  placeholder: {type: String, default: null}
})

const emit = defineEmits(['update:range'])
const aRange = ref({})  // props 로 부터 넘어와서 OK 를 누르면 emit 할 값.
const rangeProxy = ref({}) // date picker 가 임시로 받는값
const renderRange = computed(() => {
  if(aRange.value.from && aRange.value.to) return aRange.value.from + ' ~ ' + aRange.value.to
  return ''
})
const { t } = useI18n()
const renderPlaceholder = computed( () => props.placeholder ? props.placeholder : t('common.selectRange.placeholder'))

function updateProxy() {
  rangeProxy.value = aRange.value
}

function save() {
  if(typeof rangeProxy.value === 'string') {
    aRange.value = { from: rangeProxy.value, to: rangeProxy.value }
  } else {
    aRange.value = rangeProxy.value
  }
  emit('update:range', aRange.value)
}

function reset() {
  rangeProxy.value = {}
}
</script>
