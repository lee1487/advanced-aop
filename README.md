# 스프링 핵심 원리 - 고급편 AOP by 인프런 김영한 

## 스프링 AOP 구현 

### 예제 프로젝트 만들기 
```
  AOP를 적용할 예제 프로젝트를 만들어보자. 지금까지 학습 했던 내용과 비슷해서 
  큰 어려움은 없을 것이다. 
  
  OrderRepository
  OrderService
  AopTest
    - AopUtils.isAopProxy(...)을 통해서 AOP 프록시가 적용 되었는지 
	  확인할 수 있다. 현재 AOP 관련 코드를 작성하지 않았으므로 프록시가
	  적용되지 않고, 결과도 false를 반환해야 정상이다. 
	
	- 여기서는 실제 결과를 검증하는 테스트가 아니라 학습 테스트를 진행한다. 
	  앞으로 로그를 직접 보면서 AOP가 잘 작동하는지 확인해볼 것이다. 
	  테스트를 실행해서 잘 동작하면 다음으로 넘어가자
```

### 스프링 AOP 구현1 - 시작 
```
  스프링 AOP를 구현하는 일반적인 방법은 앞서 학습한 @Aspect를 사용하는 방법이다.
  이번 시간에는 @Aspect를 사용해서 가장 단순한 AOP를 구현해보자.  
  
  AspectV1
    - @Around 애노테이션의 값인 execution(* hello.aop.order..*(..))는 
	  포인트컷이 된다. 
	- @Around 애노테이션의 메서드인 doLog는 어드바이스(Advice)가 된다. 
	- execution(* hello.aop.order..*(..))는 hello.aop.order
	  패키지와 그 하위 패키지(..)를 지정하는 AspectJ 포인트컷 표현식이다. 
	  앞으로는 간단히 포인트컷 표현식이라 하겠다. 참고로 포인트컷 표현식은 뒤에서 
	  자세히 설명하겠다. 
	- 이제 OrderService, OrderRepository의 모든 메서드는 AOP의 적용 
	  대상이 된다. 참고로 스프링은 프록시 방식의 AOP를 사용하므로 프록시를 통하는 
	  메서드만 적용 대상이 된다. 
	
	참고 
	  - 스프링 AOP는 AspectJ의 문법을 차용하고, 프록시 방식의 AOP를 제공한다. 
	    AspectJ를 직접 사용하는 것이 아니다. 
	  - 스프링 AOP를 사용할 때는 @Aspect 애노테이션을 주로 사용하는데,
	    이 애노테이션도 AspectJ가 제공하는 애노테이션이다.
	
	  - @Aspect를 포함한 org.aspectj 패키지 관련 기능은 aspectjweaver.jar
	    라이브러리가 제공하는 기능이다. 앞서 build.gradle에 spring-boot-starter-aop
		를 포함했는데, 이렇게 하면 스프링의 AOP 관련 기능과 함께 aspectjweaver.jar도
		함께 사용할 수 있게 의존 관계에 포함된다. 그런데 스프링에서는 AspectJ가 
		제공하는 애노테이션이나 관련 인터페이스만 사용하는 것이고, 실제 AspectJ가 
		제공하는 컴파일, 로드타임 위버 등을 사용하는 것은 아니다. 스프링은 지금까지 
		우리가 학습한 것 처럼 프록시 방식의 AOP를 사용한다.

  AopTest - 추가
    - @Aspect는 애스펙트라는 표식이지 컴포넌트 스캔이 되는 것은 아니다. 따라서 
	  AspectV1을 AOP로 사용하려면 스프링 빈으로 등록해야 한다. 
	
	- 스프링 빈으로 등록하는 방법은 다음과 같다 
	  - @Bean을 사용해서 직접 등록 
	  - @Component 컴포넌트 스캔을 사용해서 자동 등록 
	  - @Import 주로 설정 파일을 추가할 때 사용(@Configuration)
	
	- @Import는 주로 설정 파일을 추가할 때 사용하지만, 이 기능으로 스프링 빈도 
	  등록할 수 있다. 테스트에서는 버전을 올려가면서 변경할 예정이어서 간단하게 
	  @Import 기능을 사용하자 
	
	- AopTest에 @Import(AspectV1.class)로 스프링 빈을 추가했다. 
	- AopUtils.isAopProxy(...)도 프록시가 적용되었으므로 true를 반환한다.
```

