import ajax from './ajax'
import vs from 'utils/viewSupport'
import { useAuthStore } from 'stores/authStore'

export const auth = {
  login (detail) {
    return ajax.post('/login', detail)
  },
  logout () {
    return ajax.get('/logout')
  },
  getAuth(){
    return ajax.get('/auth')
  },
  me() {  // 이게 401 혹은 어떤 에러든  세션이 풀린것이다. login 으로 보낸다.
    let authStore = useAuthStore()
    return ajax.get('/me', null, (error, reject) => {
      if(error.response.status) {
        authStore.logout()
        vs.notifyError(error.response.statusText)
      } else if (error.request) {
        vs.notifyError('서버의 응답이 없습니다.')
    } else {
      vs.notifyError(error.response.message)
    }
    authStore.logout()
    reject(error)
    })
  },
}
