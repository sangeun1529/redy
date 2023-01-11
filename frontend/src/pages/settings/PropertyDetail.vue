<template>
  <q-dialog persistent v-model="showProxy" @before-show="onBeforeShow" @before-hide="onBeforeHide">
    <q-card style="width:400px;">
      <q-form ref="form">
        <q-card-section>
          <div class="text-h6">{{ props.title }}</div>
        </q-card-section>
        <q-card-section>
          <div class="row">
            <div class="col q-ma-sm">
              <q-input dense :readonly="isUpdateMode" v-model="itemProxy.key" :label="$t('dbProperty.table.key')" :rules="rules.propertyKey"/>
            </div>
            <div class="col q-ma-sm">
              <q-input dense v-model="itemProxy.value" :label="$t('dbProperty.table.value')" :rules="rules.propertyValue"/>
            </div>
          </div>
          <div class="row">
            <div class="col q-ma-sm">
              <q-input dense v-model="itemProxy.description" type="textarea" rows="4" :label="$t('dbProperty.table.description')" :rules="rules.generalDescription"/>
            </div>
          </div>
        </q-card-section>
        <q-card-actions class="flex justify-end">
          <q-btn @click="close" color="positive"><span v-t="'common.cancel'"/></q-btn>
          <q-btn @click="save" color="negative">{{ btnSaveLabel }}</q-btn>
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script>
import {i18n} from '@/locale'

const {t} = i18n.global // setup 이 아니면 useI18n() 을 부를 수 없다.
const def = {
  item: {
    type: Object, default() {
      return null
    }
  },
  show: {type: Boolean, default: false},
  title: {type: String, default: t('common.alert')}  // setup 안에서는 t 를 부를 수 없다. https://eslint.vuejs.org/rules/valid-define-props.html
}
</script>
<script setup>
import {computed, ref, watch} from 'vue'
import vs from 'utils/viewSupport'
import rules from 'utils/validationRules'

const props = defineProps(def)
const emit = defineEmits(['update', 'update:show'])

const itemProxy = ref({})
const showProxy = ref(false)
const btnSaveLabel = ref('')
const form = ref(null)
async function save() {
  if (await form.value.validate()) {
    vs.showConfirm(t('common.confirm.save')).onOk(() => {
      emit('update', itemProxy.value)
      close()
    })
  }
}

function close() {
  showProxy.value = false
  emit('update:show', showProxy.value)
}

function onBeforeShow() {
  itemProxy.value = props.item ? JSON.parse(JSON.stringify(props.item)) : {} // object deep copy required.
  btnSaveLabel.value = props.item ? t('common.update') : t('common.save')
}

function onBeforeHide() {
  itemProxy.value = null
}

watch(
  () => props.show,
  show => showProxy.value = props.show
)

const isUpdateMode = computed(() => props.item !== null)
const isCreateMode = computed(() => props.item === null)
</script>