### 스프링 AOP 구현2 - 포인트컷 분리 
```
  @Around에 포인트컷 표현식을 직접 넣을 수 도 있지만, @Pointcut 애노테이션을 사용해서 
  별도로 분리할 수 도 있다.
  
  AspectV2
    - @Pointcut
	  - @Pointcut에 포인트컷 표현식을 사용한다. 
	  - 메서드 이름과 파라미터를 합쳐서 포인트컷 시그니처(signature)라 한다. 
	  - 메서드의 반환 타입은 void여야 한다. 
	  - 코드 내용은 비워둔다. 
	  - 포인트컷의 시그니처는 allOrder()이다. 이름 그대로 주문과 관련된 모든 기능을 
	    대상으로 하는 포인트컷이다. 
	  - @Around 어드바이스에서는 포인트컷을 직접 지정해도 되지만, 포인트컷 시그니처를 
	    사용해도 된다. 여기서는 @Around("allOrder()")를 사용한다. 
	  - private, public 같은 접근 제어자는 내부에서만 사용하면 private를 
	    사용해도 되지만 다른 애스펙트에서 참고하려면 public을 사용해야 한다. 
	
	  - 결과적으로 AspectV1과 같은 기능을 수행한다. 이렇게 분리하면 하나의 포인트컷 
	    표현식을 여러 어드바이스에서 함께 사용할 수 있다. 그리고 뒤에 설명하겠지만 
		다른 클래스에 있는 외부 어드바이스에서도 포인트컷을 함께 사용할 수 있다. 

  AopTest - 수정
    - AspectV2를 실행하기 위해서 다음 처리를 하자 
	  - @Import(AspectV1.class) 주석 처리 
	  - @Import(AspectV2.class) 추가 
	- 실행해보면 이전과 동일하게 동작하는 것을 확인할 수 있다. 
```

### 스프링 AOP 구현3 - 어드바이스 추가 
```
  이번에는 조금 복잡한 예제를 만들어보자. 
  앞서 로그를 출력하는 기능에 추가로 트랜잭션을 적용하는 코드도 추가해보자. 여기서는 진짜 
  트랜잭션을 실행하는 것은 아니다. 기능이 동작한 것 처럼 로그만 남기겠다. 
  
  트랜잭션 기능은 보통 다음과 같이 동작한다. 
    - 핵심 로직 실행 직전에 트랜잭션을 시작
	- 핵심 로직 실행 
	- 핵심 로직 실행에 문제가 없으면 커밋 
	- 핵심 로직 실행에 예외가 발생하면 롤백 

  AspectV3
    - allOrder() 포인트컷은 hello.aop.order 패키지와 하위 패키지를 대상으로 한다
	- allService() 포인트컷은 타입 이름 패턴이 *Service 를 대상으로 하는데 쉽게 이야기해서
	  XxxService 처럼 Service 로 끝나는 것을 대상으로 한다. *Servi* 과 같은 패턴도 가능하다.
	- 여기서 타입 이름 패턴이라고 한 이유는 클래스, 인터페이스에 모두 적용되기 때문이다.
	
	@Around("allOrder() && allService()")
	- 포인트컷은 이렇게 조합할 수 있다. && (AND), || (OR), ! (NOT) 3가지 조합이 가능하다
	- hello.aop.order 패키지와 하위 패키지 이면서 타입 이름 패턴이 *Service 인 것을 대상으로 한다.
	- 결과적으로 doTransaction() 어드바이스는 OrderService 에만 적용된다.
	- doLog() 어드바이스는 OrderService , OrderRepository 에 모두 적용된다.

  포인트것이 적용된 AOP 결과는 다음과 같다
    - orderService : doLog() , doTransaction() 어드바이스 적용
	- orderRepository : doLog() 어드바이스 적용

  AopTest - 수정
    - AspectV3 를 실행하기 위해서 다음 처리를 하자.
	- @Import(AspectV2.class) 주석 처리
	- @Import(AspectV3.class) 추가

  전체 실행 순서를 분석해보자.
  
  AOP 적용 전
   클라이언트 -> orderService.orderItem() -> orderRepository.save()
  
  AOP 적용 후
   클라이언트 -> [ doLog() -> doTransaction() ] -> orderService.orderItem()
   -> [ doLog() ] -> orderRepository.save()
   
   - orderService 에는 doLog() , doTransaction() 두가지 어드바이스가 적용되어 있고,
     orderRepository 에는 doLog() 하나의 어드바이스만 적용된 것을 확인할 수 있다.

  실행 - exception()
    - 예외 상황에서는 트랜잭션 커밋 대신에 트랜잭션 롤백이 호출되는 것을 확인할 수 있다.
	- 그런데 여기에서 로그를 남기는 순서가 [doLog() -> doTransaction()] 순서로 작동한다. 
	  만약 어드바이스가 적용되는 순서를 변경하고 싶으면 어떻게 하면 될까? 예를 들어서 실행 시간을 
	  측정해야 하는데 트랜잭션과 관련된 시간을 제외하고 측정하고 싶다면 doTransaction()
	  -> doLog() 이렇게 트랜잭션 이후에 로그를 남겨야 할 것이다. 
	  

  그 전에 잠깐 포인트컷을 외부로 빼서 사용하는 방법을 먼저 알아보자.
```

