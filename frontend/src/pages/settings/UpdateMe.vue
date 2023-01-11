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
              <q-input dense readonly v-model="itemProxy.username" :label="$t('users.table.username')" :rules="rules.loginUsername"/>
            </div>
            <div class="col q-ma-sm">
              <q-input dense v-model="itemProxy.displayName" :label="$t('users.table.displayName')" :rules="rules.userDisplayName"/>
            </div>
          </div>
          <div class="row">
            <div class="col q-ma-sm">
              <q-input dense v-model="itemProxy.password" type="password" :label="$t('login.password')" :rules="rulePassword"/>
            </div>
            <div class="col q-ma-sm">
              <q-input dense v-model="itemProxy.passwordConfirm" type="password" :label="$t('users.passwordConfirm')" :rules="rulePasswordConfirm"/>
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
import rules, {Required, Between, NullOrMin} from 'utils/validationRules'
import api from '@/api'
const props = defineProps(def)
const emit = defineEmits(['update', 'update:show'])

const itemProxy = ref({})
const showProxy = ref(false)
const btnSaveLabel = ref('')
const form = ref(null)
const roleGroupOptions = ref([])

async function loadRoleGroupOptions() {
  const r = await api.roleGroup.list()
  roleGroupOptions.value = r.data
}

async function save() {
  if (await form.value.validate()) {
    vs.showConfirm(t('common.confirm.save')).onOk(() => {
      api.users.updateMe(itemProxy.value).then(()=>{
        emit('update', itemProxy.value)
        close()
      })
    })
  }
}

function close() {
  showProxy.value = false
  emit('update:show', showProxy.value)
}

function onBeforeShow() {
  loadRoleGroupOptions()
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

const rulePassword = computed(() => isCreateMode.value ? [Required(), Between(4,50)] : [NullOrMin(4)])
const rulePasswordConfirm = computed( () => [Required(), () => (itemProxy.value.password === itemProxy.value.passwordConfirm) || t('validate.passwordConfirm')])
</script>
