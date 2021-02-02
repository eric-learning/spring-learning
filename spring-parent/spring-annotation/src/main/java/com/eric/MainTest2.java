package com.eric;

import com.eric.bean.Person;
import com.eric.config.BeanConfigScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description: spring_parent
 *
 * @author Eric.Zhang
 * @date 2021-1-19
 */
public class MainTest2 {

	public static void main(String[] args) {
		ApplicationContext ac = new AnnotationConfigApplicationContext(BeanConfigScope.class);
		Person person2 = (Person) ac.getBean("person2");
	}
}
