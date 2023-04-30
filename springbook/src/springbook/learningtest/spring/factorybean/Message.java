package springbook.learningtest.spring.factorybean;

//생성자를 제공하지 않는 클래스
public class Message {
	String text;

	private Message(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public static Message newMessage(String text) {
		return new Message(text);
	}

}
