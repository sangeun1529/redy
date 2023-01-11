<template>
  <q-card bordered flat class="q-ma-sm">
    <q-card-section>
      <div :class="vs.theme.class.main.title">
        <q-icon :name="$t('menu.loginHistory.icon')" size="md"/>
        <span v-t="'menu.loginHistory.text'"/>
      </div>
      <div :class="vs.theme.class.main.subTitle" v-t="'menu.loginHistory.desc'"/>
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
        <template v-slot:top-right="props">
          <date-ranger-picker v-model:range="filter.range" class="q-mr-sm"/>
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
        <template v-slot:body-cell-success="{row}">
          <q-td style="text-align: center">
            {{ vs.renderEnabled(row.success) }}
          </q-td>
        </template>
        <template v-slot:body-cell-loginDate="{row}">
          <q-td style="text-align: center">
            {{ vs.renderServerDatetime(row.loginDate) }}
          </q-td>
        </template>
      </q-table>
    </q-card-section>
  </q-card>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import vs from 'utils/viewSupport'
import api from '@/api'
import DateRangerPicker from 'components/DateRangePicker'
import {useI18n} from 'vue-i18n'

const {t} = useI18n()
const loading = ref(false)
const rows = ref([])
const pagination = ref({
  sortBy: 'historyId',
  descending: true,
  page: 1,
  rowsPerPage: vs.theme.datatable.footer.itemsPerPageDefault,
  rowsNumber: 0,
})
const filter = ref({
  search: '',
  range: {from: null, to: null}
})

const columns = [
  {required: true, field: 'historyId', align: 'center', sortable: true}, // always visible, sortable false is default.
  {field: 'loginDate', align: 'center', sortable: true}, // align right
  {field: 'username', align: 'center', sortable: true},
  {field: 'success', align: 'center', sortable: true},
  {field: 'clientAddress', align: 'center', sortable: true},
  {field: 'message', align: 'left', headerStyle: 'text-align:center'}
]
// 문서의 name: row=>row.field 는 동작 하지 않는다.
columns.forEach(e => {
  e.label = t('loginHistory.table.' + e.field)
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

  api.users.loginHistory(pagination.value, filter.value.search, filter.value.range.from, filter.value.range.to).then(r => {
    rows.value = r.data.content
    pagination.value.rowsNumber = r.data.totalElements

  }).finally(() => {
    loading.value = false
  })
}

onMounted(() => {
  list({pagination: pagination.value})
})
</script>
