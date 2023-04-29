package springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler{

	Hello target;
	
	//다이내믹 프록시로부터 전달받은 요청을 다시 타깃 오브젝트에 위임해야 하므로 타깃 오브젝트를 주입받아 둔다.
	public UppercaseHandler(Hello target) {
		this.target = target;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String ret = (String)method.invoke(target, args);
		return ret.toUpperCase();// 부가기능 제공
	}

}
