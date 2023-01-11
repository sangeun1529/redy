import axios from 'axios'
import vs from 'utils/viewSupport'

export default {
  /** Quasar Data Table sorting options to Spring boot PageRequest */
  optionsToParams(options, paramMap = null, sortMap = null) {
    const { sortBy, descending, page, rowsPerPage } = options

    let params = {
      'page': page - 1,
      'size': rowsPerPage,
    }

    if(sortBy) {
      let sortName = (sortMap != null && sortMap[sortBy] != null) ? sortMap[sortBy] : sortBy
      params['sort'] = sortName + ',' + (descending ? 'desc': 'asc')
    }

    if(paramMap != null) {
      for (const [k, v] of Object.entries(paramMap)) {
        if(v)
          params[k] = v
      }
    }
    return params
  },

  call (method, url, params, payload, errorHandler) {
    return new Promise((resolve, reject) => {
      axios({
        method: method,
        url: url,
        params: params,
        data: payload
      }).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        if (errorHandler != null) {
          errorHandler(error, reject)
        } else {
          reject(error)
        }
      })
    })
  },
  get (url, params = null, errorHandler = this.defaultHandleError) {
    return this.call('get', url, params,null, errorHandler)
  },
  post (url, payload, errorHandler = this.defaultHandleError) {
    return this.call('post', url, null, payload, errorHandler)
  },
  put (url, payload, errorHandler = this.defaultHandleError) {
    return this.call('put', url, null, payload, errorHandler)
  },
  delete (url, params = null, errorHandler = this.defaultHandleError) {
    return this.call('delete', url, params,null, errorHandler)
  },
  /* files object {'part':'partName', 'file': fileObj} */
  upload (method, url, payload, files, onUploadProgress, cancelToken, errorHandler) {
    const formData = new FormData()
    if(payload !== undefined && payload != null) {
      formData.append('payload', new Blob([JSON.stringify(payload)], { type: 'application/json'}))
    }
    if(files !== undefined && files != null) {
      for (const element of files) {
        if (element.part !== undefined && element.content !== undefined) {
          formData.append(element.part, element.content)
        } else {
          formData.append('file', element)
        }
      }
    }
    return new Promise((resolve, reject) => {
      axios({
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        method: method,
        url: url,
        data: formData,
        onUploadProgress: onUploadProgress,
        cancelToken: cancelToken
      }).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        errorHandler(error, reject)
      })
    })
  },

  uploadPost (url, payload, files, onUploadProgress, cancelSource = null, errorHandler = this.defaultHandleError) {
    return this.upload('post', url, payload, files, onUploadProgress, cancelSource === null ? null : cancelSource.token, errorHandler)
  },

  uploadPut (url, payload, files, onUploadProgress, cancelSource = null, errorHandler = this.defaultHandleError) {
    return this.upload('put', url, payload, files, onUploadProgress, cancelSource === null ? null : cancelSource.token, errorHandler)
  },

  newCancelSource() {
    return axios.CancelToken.source()
  },

  defaultHandleError (error, reject) {
    if(axios.isCancel(error)) {
      vs.notifyError(error.message)
      reject(error)
    } else if (error.response) {
      const status = error.response.status
      const data = error.response.data
      switch(status) {
        case 401 :
          vs.notifyError('접근 권한이 없습니다.'); break
        case 404 :
          vs.notifyError('찾으시는 내용이 없습니다.'); break
        default :
          vs.notifyError('status : ' + status + ' ' + data.error.title + ', ' + data.error.detail)
      }
    } else if (error.request) {
      vs.notifyError('서버의 응답이 없습니다.')
    } else {
      vs.notifyError(error.message)
    }
    reject(error)
  }
}
