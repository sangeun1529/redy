# redy ( Kotlin  Web Alone )
## 개요
* core-web
* skeleton project
* sub module 이 아닌 통합 Web 프로젝트 샘플.
* Spring MVC thymeleaf template engine.
* Vue, Quasar frontend.
* node version 16.x 설치
* java 11 설치

## Build
* local H2 build

``` gradle clean build publish```

* build for dev skip tests

``` gradle -Pdev -x test clean build publish```

* build for prod skip tests

``` gradle -Pprod -x test clean build publish```

## Frontend
* readMe 작성