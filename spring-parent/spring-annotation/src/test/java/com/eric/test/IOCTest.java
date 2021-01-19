package com.eric.test;

import com.eric.config.BeanConfig;
import com.eric.config.BeanConfigScope;
import com.eric.scope.ThreadScope;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * Description: spring-parent
 *
 * @author zhangxiusen
 * @date 2021-1-18
 */
public class IOCTest {

	/**
	 * 通过注解方式注册bean并获取
	 */
	@Test
	public void test() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(BeanConfig.class);
		String[] beanNames = ac.getBeanDefinitionNames();
		for (String name : beanNames) {
			System.out.println(name);
		}
	}

	/**
	 * 自定义bean的scope（线程级别）
	 */
	@Test
	public void test02() {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(BeanConfigScope.class);
		// 向容器中注册自定义的Scope
		ac.getBeanFactory().registerScope(ThreadScope.THREAD_SCOPE, new ThreadScope());
		// 使用容器获取bean
		for (int i = 0; i < 2; i++) {
			new Thread(() -> {
				System.out.println(Thread.currentThread() + "," + ac.getBean("person3"));
				System.out.println(Thread.currentThread() + "," + ac.getBean("person3"));
			}).start();
		}
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