### 스프링 AOP 구현4 - 포인트컷 참조 
```
  다음과 같이 포인트컷을 공용으로 사용하기 위해 별도의 외부 클래스에 모아두어도 된다. 참고로 외부에서 
  호출할 때는 포인트컷의 접근 제어자를 public으로 열어두어야 한다. 
  
  Pointcuts
    - orderAndService(): allOrder() 포인트컷과 allService() 포인트컷을 조합해서 
	  새로운 포인트컷을 만들엇다.
	  
  AspectV4Pointcut 
    - 사용하는 방법은 패키지명을 포함한 클래스 이름과 포인트컷 시그니처를 모두 지정하면 된다.
	  포인트컷을 여러 어드바이스에서 함께 사용할 때 이 방법을 사용하면 효과적이다.
``` 

### 스프링 AOP 구현5 - 어드바이스 순서 
```
  어드바이스는 기본적으로 순서를 보장하지 않는다. 순서를 지정하고 싶으면 @Aspect 적용 단위로 
  org.springframework.core.annotation.@Order 애노테이션을 적용해야 한다. 
  문제는 이것을 어드바이스 단위가 아니라 클래스 단위로 적용할 수 있다는 점이다. 그래서 지금처럼 
  하나의 애스펙트에 여러 어드바이스가 있으면 순서를 보장 받을 수 없다. 따라서 애스펙트를 
  별도의 클래스로 분리해야 한다. 
  
  현재 로그를 남기는 순서가 아마도 [doLog() -> doTransaction()] 이 순서로 남을 것이다. 
  (참고로 이 순서로 실행되지 않는 분도 있을 수 있다. JVM이나 실행 환경에 따라 달라질 수도 있다.)
  
  로그를 남기는 순서를 바꾸어서 [doTransaction() -> doLog()] 트랜잭션이 먼저 처리되고, 
  이후에 로그가 남도록 변경해보자. 
  
  AspectV5Order
    - 하나의 애스팩트 안에 있던 어드바이스를 LogAspect, TxAspect 애스팩트로 각각 분리했다. 
	  그리고 각 애스팩트에 @Order 어노테이션을 통해 실행 순서를 적용했다. 
	  참고로 숫자가 작을 수록 먼저 실행된다.
```

