package org.printercounter.boot;

import java.io.IOException;

import org.printercounter.service.SearchService;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Startup {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws BeansException, IOException {
		new ClassPathXmlApplicationContext("spring.xml").getBean(SearchService.class).run();
	}

}
