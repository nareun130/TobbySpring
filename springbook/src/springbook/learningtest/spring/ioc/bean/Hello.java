package springbook.learningtest.spring.ioc.bean;

public class Hello {
	String name;
	Printer printer;//Hello 클래스는 Printer인터페이스에 의존

	public String sayHello() {
		return "Hello " + name;// 프로퍼티로 DI 받은 이름을 이용해 간단한 인사문구 만들기

	}

	//DI에 의해 의존 오브젝트로 제공받은 Printer타입의 오브젝트에게 출력작업을 위임.
	//구체적인 출력방식에 상관하지 않음.
	//어떤 방식으로 출력하도록 변경해도 Hello코드는 수정할 필요 없음.
	public void print() { 
		this.printer.print(sayHello());
	}

	public void setPrinter(Printer printer) {//출력을 위해 사용할 Printer 인터페이스를 구현한 오브젝트를 DI 받음.
		this.printer = printer;
	}

	public void setName(String name) {
		this.name = name;
	}

}