### 스프링 AOP 구현6 - 어드바이스 종류
```
  어드바이스는 앞서 살펴본 @Around외에도 여러가지 종류가 있다. 
  
  어드바이스 종류 
    - @Around: 메서드 호출 전후에 수행, 가장 강력한 어드바이스, 조인 포인트 실행 여부 선택, 
	  반환 값 변환, 예외 변환 등이 가능 
	- @Before: 조인 포인트 실행 이전에 실행 
	- @AfterReturning: 조인 포인트가 정상 완료 후 실행 
	- @AfterThrowing: 메서드가 예외를 던지는 경우 실행 
	- @After: 조인 포인트가 정상 또는 예외에 관계없이 실행(finally)

  예제를 만들면서 학습해보자.
  
  AspectV6Advice
    - doTransaction() 메서드에 남겨둔 주석을 보자. 
	  복잡해 보이지만 사실 @Around를 제외한 나머지 어드바이스들은 @Around가 할 수 있는 일의 
	  일부만 제공할 뿐이다. 따라서 @Around 어드바이스만 사용해도 필요한 기능을 모두 수행할 수 있다. 
	  
	참고 정보 획득 
	  - 모든 어드바이스는 org.aspectj.lang.JoinPoint를 첫번째 파라미터에 사용할 수 있다.
	    (생략해도 된다.)
	  - 단 @Around는 ProceedingJoinPoint를 사용해야 한다. 
	  - 참고로 ProceedingJoinPoint는 org.aspectj.lang.JoinPoint의 하위 타입이다.
	  
	JoinPoint 인터페이스의 주요 기능
	  - getArgs(): 메서드 인수를 반환합니다.
	  - getThis(): 프록시 객체를 반환합니다.
	  - getTarget(): 대상 객체를 반환합니다.
	  - getSignature(): 조언되는 메서드에 대한 설명을 반환합니다.
	  - toString(): 조언되는 방법에 대한 유용한 설명을 인쇄합니다. 
	  
	ProceedingJoinPoint 인터페이스의 주요 기능
	  - proceed(): 다음 어드바이스나 타켓을 호출한다. 
	  - 추가로 호출시 전달한 매개변수를 파라미터를 통해서도 전달 받을 수도 있는데, 이부분은 뒤에서 설명한다. 

  어드바이스 종류 
    @Before 
	  - 조인 포인트 실행 전 
	  - @Around와 다르게 작업 흐름을 변경할 수는 없다. 
	  - @Around는 ProceedingJoinPoint.proceed()를 호출해야 다음 대상이 호출된다. 
	    만약 호출하지 않으면 다음 대상이 호출되지 않는다. 반면에 @Before는 
		ProceedingJoinPoint.proceed() 자체를 사용하지 않는다. 메서드 종료시 자동으로 
		다음 타켓이 호출된다. 물론 예외가 발생하면 다음 코드가 호출되지 않는다.
	@AfterReturning
	  - 메서드 실행이 정상적으로 반환될 때 실행 
	  - returning 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다. 
	  - returning 절에 지정된 타입의 값을 반환하는 메서드만 대상으로 실행한다.
	    (부모 타입을 지정하면 모든 자식 타입은 인정된다.)
	  - @Around와 다르게 반환되는 객체를 변경할 수는 없다. 반환 객체를 변경하려면 @Around를 
	    사용해야 한다. 참고로 반환 객체를 조작할 수는 있다. 
	@AfterThrowing
	  - 메서드 실행이 예외를 던져서 종료될 때 실행 
	  - throwing 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다. 
	  - throwing 절에 지정된 타입과 맞은 예외를 대상으로 실행한다.
	    (부모 타입을 지정하면 모든 자식 타입은 인정된다.)
	@After
	  - 메서드 실행이 종료되면 실행된다.(finally를 생각하면 된다.)
	  - 정상 및 예외 반환 조건을 모두 처리한다.
	  - 일반적으로 리소스를 해제하는 데 사용한다 .
	@Around
	  - 메서드의 실행의 주변에서 실행된다. 메서드 실행 전후에 작업을 수행한다. 
	  - 가장 강력한 어드바이스
	    - 조인 포인트 실행 여부 선택 joinPoint.proceed() 호출 여부 선택 
		- 전달 값 변환: joinPoint.proceed(args[])
		- 반환 값 변환 
		- 예외 변환
		- 트랜잭션 처럼 try ~ catch ~ finally 모두 들어가는 구문 처리 가능 
	  - 어드바이스의 첫 번째 파라미터는 ProceedingJoinPoint를 사용해야 한다. 
	  - proceed()를 통해 대상을 실행한다.
	  - proceed()를 여러변 실행할 수도 있음(재시도)

  AopTest - 변경
    순서 
	  - 스프링은 5.2.7 버전부터 동일한 @Aspect안에서 동일한 조인포인트의 우선순위를 정했다. 
	  - 실행 순서: @Around, @Before, @After, @AfterReturning, @AfterThrowing
	  - 어드바이스가 적용되는 순서는 이렇게 적용되지만, 호출 순서와 리턴 순서는 반대라는 점을 알아두자. 
	  - 물론 @Aspect안에 동일한 종류의 어드바이스가 2개 있으면 순서가 보장되지 않는다. 이 경우 앞서 
	    배운 것 처럼 @Aspect를 분리하고 @Order를 적용하자.

  @Around 외에 다른 어드바이스가 존재하는 이유
    - @Around 하나만 있어도 모든 기능을 수행할 수 있다. 그런데 다른 어드바이스들이 존재하는
	  이유는 무엇일까?
	
	다음 코드를 보자 
	
	@Around("hello.aop.order.aop.Pointcuts.orderAndService()")
	public void doBefore(ProceedingJoinPoint joinPoint) {
	 log.info("[before] {}", joinPoint.getSignature());
	}
	
	이 코드의 문제점을 찾을 수 있겠는가? 이 코드는 타켓을 호출하지 않는 문제가 있다. 
	이 코드를 개발한 의도는 타켓 실행 전에 로그를 출력하는 것이다. 그런데 @Around는 항상 
	joinPoint.proceed()를 호출해야 한다. 만약 실수로 호출하지 않으면 타켓이
	호출되지 않는 치명적인 버그가 발생한다.
	
	다음 코드를 보자 
	
	@Before("hello.aop.order.aop.Pointcuts.orderAndService()")
	public void doBefore(JoinPoint joinPoint) {
	 log.info("[before] {}", joinPoint.getSignature());
	}
	
	@Before는 joinPoint.proceed()를 호출하는 고민을 하지 않아도 된다. 
	
	@Around가 가장 넓은 기능을 제공하는 것은 맞지만, 실수할 가능성이 있다. 반면에 
	@Before, @After 같은 어드바이스는 기능은 적지만 실수할 가능성이 낮고, 
	코드도 단순한다. 그리고 가장 중요한 점이 있는데, 바로 이 코드를 작성한 의도가 
	명확하게 들어난다는 점이다. @Before라는 애노테이션을 보는 순간 아~ 이 코드는 
	타켓 실행 전에 한정해서 어떤 일을 하는 코드구나 라는 것이 들어난다.
	
  좋은 설계는 제약이 있는 것이다.
    - 좋은 설계는 제약이 있는 것이다. @Around만 있으면 되는데 왜? 이렇게 제약을 두는가?
	  제약은 실수를 미연에 방지한다. 일종에 가이드 역할을 한다. 만약 @Around를 사용했는데, 
	  중간에 다른 개발자가 해당 코드를 수정해서 호출하지 않았다면? 큰 장애가 발생했을 것이다. 
	  처음부터 @Before를 사용했다면 이런 문제 자체가 발생하지 않는다. 
	- 제약 덕분에 역할이 명확해진다. 다른 개발자도 이 코드를 보고 고민해야 하는 범위가 줄어들고 
	  코드의 의도도 파악하기 쉽다.
``` 

