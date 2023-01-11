import { route } from 'quasar/wrappers'
import {createRouter, createMemoryHistory, createWebHistory, createWebHashHistory} from 'vue-router'
import { useAuthStore } from 'stores/authStore'
import { useAppStore } from 'stores/appStore'
import { i18n } from '@/locale'
import { h, resolveComponent } from 'vue'

const {t} = i18n.global

const makeRoute = (authStore) => {
  const requireAuth = () => (from, to, next) => {
    if (authStore.authenticated === true) {
      next()
      return
    }
    next('/login')
  }

  const alreadyLogin = () => (from, to, next) => {
    if (authStore.authenticated === true) {
      next('/')
      return
    }
    next()
  }


  const checkRole = () => (from, to, next) => {
    if (Object.prototype.hasOwnProperty.call(from, 'meta') && Object.prototype.hasOwnProperty.call(from.meta, 'role') && authStore.hasNotAuthority(from.meta.role)) {
      next('/')
      return
    }
    next()
  }

  return [
    {
      path: '/',
      component: () => import('layouts/MainLayout.vue'),
      beforeEnter: requireAuth(),
      children: [
        {
          path: '',
          name: 'Index',
          component: () => import('pages/IndexPage'),
          meta: {
            breadcrumb: [{text:t('menu.home.text'), to:{path:'/'}}]
          },
        },
        {
            path:'/settings',
          redirect: '/settings/users',
          beforeEnter: checkRole(),
          component: {
             render() { return h(resolveComponent('router-view'))}
          },
          children: [
            {
              path: 'users',
              name: 'Users',
              component: () =>import('pages/settings/AdminUsers'),
              meta: {
                role: 'SYS_ACCOUNT',
                breadcrumb: [{text:t('menu.home.text'), to:{path:'/'}}, {text:t('menu.systemSettings.text'), to:{path:'/settings'}}, {text:t('menu.users.text'), to:{path:'/settings/users'}}],
              }
            },
            {
              path: 'loginHistory',
              name: 'LoginHistory',
              component: () => import('pages/settings/LoginHistory'),
              meta: {
                role: 'SYS_ACCOUNT',
                breadcrumb: [{text:t('menu.home.text'), to:{path:'/'}}, {text:t('menu.systemSettings.text'), to:{path:'/settings'}}, {text:t('menu.loginHistory.text'), to:{path:'/settings/loginHistory'}}],
              }
            },
            {
              path: 'commonCode',
              name: 'CommonCode',
              component: () => import('pages/settings/CommonCode'),
              meta: {
                role: 'SYS_CODE',
                breadcrumb: [{text:t('menu.home.text'), to:{path:'/'}}, {text:t('menu.systemSettings.text'), to:{path:'/settings'}}, {text:t('menu.commonCode.text'), to:{path:'/settings/commonCode'}}],
              }
            },
            {
              path: 'property',
              name: 'Property',
              component: () => import('pages/settings/DbProperty'),
              meta: {
                role: 'SYS_CODE',
                breadcrumb: [{text:t('menu.home.text'), to:{path:'/'}}, {text:t('menu.systemSettings.text'), to:{path:'/settings'}}, {text:t('menu.property.text'), to:{path:'/settings/property'}}],
              }
            },
            {
              path: 'users',
              name: 'AdminUsers',
              component: () => import('pages/settings/AdminUsers'),
              meta: {
                role: 'SYS_ACCOUNT',
                breadcrumb: [{text:t('menu.home.text'), to:{path:'/'}}, {text:t('menu.systemSettings.text'), to:{path:'/settings'}}, {text:t('menu.users.text'), to:{path:'/settings/users'}}],
              }
            },
            {
              path: 'roleGroup',
              name: 'RoleGroup',
              component: () => import('pages/settings/RoleGroup'),
              meta: {
                role: 'SYS_ROLE',
                breadcrumb: [{text:t('menu.home.text'), to:{path:'/'}}, {text:t('menu.systemSettings.text'), to:{path:'/settings'}}, {text:t('menu.roleGroup.text'), to:{path:'/settings/roleGroup'}}],
              }
            },
            {
              path: 'fileUpload',
              name: 'FileUpload',
              component: () => import('pages/settings/FileUpload'),
              meta: {
                role: 'SYS_CODE',
                breadcrumb: [{text:t('menu.home.text'), to:{path:'/'}}, {text:t('menu.systemSettings.text'), to:{path:'/settings'}}, {text:t('menu.fileUpload.text'), to:{path:'/settings/fileUpload'}}],
              }
            },
          ]
        },
      ],
    },
    {
      path: '/login',
      name: 'LoginPage',
      component: () => import('pages/LoginPage'),
      beforeEnter: alreadyLogin(),
    },
    {
      path: '/:catchAll(.*)*',
      component: () => import('pages/ErrorNotFound')
    },
  ]
}

export default route(function ( /* { store } */ ) {
  const authStore = useAuthStore()
  const appStore = useAppStore()
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : (process.env.VUE_ROUTER_MODE === 'history' ? createWebHistory : createWebHashHistory)

  const routes = makeRoute(authStore)
  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,

    // Leave this as is and make changes in quasar.conf.js instead!
    // quasar.conf.js -> build -> vueRouterMode
    // quasar.conf.js -> build -> publicPath
    history: createHistory(process.env.MODE === 'ssr' ? void 0 : process.env.VUE_ROUTER_BASE)
  })

  appStore.router = Router
  return Router
})
