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