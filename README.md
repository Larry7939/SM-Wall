# SM-Wall
졸업프로젝트-상명 담벼락

2022 - 07 - 01
 - Repository 생성
 
2022 - 07 - 04
 - main, register layout 추가
 
2022 - 07 - 05
 - layout, menu, drawable 파일 업로드

2022 - 07 - 11
 - Retrofit 세팅
 - build.gradle 세팅(viewBinding,Retrofit,okhttp,glide,Interceptor,Gson converter)
 - implementation'androidx.core:core-ktx:1.7.0' -> 1.6.0
 - Manifest 세팅(Internet Permission)
 - mvp패턴 적용
  - config/src/util폴더 생성
  - Application class 작성
  - Retrofit 세팅 및 Base 파일 구현
  - config - 전역변수 초기화 역할 Application class, 부모 Activity / Fragment / Response
  - src - 구현 파일(각 기능마다 하위 폴더 만들 예정)
  - util - Adapter나 LoadingDialog와 같은 유틸 파일
 - 로딩창 구현(LoadingDialog/dialog_loading.xml/loading_anim_rotate)

