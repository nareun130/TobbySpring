package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	public Integer calcSum(String filepath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		Integer sum = 0;
		String line = null;
		while ((line = br.readLine()) != null) {
			sum += Integer.valueOf(line);
		}
		br.close();// 한 번 파일을 열면 반드시 닫아준다.
		return sum;
	}

}
