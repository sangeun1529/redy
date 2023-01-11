export default {
  login: {
    text: '로그인',
    welcome: '@:app.title 에 오신것을 환영 합니다', // Linked Message : https://vue-i18n.intlify.dev/guide/essentials/syntax.html#linked-messages
    description: '로그인이 필요합니다',
    button: '로그인',
    error: 'ID 와 패스워드를 확인해 주십시오',
    id: '아이디',
    password: '패스워드'
  },
  logout: {
    text: '로그아웃'
  },

  validate: {
    required: '필수 입력 입니다.',
    min: '{0} 글자 이상 입력 하셔야 합니다.',
    max: '{0} 글자 이하로 입력 하셔야 합니다.',
    between: '{0} 글자 이상 {1} 글자 이하로 입력하셔야 합니다.',
    digitOnly: '숫자만 입력하셔야 합니다.',
    alphaOnly: '영문만 입력하셔야 합니다.',
    key: '영문과 숫자 . 과 - 만 가능합니다.',
    minMax: '{0} ~ {1} 사이의 값을 입력하셔야 합니다.',
    minMaxValue: '{0} ~ {1} 사이의 값을 입력하셔야 합니다.',
    minValue: '{0} 보다 큰 값을 입력하셔야 합니다.',
    positiveNumber: '양수만 가능합니다.',
    alphaDigitOnly: '영문자와 숫자만 가능합니다.',
    alphaUnderBarOnly: '영문자와 _ 만 가능합니다',
    email: '이메일 형식이 아닙니다.',
    url: 'URL 형식이 아닙니다.',
    phoneNumber: '전화번호 형식이 아닙니다.',
    passwordConfirm: '입력하신 패스워드가 서로 맞지 않습니다.',
    choose: '반드시 선택 하셔야 합니다.',
    tooLargeFile: '파일이 너무 큽니다. {0} 이하만 가능합니다.'
  },

  menu: {
    home: {text: '홈', desc: '홈', icon: 'mdi-home'},
    systemSettings: {text: '시스템 설정', desc: '시스템 설정', icon: 'mdi-cog-outline'},
    roleGroup: {text: '권한그룹 관리', desc: '사용자의 권한 그룹을 관리 합니다.', icon: 'mdi-account-multiple-check-outline'},
    property: {text: '시스템 프로퍼티 관리', desc: '시스템 프로퍼티를 관리 합니다.', icon: 'mdi-pencil-outline'},
    users: {text: '사용자 관리', desc: '사용자를 관리 합니다.', icon: 'mdi-account-check-outline'},
    commonCode: {text: '공통코드 관리', desc: '공통코드를 관리 합니다.', icon: 'mdi-counter'},
    fileUpload: {text: '파일 관리', desc: '업로드 파일을 관리 합니다.', icon: 'mdi-file-cabinet'},
    loginHistory: {text: '로그인 기록 조회', desc: '로그인 기록을 조회 합니다.', icon: 'mdi-history'}
  },

  app: {
    title: 'My App',
    footer: {
      caption: '(c) Copyrighted by Team Obvious Traverse 2022.'
    }
  },

  loginHistory: {
    table: {
      historyId: 'ID',
      loginDate: '날짜',
      username: '사용자 ID',
      success: '성공여부',
      clientAddress: '접속 주소',
      message: '메시지'
    }
  },

  commonCode: {
    table: {
      parentCode: '부모 코드',
      code: '코드',
      name: '이름',
      description: '설명',
      enabled: '@:common.use',
      action: ''
    },
    confirm: {
      delete: '부모 코드를 삭제하면 하위 코드가 모두 삭제됩니다. 사용중인 코드를 삭제하면 시스템에 문제가 발생할 수 있습니다. \'사용안함\'으로 바꾸시는것을 추천 합니다. 그래도 삭제 하시겠습니까?'
    },
    result: {
      error: '저장에 실패 하였습니다. 동일 코드가 있는지 확인 바랍니다.'
    }
  },

  dbProperty: {
    table: {
      key: '키',
      value: '값',
      description:'설명',
      action: ''
    },
    confirm: {
      delete: '사용중인 키를 삭제하면 시스템이 오동작 할 수 있습니다. 그래도 삭제 하시겠습니까?'
    },
    result: {
      error: '저장에 실패 하였습니다. 동일 키가 있는지 확인 바랍니다.'
    }
  },

  users: {
    table: {
      username: 'ID',
      displayName: '표시이름',
      dtCreate: '@:common.dtCreate',
      dtUpdate: '@:common.dtUpdate',
      dtLastLogin: '최근 로그인',
      loginFailureCount: '로그인 실패',
      enabled: '@:common.use',
      roleGroupName: '권한그룹',
      action: ''
    },
    result: {
      error: '저장에 실패 하였습니다. 동일 ID가 있는지 확인 바랍니다.'
    },
    passwordConfirm: '패스워드 확인'
  },

  roleGroup: {
    table: {
      roleGroupId: '그룹 ID',
      roleGroupName: '그룹명',
      roleGroupDescription: '그룹 설명',
      action: ''
    },
    confirm: {
      delete: '권한그룹을 삭제하면 사용자의 접근이 제한될 수 있습니다. 그래도 삭제 하시겠습니까?'
    },
    result: {
      deleteError: '사용중인 권한 그룹은 삭제할 수 없습니다.'
    },
  },

  fileUpload: {
    table: {
      fileId: '파일 ID',
      fileName: '파일명',
      contentType: '컨텐트 타입',
      fileSize: '파일크기',
      fileComment: '설명',
      storageType: '저장형태',
      originalFileName: '원본 파일명',
      repositoryPath:'서버 저장 위치',
      file:'파일 선택',
      action: ''
    }
  },

  common: {
    toHome: '메인페이지로',
    profile: '프로필',
    create: '등록',
    save: '저장',
    update: '수정',
    delete: '삭제',
    cancel: '취소',
    alert: '알림',
    add:'추가',
    ok: '확인',
    yes: '예',
    reset: '초기화',
    no: '아니오',
    search: '검색',
    fullscreen: '전체화면',
    fullscreenExit: '전체화면 종료',
    tableFullscreen: '표 전체화면',
    tableFullscreenExit: '표 전체화면 종료',
    dtCreate: '생성시각',
    dtUpdate: '수정시각',
    use: '사용여부',
    download: '다운로드',
    title: '제목',
    description: '설명',
    selectRange: {
      placeholder: '조회 기간 선택'
    },
    result: {
      save: '저장되었습니다.',
      delete: '삭제되었습니다.',
      ok: '적용 되었습니다.'
    },
    confirm: {
      title: '확인',
      save: '저장 하시겠습니까?',
      delete: '삭제 하시겠습니까?'
    },
    currency: {
      style: 'currency', currency: 'KRW', notation: 'standard'    // use $n instead of $t : https://vue-i18n.intlify.dev/guide/essentials/number.html
    },
    format: {
      server: {
        dateTime: 'YYYY-MM-DD HH:mm:ss',
        date: 'YYYY-MM-DD',
        time: 'HH:mm:ss'
      },
      dateTime: 'YYYY-MM-DD HH:mm:ss',
      date: 'YYYY-MM-DD',
      time: 'HH:mm:ss'
    },
  },

  error: {
    http: {
      notFound: '찾으시는 페이지가 없습니다.'
    },
  },


}

// 복수형 처리 https://vue-i18n.intlify.dev/guide/essentials/pluralization.html
