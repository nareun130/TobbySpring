package springbook;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

//DO 메타정보로 사용될 TestApplicationContext클래스
@Configuration
@ImportResource("/test-applicationContext.xml")
public class TestApplicationContext {
	
}
