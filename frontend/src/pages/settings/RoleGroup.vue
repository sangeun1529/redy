<template>
  <div>
  <q-card bordered flat class="q-ma-sm">
    <q-card-section>
      <div :class="vs.theme.class.main.title">
        <q-icon :name="$t('menu.roleGroup.icon')" size="md"/>
        <span v-t="'menu.roleGroup.text'"/>
      </div>
      <div :class="vs.theme.class.main.subTitle" v-t="'menu.roleGroup.desc'"/>
    </q-card-section>
    <q-separator/>
    <q-card-section>
      <q-table :dense="$q.screen.lt.md"
               :rows="rows"
               row-key="historyId"
               :columns="columns"
               :loading="loading"
               @request="list"
               :rows-per-page-options="vs.theme.datatable.footer.itemsPerPageOptions"
               binary-state-sort
      >
        <template v-slot:top-left="">
          <q-btn color="primary" @click="showAddDialog">
            <span v-t="'common.add'"/>
          </q-btn>
        </template>
        <template v-slot:top-right="props">
          <q-btn
            flat round dense
            :icon="vs.getFullscreenIcon(props.inFullscreen)"
            @click="props.toggleFullscreen"
            class="q-ml-md"
          >
            <q-tooltip>{{ vs.getTableFullscreenLabel(props.inFullscreen) }}</q-tooltip>
          </q-btn>
        </template>
        <template v-slot:body-cell-action="{row}">
          <q-td>
            <q-btn flat dense size="sm" :icon="vs.theme.icon.edit" @click="showUpdateDialog(row)"
                   color="warning"
            >
              <q-tooltip><span v-t="'common.update'"/></q-tooltip>
            </q-btn>
            <q-btn flat dense size="sm" :icon="vs.theme.icon.delete" @click="deleteRow(row)"
                   color="negative"
            >
              <q-tooltip><span v-t="'common.delete'"/></q-tooltip>
            </q-btn>
          </q-td>
        </template>
      </q-table>
    </q-card-section>
  </q-card>
  <role-group-detail v-model:show="showDialog" :title="$t('menu.roleGroup.text')" :item="itemSelected" @update="updateItem"/>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import vs from 'utils/viewSupport'
import api from '@/api'
import {useI18n} from 'vue-i18n'
import RoleGroupDetail from './RoleGroupDetail'
import {useAuthStore} from 'stores/authStore'

const authStore = useAuthStore()

const showDialog = ref(false)
const itemSelected = ref(null)
const {t} = useI18n()
const loading = ref(false)
const rows = ref([])

const columns = [
  {field: 'roleGroupId', align: 'center', sortable: true}, // always visible, sortable false is default.
  {field: 'roleGroupName', align: 'center', sortable: true}, // align right
  {field: 'roleGroupDescription', align: 'center', sortable: true},
  {field: 'action', align: 'center'},
]
columns.forEach(e => {
  e.label = t('roleGroup.table.' + e.field)
  e.name = e.field
})

function list() {
  loading.value = true
  api.roleGroup.list().then(r => {
    rows.value = r.data
  }).finally(() => {
    loading.value = false
  })
}

function showUpdateDialog(row) {
  itemSelected.value = row
  showDialog.value = true
}

function deleteRow(row) {
  vs.showConfirm(t('roleGroup.confirm.delete')).onOk((()=>{
    api.roleGroup.delete(row.roleGroupId).then((r)=>{
      if (r.error.code === '9100') throw Error()
      list()
    }).catch(err=>{
      vs.notifyError(t('roleGroup.result.deleteError'))
    })
  }))
}

function showAddDialog() {
  itemSelected.value = null
  showDialog.value = true
}

function updateItem(item) {
  if(item) {
    if (itemSelected.value) {
      // update
      api.roleGroup.update(item.roleGroupId, item).then((r)=>{
        vs.notifyInfo(t('common.result.ok'))
        list()
      })
    } else {
      // create
      api.roleGroup.create(item).then((r)=>{
        vs.notifyInfo(t('common.result.ok'))
        list()
      })
    }
  }
}

onMounted(() => {
  list()
})
</script>
