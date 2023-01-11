<template>
  <q-card bordered flat class="q-ma-sm">
    <q-card-section>
      <div :class="vs.theme.class.main.title">
        <q-icon :name="$t('menu.property.icon')" size="md"/>
        <span v-t="'menu.property.text'"/>
      </div>
      <div :class="vs.theme.class.main.subTitle" v-t="'menu.property.desc'"/>
    </q-card-section>
    <q-separator/>
    <q-card-section>
      <q-table :dense="$q.screen.lt.md"
               :rows="rows"
               row-key="historyId"
               :columns="columns"
               v-model:pagination="pagination"
               :loading="loading"
               @request="list"
               :rows-per-page-options="vs.theme.datatable.footer.itemsPerPageOptions"
               :filter="filter"
               binary-state-sort
      >
        <template v-slot:top-left="">
          <q-btn color="primary" @click="showAddDialog">
            <span v-t="'common.add'"/>
          </q-btn>
        </template>
        <template v-slot:top-right="props">
          <q-input dense debounce="500" v-model="filter.search" :placeholder="$t('common.search')">
            <template v-slot:append>
              <q-icon :name="vs.theme.icon.search" />
            </template>
          </q-input>
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
  <property-detail v-model:show="showDialog" :title="$t('menu.property.text')" :item="itemSelected" @update="updateItem"/>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import vs from 'utils/viewSupport'
import api from '@/api'
import {useI18n} from 'vue-i18n'
import PropertyDetail from './PropertyDetail'

const showDialog = ref(false)
const itemSelected = ref(null)
const {t} = useI18n()
const loading = ref(false)
const rows = ref([])
const pagination = ref({
  sortBy: 'key',
  descending: false,
  page: 1,
  rowsPerPage: vs.theme.datatable.footer.itemsPerPageDefault,
  rowsNumber: 0
})
const filter = ref({
  search: ''
})

const columns = [
  {field: 'key', align: 'center', sortable: true, style:'width:20%'}, // always visible, sortable false is default.
  {field: 'value', align: 'center', sortable: true, style:'width:30%'}, // align right
  {field: 'description', align: 'center', style:'width:40%'},
  {field: 'action', align: 'center', style:'10%'}
]
// 문서의 name: row=>row.field 는 동작 하지 않는다.
columns.forEach(e => {
  e.label = t('dbProperty.table.' + e.field)
  e.name = e.field
})

function list(props) {
  loading.value = true
  const {page, rowsPerPage, sortBy, descending} = props.pagination

  // don't forget to update local pagination object
  pagination.value.rowsPerPage = rowsPerPage
  pagination.value.page = page
  pagination.value.sortBy = sortBy
  pagination.value.descending = descending

  api.property.list(pagination.value, filter.value.search).then(r => {
    rows.value = r.data.content
    pagination.value.rowsNumber = r.data.totalElements

  }).finally(() => {
    loading.value = false
  })
}

function showUpdateDialog(row) {
  itemSelected.value = row
  showDialog.value = true
}

function deleteRow(row) {
  vs.showConfirm(t('dbProperty.confirm.delete')).onOk((()=>{
    api.property.delete(row.key).then(()=>{
      list({pagination: pagination.value})
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
      api.property.update(item.key, item).then((r)=>{
        if (r.error.code === '9100') throw Error()
        vs.notifyInfo(t('common.result.ok'))
        list({pagination: pagination.value})
      }).catch(err=>{
        vs.notifyError(t('dbProperty.result.error'))
      })
    } else {
      // create
      api.property.create(item).then((r)=>{
        if (r.error.code === '9100') throw Error()
        vs.notifyInfo(t('common.result.ok'))
        list({pagination: pagination.value})
      }).catch(err=>{
        vs.notifyError(t('dbProperty.result.error'))
      })
    }
  }
}

onMounted(() => {
  list({pagination: pagination.value})
})
</script>
