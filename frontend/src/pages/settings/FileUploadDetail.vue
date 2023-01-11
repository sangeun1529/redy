<template>
  <q-dialog persistent v-model="showProxy" @before-show="onBeforeShow" @before-hide="onBeforeHide">
    <q-card style="width:600px;">
      <q-form ref="form">
        <q-card-section>
          <div class="text-h6">{{ props.title }}</div>
        </q-card-section>
        <q-card-section>
          <div class="row">
            <div class="col q-ma-sm">
              <q-input dense readonly v-model="itemProxy.fileId" :label="$t('fileUpload.table.fileId')"/>
            </div>
            <div class="col q-ma-sm">
              <q-input dense readonly v-model="itemProxy.fileSize" type="number"
                       :label="$t('fileUpload.table.fileSize')"/>
            </div>
          </div>
          <div class="row">
            <div class="col q-ma-sm">
              <q-input dense readonly v-model="itemProxy.originalFileName"
                       :label="$t('fileUpload.table.originalFileName')"/>
            </div>
            <div class="col q-ma-sm">
              <q-input dense readonly v-model="itemProxy.contentType" :label="$t('fileUpload.table.contentType')"/>
            </div>
          </div>
          <div class="row">
            <div class="col q-ma-sm">
              <q-input dense readonly v-model="itemProxy.repositoryPath" :label="$t('fileUpload.table.repositoryPath')"
                       :rules="[NullOrMax(50)]"/>
            </div>
          </div>
          <div class="row">
            <div class="col q-ma-sm">
              <q-select dense map-options emit-value options-dense v-model="itemProxy.storageType"
                        :options="['DB', 'FS']" :label="$t('fileUpload.table.storageType')" :rules="[Choose()]"/>
            </div>
            <div class="col q-ma-sm">
              <q-input dense v-model="itemProxy.fileName" :label="$t('fileUpload.table.fileName')"
                       :rules="[NullOrMax(50)]"/>
            </div>
          </div>
          <div class="row">
            <div class="col q-ma-sm">
              <q-input dense v-model="itemProxy.fileComment" type="textarea" rows="3"
                       :label="$t('fileUpload.table.fileComment')" :rules="rules.generalDescription"/>
            </div>
          </div>
          <div class="row">
            <div class="col q-ma-sm">
              <q-file dense v-model="files[0]" :label="$t('fileUpload.table.file')" :rules="ruleFileSize"/>
            </div>
          </div>
          <div class="row" v-if="progress > 0">
            <div class="col q-ma-sm">
              <q-linear-progress size="20px" :value="progress">
                <div class="absolute-full flex flex-center">
                  <q-badge color="white" text-color="black" :label="progressLabel"/>
                </div>
              </q-linear-progress>
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
import rules, {NullOrMax, Choose} from 'utils/validationRules'
import api from '@/api'
import ajax from '@/api/ajax'

const props = defineProps(def)
const emit = defineEmits(['update', 'update:show'])

const itemProxy = ref({})
const showProxy = ref(false)
const files = ref([])
const btnSaveLabel = ref('')
const form = ref(null)
const progress = ref(0)
const cancelSource = ref(null)

async function save() {
  if (await form.value.validate()) {
    vs.showConfirm(t('common.confirm.save')).onOk(() => {
      cancelSource.value = ajax.newCancelSource()
      if (isCreateMode.value) {
        api.upload.create(itemProxy.value, files.value, progressEvent => {
          progress.value = Math.round(progressEvent.loaded / progressEvent.total)
        }, cancelSource.value).then(() => {
          emit('update', itemProxy.value)
          close()
        })
      } else {
        api.upload.update(itemProxy.value.fileId, itemProxy.value, files.value, progressEvent => {
          progress.value = Math.round(progressEvent.loaded / progressEvent.total)
        }, cancelSource.value).then(() => {
          emit('update', itemProxy.value)
          close()
        })
      }
    })
  }
}

function close() {
  showProxy.value = false
  emit('update:show', showProxy.value)
}

function onBeforeShow() {
  itemProxy.value = props.item ? JSON.parse(JSON.stringify(props.item)) : {storageType: 'DB'} // object deep copy required.
  progress.value = 0.0
  files.value = []
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
const progressLabel = computed(() => (progress.value * 100).toFixed(2) + '%')
const ruleFileSize = computed(() => {
    if (isCreateMode.value) {
      return [v => !!v || t('validate.required'), v => (v !== undefined && v.size > 0) || t('validate.required'), v => (v !== undefined && v.size < (1024 * 1024 * 500)) || t('validate.tooLargeFile', ['500MB'])]
    } else {
      return [v => {
        if (v === undefined) return true
        return ((v !== undefined && v.size < (1024 * 1024 * 500)) || t('validate.tooLargeFile', ['500MB']))
      }]
    }
  }
)
</script>