## 스프링 AOP - 포인트컷 

### 포인트컷 지시자 
```
  포인트컷 표현식은 execution같은 포인트컷 지시자(Pointcut Designator)로 시작한다.
  줄여서 PCD라 한다. 
  
  포인트컷 지시자의 종류 
    - execution: 메소드 실행 조인 포인트를 매칭한다. 스프링 AOP에서 가장 많이 사용하고, 
	  기능도 복잡하다.
	- within: 특정 타입 내의 조인 포인트를 매칭한다. 
	- args: 인자가 주어진 타입의 인스턴스인 조인 포인트 
	- this: 스프링 빈 객채(스프링 AOP 프록시)를 대상으로 하는 조인 포인트 
	- target: Target 객체(스프링 AOP 프록시가 가르키는 실제 대상)를 대상으로 하는 조인 포인트 
	- @target: 실행 객체의 클래스에 주어진 타입의 애노테이션이 있는 조인 포인트 
	- @within: 주어진 애노테이션이 있는 타입 내 조인 포인트 
	- @annotation: 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭 
	- @args: 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트 
	- bean: 스프링 전용 포인트컷 지시자, 빈의 이름으로 포인트컷을 지정한다.

  포인트컷 지시자가 무엇을 뜻하는지, 사실 글로만 읽어보면 이해하기 쉽지 않다. 예제를 통해서 
  하나씩 이해해보자.
  
  execution은 가장 많이 사용하고, 나머지는 자주 사용하지 않는다. 따라서 execution을
  중점적으로 이해하자.
```

