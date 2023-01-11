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
              <q-input dense readonly v-model="itemProxy.roleGroupId" :label="$t('roleGroup.table.roleGroupId')" />
            </div>
            <div class="col q-ma-sm">
              <q-input dense v-model="itemProxy.roleGroupName" :label="$t('roleGroup.table.roleGroupName')" :rules="rules.roleGroupName"/>
            </div>
          </div>
          <div class="row">
            <div class="col q-ma-sm">
              <q-input dense v-model="itemProxy.roleGroupDescription" :label="$t('roleGroup.table.roleGroupDescription')" :rules="rules.generalDescription"/>
            </div>
          </div>
          <div class="row">
            <div class="col q-ma-sm">
              <q-select dense options-dense map-options emit-value use-chips multiple
                        v-model="itemProxy.userRoles" :options="roleOptions" option-label="roleName" option-value="roleName" :label="$t('users.table.roleGroupName')" >
                <template v-slot:option="scope">
                  <q-item v-bind="scope.itemProps">
                    <q-item-section>
                      <q-item-label>{{ scope.opt.roleName }}</q-item-label>
                      <q-item-label caption>{{ scope.opt.roleDescription }}</q-item-label>
                    </q-item-section>
                  </q-item>
                </template>
              </q-select>
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
import api from '@/api'
const props = defineProps(def)
const emit = defineEmits(['update', 'update:show'])

const itemProxy = ref({})
const showProxy = ref(false)
const btnSaveLabel = ref('')
const form = ref(null)
const roleOptions = ref([])

async function loadRoleOptions() {
  const r = await api.roleGroup.listRoles()
  roleOptions.value = r.data
}

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
  loadRoleOptions()
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
