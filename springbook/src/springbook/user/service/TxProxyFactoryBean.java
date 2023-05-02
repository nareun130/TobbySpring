package springbook.user.service;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

//JDK 다이내믹 프록시의 구조
public class TxProxyFactoryBean implements FactoryBean<Object> {

	Object target;
	PlatformTransactionManager transactionManager;
	String pattern;
	Class<?> serviceInterface;

	public void setTarget(Object target) {
		this.target = target;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	// FactoryBean 인터페이스 구현 메소
	@Override
	public Object getObject() throws Exception {
		// DI 받은 정보를 이용해서 TransactionHandler를 사용하는 다이내믹 프록시를 생성
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(target);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern(pattern);
		return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { serviceInterface }, txHandler);

	}

	@Override
	public Class<?> getObjectType() {
		// 팩토리 빈이 생성하는 오브젝트 타입은 DI 받은 인터페이스 타입에 따라 달라짐. -> 다양한 타입의 프록시 오브젝트 생성에 재사용 가능
		return serviceInterface;
	}
	
	@Override
	public boolean isSingleton() {
		// 싱글톤 빈이 아니라는 뜻이 아님. -> getObject()가 매번 같은 오브젝트를 리턴하지 않는다는 뜻.
		return false;
	}

}
