package hello.aop.internalcall;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * 구조를 변경(분리)
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallServiceV3 {

	private final InternalService internalService;



	public void external() {
		log.info("call external");
		internalService.internal();
	}


}