### 예제 만들기 
```
  포인트컷 표현식을 이해하기 위해 예제 코드를 하나 추가하자. 
  
  ClassAop
  MethodAop
  MemberService
  MemberServiceImpl
  ExecutionTest
    - AspectJExpressionPointcut이 바로 포인트컷 표현식을 처리해주는 클래스다.
	  여기에 포인트컷 표현식을 지정하면 된다. AspectJExpressionPointcut는 
	  상위에 Pointcut 인터페이스를 가진다. 
	- printMethod() 테스트는 MemberServiceImpl.hello(String)
	  메서드의 정보를 출력해준다.

  실행 결과 
    - 이번에 알아볼 execution으로 시작하는 포인트컷 표현식은 이 메서드 정보를 
	  매칭해서 포인트컷 대상을 찾아낸다.
```

### execution - 1
```
  execution 문법 
    execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?
	name-pattern(param-pattern) throws-pattern?)
	
	execution(접근제어자? 반환타입 선언타입?메서드이름(파라미터) 예외?)
	
	- 메소드 실행 조인 포인트를 매칭한다. 
	- ?는 생략할 수 있다. 
	- * 같은 패턴을 지정할 수 있다.
	
  실제 코드를 하나씩 보면서 execution을 이해해보자. 
  
  가장 정확한 포인트컷 
    먼저 MemberServiceImpl.hello(String)메서드와 가장 정확하게 모든 내용이 
	매칭되는 표현식이다. 
	  - execution(public String hello.aop.member.MemberServiceImpl.hello(String))
  ExecutionTest - 추가
    - AspectJExpressionPointcut에 pointcut.setExpression을 통해서 포인트컷 표현식을 
	  적용할 수 있다. 
	- pointcut.matches(메서드, 대상 클래스)를 실행하면 지정한 포인트컷 표현식의 매칭 여부를 
	  true, false로 반환한다.
	  
	매칭 조건 
	  - 접근 제어자?: public 
	  - 반환타입: String 
	  - 선언타입?: hello.aop.member.MemberServiceImpl
	  - 메서드이름: hello
	  - 파라미터: (String)
	  - 예외?: 생략 
	
	- MemberServiceImpl.hello(String)메서드와 포인트컷 표현식의 모든 내용이 
	  정확하게 일치한다. 따라서 true를 반환한다.

  가장 많이 생략한 포인트컷
    - execution(* *(..))
	
	매칭 조건 
	  - 접근 제어자?: 생략 
	  - 반환 타입: *
	  - 선언타입?: 생략 
	  - 메서드이름: *
	  - 파라미터(..)
	  - 예외?: 없음 
	
	- *는 아무 값이 들어와도 된다는 뜻이다.
	- 파라미터에서 ..은 파라미터의 타입과 파라미터 수가 상관없다는 뜻이다. 
	  (0..*) 파라미터는 뒤에 자세히 정리하겠다.
	  

  메서드 이름 매칭 관련 포인트컷
    - 메서드 이름 앞 뒤에 *을 사용해서 매칭할 수 있다. 

  패키지 매칭 관련 포인트컷
    - hello.aop.member.*(1).*(2)
	  - (1): 타입 
	  - (2): 메서드 이름 
	- 패키지에서 ., ..의 차이를 이해해야 한다. 
	  - .: 정확하게 해당 위치의 패키지 
	  - ..: 해당 위치의 패키지와 그 하위 패키지도 포함 
```

