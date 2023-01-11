<template>
  <apexchart height="300" type="line" :options="options" :series="series"></apexchart>
</template>

<script setup>
//https://apexcharts.com/docs/vue-charts/
import { ref } from 'vue'
import { getCssVar } from 'quasar'
import vs from 'utils/viewSupport'

const options = ref({
        title: {
          text: 'ApexLine',
          align: 'left'
        },
        chart: {
          id: 'apex-line',
          locales: vs.theme.chart.locales,
          defaultLocale: vs.theme.chart.defaultLocale,
        },
        colors: [getCssVar('primary'), getCssVar('secondary'), getCssVar('negative')],
        markers: {
          size: 4,
          hover: {
            sizeOffset: 6
          }
        },
        xaxis: {
          categories: [1991, 1992, 1993, 1994, 1995, 1996, 1997, 1998]
        }
      })
const series = ref([{
        name: 'series-1',
        data: [30, 40, 45, 50, 49, 60, 70, 91]
      }])
</script>
