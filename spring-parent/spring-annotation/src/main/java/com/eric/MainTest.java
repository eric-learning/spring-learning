package com.eric;

import com.eric.bean.Person;
import com.eric.config.BeanConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description: spring_parent
 *
 * @author zhangxiusen
 * @date 2021-1-18
 */
public class MainTest {

	public static void main(String[] args) {
		ApplicationContext ac = new AnnotationConfigApplicationContext(BeanConfig.class);
		Person person = ac.getBean(Person.class);
		System.out.println(person.toString());
	}
}