### execution - 2
```
  타입 매칭 - 부모 타입 허용 
   execution(* hello.aop.member.MemberServiceImpl.*(..))
     - typeExactMatch()는 타입 정보가 정확하게 일치하기 때문에 매칭된다. 

   execution(* hello.aop.member.MemberService.*(..))
     - typeMatchSuperType()을 주의해서 보아야 한다. 
	 - execution에서는 MemberService처럼 부모 타입을 선언해도 
	   그 자식 타입은 매칭된다. 다형성에서 부모타입 = 자식타입 이 할당 
	   가능하다는 점을 떠올려보면 된다. 

  타입 매칭 - 부모 타입에 있는 메서드만 허용 
    execution(* hello.aop.member.MemberServiceImpl.*(..))
	  - typeMatchInternal()의 경우 MemberServiceImpl를 표현식에 
	    선언했기 때문에 그 안에 있는 internal(String) 메서드도 매칭 
		대상이 된다. 
	execution(* hello.aop.member.MemberService.*(..))
	  - typeMatchNoSuperTypeMethodFalse()를 주의해서 보아야 한다. 
	  - 이 경우 표현식에 부모 타입인 MemberService를 선언했다. 그런데 
	    자식 타입인 MemberServiceImpl의 internal(String)
		메서드를 매칭하려 한다. 이 경우 매칭에 실패한다. MemberService에는 
		internal(String) 메서드가 없다. 
	  - 부모 타입을 표현식에 선언한 경우 부모 타입에서 선언한 메서드가 자식 타입에 
	    있어야 매칭에 성공한다. 그래서 부모 타입에 있는 hell(String) 메서드는
		매칭에 성공하지만, 부모 타입에 없는 internal(String)은 매칭에 실패한다.

  파라미터 매칭 
    - execution 파라미터 매칭 규칙은 다음과 같다. 
	  - (String): 정확하게 String 타입 파라미터 
	  - (): 파라미터가 없어야 한다. 
	  - (*): 정확히 하나의 파라미터, 단 모든 타입을 허용한다.
	  - (*,*): 정확히 두 개의 파라미터, 단 모든 타입을 허용한다. 
	  - (..): 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다. 
	    참고로 파라미터가 없어도 된다. 0..*로 이해하면 된다. 
	  - (String, ..): String 타입으로 시작해야 한다. 숫자와 무관하게 
	    모든 파라미터, 모든 타입을 허용한다.
		- 예) (String), (String, Xxx), (String, Xxx, Xxx) 허용 
```

### within
```
  within 지시자는 특정 타입 내의 조인 포인트에 대한 매칭을 제한한다. 쉽게 이야기해서 
  해당 타입이 매칭되면 그 안의 메서드(조인 포인트)들이 자동으로 매칭된다. 
  문법은 단순한데 execution에서 타입 부분만 사용한다고 보면 된다.
  
  WithinTest
	주의 
	  그런데 within 사용시 주의해야 할 점이 있다. 표현식에 부모 타입을 지정하면 
	  안된다는 점이다. 정확하게 타입이 맞아야 한다. 이부분에서 execution과 차이가 난다.

  WithinTest - 추가 
    - 부모 타입(여기서는 MemberService인터페이스)지정시 within은 실패하고, 
	  execution은 성공하는 것을 확인할 수 있다.
```

### args 
```
  - args: 인자가 주어진 타입의 인스턴스인 조인 포인트로 매칭 
  - 기본 문법은 execution의 args부분과 같다. 
  
  execution과 args의 차이점 
    - execution은 파라미터 타입이 정확하게 매칭되어야 한다. execution은 
	  클래스에 선언된 정보를 기반으로 판단한다. 
	- args는 부모 타입을 허용한다. args는 실제 넘어온 파라미터 
	  객체 인스턴스를 보고 판단한다

  ArgsTest
    - pointcut(): AspectJExpressionPointcut에 포인트컷은 
	  한번만 지정할 수 있다. 이번 테스트에서는 테스트를 편리하게 진행하기 위해 
	  포인트컷을 여러번 지정하기 위해 포인트컷 자체를 생성하는 메서드를 만들었다. 
	- 자바가 기본으로 제공하는 String은 Object, java.io.Serializable의 
	  하위 타입이다. 
	- 정적으로 클래스에 선언된 정보만 보고 판단하는 execution(* *(Object))는 
	  매칭에 실패한다. 
	- 동적으로 실제 파라미터로 넘어온 객체 인스턴스로 판단하는 args(Object)는 
	  매칭에 성공한다. (부모 타입 허용) 
	
	참고 
	  - args 지시자는 단독으로 사용되기 보다는 뒤에서 설명할 파라미터 바인딩에서 
	    주로 사용된다.
```

