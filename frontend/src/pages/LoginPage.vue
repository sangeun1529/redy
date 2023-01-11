<template>
  <div class="fullscreen text-center q-pa-md flex flex-center bg-image" style="overflow: auto;">
    <q-card style="width:400px;" class="bg-primary q-pa-lg" :class="{'animated shakeX': loginFail}">
      <q-card-section class="text-h6 text-white" v-t="'login.text'"/>
      <q-card-section class="q-mt-xs q-mb-sm text-white" v-t="'login.welcome'"/>
      <q-form ref="loginForm" @submit="login">
        <q-card-section>
          <q-input color="white" label-color="white" v-model="form.username" :label="$t('login.id')"
                   :placeholder="$t('login.id')" autofocus outlined :rules="rules.loginUsername"
          >
            <template v-slot:prepend>
              <q-icon color="white" :name="vs.theme.icon.login.username"/>
            </template>
          </q-input>
          <q-input color="white" label-color="white" v-model="form.password" :label="$t('login.password')"
                   :placeholder="$t('login.password')" type="password" outlined class="q-mt-md" @keyup.enter="login"
                   :rules="rules.loginPassword"
          >
            <template v-slot:prepend>
              <q-icon color="white" :name="vs.theme.icon.login.password"/>
            </template>
          </q-input>
        </q-card-section>
        <q-card-actions vertical class="q-mt-md">
          <q-btn flat color="white" type="submit" v-t="'login.text'"/>
        </q-card-actions>
      </q-form>
    </q-card>
  </div>
</template>

<script setup>
import {reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useI18n} from 'vue-i18n'
import api from '@/api'
import vs from 'utils/viewSupport'
import rules from 'utils/validationRules'
import {useAuthStore} from 'stores/authStore'

const authStore = useAuthStore()
const router = useRouter()
const {t} = useI18n()
const form = reactive({
  username: '',
  password: '',
})
const loginForm = ref(null)
const loginFail = ref(false)

async function login() {
  if (await loginForm.value?.validate()) {
    api.auth.login(form).then(r => {
      if (r.error.code === '9900') throw Error(t('login.error'))
      authStore.authenticated = true
      router.push('/')
    }).catch(() => {
      loginFail.value = true
      setTimeout(()=>{
        loginFail.value = false
      }, 1000)
    })
  }
}
</script>

<style scoped>
.bg-image {
  background-image: url("~assets/bg-system.jpg");
  background-repeat: no-repeat;
  background-size: cover;
}
</style>
