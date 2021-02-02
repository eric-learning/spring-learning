package com.eric;

import com.eric.bean.Person;
import com.eric.config.BeanConfig;
import com.eric.config.BeanConfigOfPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description: spring_parent
 *
 * @author Eric.Zhang
 * @date 2021-1-18
 */
public class MainTest {

	public static void main(String[] args) {
		ApplicationContext ac = new AnnotationConfigApplicationContext(BeanConfigOfPropertyValues.class);
		Person person = ac.getBean(Person.class);
		System.out.println(person.toString());
		String[] names = ac.getBeanNamesForType(Person.class);
		for (String name : names){
			System.out.println(name);
		}
	}

}
