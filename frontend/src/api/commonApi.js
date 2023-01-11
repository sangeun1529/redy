import ajax from './ajax'
import axios from 'axios'

export const commonCode = {
  listAll (options, search) {
    let params = ajax.optionsToParams(options, {'search':search})
    return ajax.get('/code/listAll/', params)
  },

  get(code) {
    return ajax.get('/code/get/' + code)
  },

  listEnabledGroups() {
    return ajax.get('/code/group/list')
  },

  listEnabledByParent(parentCode) {
    return ajax.get('/code/list/' + parentCode)
  },

  /* to call.
    commonCodeService.listEnabledByParentMulti(['100', '101']).then(r => {
        this.showTypeOptions = r.data['100']
        this.showTypeFormatOptions = r.data['101']
      })
   */
  listEnabledByParentMulti(parentCodes) {
    return ajax.get('/code/listMulti/', {parentCodes: parentCodes})
  },

  create(detail) {
    return ajax.post('/code/', detail)
  },

  update(code, detail) {
    return ajax.put('/code/' + code, detail)
  },

  delete(code, detail) {
    return ajax.delete('/code/' + code)
  }

}

export const property = {
  list (options, search) {
    return ajax.get('/property/', ajax.optionsToParams(options, {'search': search}))
  },
  create (detail) {
    return ajax.post('/property/', detail)
  },
  get(key) {
    return ajax.get('/property/' + key)
  },
  update(key, detail) {
    return ajax.put('/property/' + key, detail)
  },
  delete(key) {
    return ajax.delete('/property/' + key)
  }
}

export const upload = {
  list (options, search) {
    return ajax.get('/upload/', ajax.optionsToParams(options), {'search': search})
  },
  create (detail, files, onUploadProgress = null, cancelSource = null) {
    return ajax.uploadPost('/upload/', detail, files,  onUploadProgress, cancelSource)
  },
  get(fileId) {
    return ajax.get('/upload/' + fileId)
  },
  update(fileId, detail, files, onUploadProgress = null, cancelSource = null) {
    return ajax.uploadPut('/upload/' + fileId, detail, files, onUploadProgress, cancelSource)
  },
  delete(fileId) {
    return ajax.delete('/upload/' + fileId)
  },
  download(fileId) {
    return axios.get('/file/dl/' + fileId, { responseType: 'blob' })
  }
}

export const users = {
  list (options, search) {
    return ajax.get('/user/', ajax.optionsToParams(options, {'search':search }, {'roleGroupName' : 'roleGroup.roleGroupId', 'username': '_username'}))
  },
  create (detail) {
    return ajax.post('/user/', detail)
  },
  get(username) {
    return ajax.get('/user/' + username)
  },
  update(username, detail) {
    return ajax.put('/user/' + username, detail)
  },
  delete(username) {
    return ajax.delete('/user/' + username)
  },
  updateMe(detail) {
    return ajax.put('/me', detail)
  },

  loginHistory (options, search, from = null, to = null) {
    let params = ajax.optionsToParams(options, {'search':search, 'from' : from, 'to' : to})
    return ajax.get('/loginHistory', params)
  },
}

export const roleGroup = {
  list () {
    return ajax.get('/roleGroup/')
  },
  create (detail) {
    return ajax.post('/roleGroup/', detail)
  },
  get(roleGroupId) {
    return ajax.get('/roleGroup/' + roleGroupId)
  },
  update(roleGroupId, detail) {
    return ajax.put('/roleGroup/' + roleGroupId, detail)
  },
  delete(roleGroupId) {
    return ajax.delete('/roleGroup/' + roleGroupId)
  },
  listRoles() {
    return ajax.get('/roleGroup/listRoles')
  }
}
