package springbook.learningtest.spring.ioc.bean;

public class StringPrinter implements Printer {

	private StringBuffer buffer = new StringBuffer();

	@Override
	public void print(String message) {
		this.buffer.append(message);
	}

	@Override
	public String toString() {
		return this.buffer.toString();// 내장 버퍼에 추가해둔 메시지를 스트링으로 가져옴.
	}
}
