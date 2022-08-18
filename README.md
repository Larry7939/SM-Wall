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

2022 - 07 - 19
 - Login,Register Activity -> IdSignin/IdSignup 이름 변경
 - 회원가입 API연동 및 기능 구현 완료
  - idsignup models 구성 - request/response 데이터 클래스(각각 서버에 요청을 보내고 응답을 받기 위한 데이터 클래스)
  - IdSignupRetrofitInterface구성(데이터 전송 방식)
  - IdSignupView구성(Success or Failure 함수 인터페이스)
  - IdSignupService구성
  - 회원가입 시 ID 중복여부나 비밀번호 일치여부 확인 기능 구현해야함. -> 아이디 중복 등의 API를 따로 만들기보다는, 서버에서 보내주는 오류코드로 구분

2022 - 07 - 20
 - 로그인API 연동 및 토큰을 이용한 자동 로그인/로그아웃 기능 구현
 - Splash화면 임시작성(디자인 필요)
 - 로그인 및 회원가입 UI개선
 
2022 - 07 - 28
 - MainActivity위에 ar/search/post/info Fragment 생성 및 BottomNavigationView구현
 - info Fragment로 로그아웃 기능 이동
 - info Fragment Layout 작성

2022 - 07 - 29
 - 로그아웃 팝업 다이얼로그 구현
 
2022 - 07 - 30
 - 갤러리 접근 권한 체크 및
 - Glide로 갤러리에서 로드한 uri -> binding.profile에 set

2022 - 08 - 07
 - 사용자 정보 가져오기 API 구현
 - 헤더를 붙여주기 위해 XAccessTokenInterceptor 수정
 - BitmapConverter클래스 구현(base64ToBitmap)
 - 화면 전환 시 Fragment가 재생성되어 서버 통신 딜레이 발생 -> Fragment null체크 및 add/show/hide 함수 활용하여 해결 -> hide/show 시마다 새로운 Fragment instance를 만들지 않도록 주의!

2022 - 08 - 08
 - InfoFragment에서 갤러리를 실행시키고 돌아오면 Fragment가 화면에 표시되지 않는 이슈 발생 -> MainActivity에서 Fragment를 초기화시키는 initBottomNavigation()을 OnPostResume()에서 실행한 것이 문제였음 -> initBottomNavigation()을 onCreate()옮겨서 해결
 - InfoFragment 코드 정리(함수화)
 
2022 - 08 - 09
 - 프로필 사진 수정 API 구현
 - 이미지를 서버로 보내기 위해 Base64로 인코딩하는 과정에서 두가지 이슈 발생
 - 크기 이슈 : 갤러리에서 받아온 uri를 bitmap으로 변환한 다음 base64로 변환하는 과정에서 request가 너무 길어져서 통신 실패
 - 참조 이슈 : 이미지뷰에서 drawable을 얻어서 bitmap으로 바꾼 다음에 base64로 바꾸는 과정에서 이미지뷰를 얻어오지 못하고 null을 반환해서 실패 -> Bitmap.createScaledBitmap()을 이용해서 사이즈를 조정하고 compress를 이용해서 품질을 조정한 뒤, Base64로 encoding해서 해결

2022 - 08 - 11
 - uri->bitmap->base64 기능 BimapConverter에 함수화
 - arFragment layout cam, map버튼 추가
 - Search Fragment에 네이버 지도 SDK 적용

2022 - 08 - 12
 - Search Fragment의 fragment를 MapView로 변경
 - Search Fragment에서 OnMapReadyCallback를 상속 받아서 onMapReady 함수 오버라이드
 - 건물 표시 및 위도/경도/tilt/zoom 설정
 - Search Fragment map 상단에 검색창 생성
 - edit text 커스텀

2022 - 08 - 13
 - arFragment에 네이버 지도 SDK 적용
 - Search Fragment에서 Ar Fragment로 넘어가면 Search Fragment의 지도가 곂쳐져 보이는 오류 발생
 - MainActivity에서 첫번째 tab 선택 시 FragmentManager.begintransition을 이용하여 Search Fragment를 remove하고 null로 만들어서 해결
 - 대신 search fragment로 이동할 때마다 지도가 새로 그려짐!