### @target, @within 
```
  정의 
    - @target: 실행 객체의 클래스에 주어진 타입의 애노테이션이 있는 조인 포인트 
	- @within: 주어진 애노테이션이 있는 타입 내 조인 포인트 

  설명 
    - @target, @within은 다음과 같이 타입에 있는 애노테이션으로 
	  AOP 적용 여부를 판단한다. 
	  - @target(hello.aop.member.annotation.ClassAop)
	  - @within(hello.aop.member.annotation.ClassAop)
	  
  @target vs @within 
    - @target은 인스턴스의 모든 메서드를 조인 포인트로 적용한다.
	- @within은 해당 타입 내에 있는 메서드만 조인 포인트로 적용한다. 
	
	- 쉽게 이야기해서 @target은 부모 클래스의 메서드까지 어드바이스를 
	  다 적용하고, @within은 자기 자신의 클래스에 정의된 메서드에만 
	  어드바이스를 적용한다.

  AtTargetAtWithinTest
    - parentMethod()는 Parent 클래스에만 정의되어 있고, Child 클래스에 
	  정의되어 있지 않기 때문에 @within에서 AOP 적용 대상이 되지 않는다. 
	- 실행결과를 보면 child.parentMethod()를 호출 했을 때 [@within]이 
	  호출되지 않은 것을 확인할 수 있다. 
	
	참고 
	  - @target, @within 지시자는 뒤에서 설명할 파라미터 바인딩에서 함께 사용된다. 
	
	주의 
	  - 다음 포인트컷 지시자는 단독으로 사용하면 안된다. args, @args, @target 
	  - 이번 예제를 보면 execution(* hello.aop..*(..))를 통해 적용 대상을 
	    줄여준 것을 확인할 수 있다. args, @args, @target은 실제 객체 인스턴스가 
		생성되고 실행될 때 어드바이스 적용 여부를 확인할 수 있다. 
	  - 실행 시점에 일어나는 포인트컷 적용 여부도 결국 프록시가 있어야 실행 시점에
	    판단할 수 있다. 프록시가 없다면 판단 자체가 불가능하다. 그런데 스프링 컨테이너가 
		프록시를 생성하는 시점은 스프링 컨테이너가 만들어지는 애플리케이션 로딩 시점에 
		적용할 수 있다. 따라서 args, @args, @target같은 포인트컷 지시자가 있으면 
		스프링은 모든 스프링 빈에 AOP를 적용하려고 시도한다. 앞서 설명한 것 처럼 프록시가 
		없으면 실행 시점에 판단 자체가 불가능하다. 
	  - 문제는 이렇게 모든 스프링 빈에 AOP 프록시를 적용하려고 하면 스프링이 내부에서 사용하는 
	    빈 중에는 final로 지정된 빈들도 있기 때문에 오류가 발생할 수 있다. 
	  - 따라서 이러한 표현식은 최대한 프록시 적용 대상을 축소하는 표현식과 함께 사용해야 한다.
```

### @annotation, @args
```
  @annotation 
    정의 
	  - @annotation: 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭 
	
	설명 
	  - @annotation(hello.aop.member.annotation.MethodAop)
	  
	  다음과 같이 메서드(조인 포인트에) 애노테이션이 있으면 매칭한다.
	  public class MemberServiceImpl {
		@MethodAop("test value")
		public String hello(String param) {
			return "ok";
		}
	  }

  @args 
    정의 
	  - @args: 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트 
	설명 
	  - 전달된 인수의 런타임 타입에 @check 애노테이션이 있는 경우에 매칭한다.
	  @args(test.Check)
```

### bean 
```
  bean 
    정의 
	  - bean: 스프링 전용 포인트컷 지시자, 빈의 이름으로 지정한다 
	
	설명 
	  - 스프링 빈의 이름으로 AOP 적용 여부를 지정한다. 이것은 스프링에서만 사용할 수 있는 
	    특별한 지시자이다. 
	  - bean(orderService) || bean(*Repository)
	  - *과 같은 패턴을 사용할 수 있다.
```