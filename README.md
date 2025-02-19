# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* InputStream 을 통해 Http 요청 정보 가지고 있다.
* BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
  * BufferedReader 를 통해 InputStream을 읽어올 수 있다.
* try() -> () 에서 할당된 자원들은 try 절이 끝나면 자원을 반납한다.
* byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
  * 요청 url의 파일을 바이트 단위로 읽어오는 방법

### 요구사항 2 - get 방식으로 회원가입
* String.startWith(String prefix) - prefix 로 시작하는지 확인하는 메소드

### 요구사항 3 - post 방식으로 회원가입
* 완료

### 요구사항 4 - redirect 방식으로 이동
* Http 응답 헤더의 Location 의 경로에 따라 redirect 함

### 요구사항 5 - cookie
* 완료

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 