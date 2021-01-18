package com.eric.test;

import com.eric.config.BeanConfig;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description: spring-parent
 *
 * @author zhangxiusen
 * @date 2021-1-18
 */
public class IOCTest {

	@Test
	public void test(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(BeanConfig.class);
		String[] beanNames = ac.getBeanDefinitionNames();
		for (String name : beanNames){
			System.out.println(name);
		}
	}
}
