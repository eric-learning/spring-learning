package com.eric.config;

import com.eric.bean.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Description: spring_parent
 *
 * @author Eric.Zhang
 * @date 2021-1-19
 */
@Configuration
public class BeanConfigScope {

	/**
	 * Spring容器在启动时，将单例组件实例化之后，会即刻加载到Spring容器中，以后每次从容器中获取组件实例对象时，
	 * 都是直接返回该对象，而不会创建新的对象了。
	 * 需注意线程安全问题
	 * @return
	 */
	@Scope("singleton")
	@Bean("person")
	public Person person() {
		System.out.println("I'm creating an instance named YingZheng");
		return new Person("YingZheng", 28);
	}

	/**
	 * 而多实例组件的实例化是在向Spring容器获取对象时，创建一个新的对象并返回。即每次获取的实例对象都不是同一个对象
	 * 需考虑系统性能影响
	 * @return
	 */
	@Scope("prototype") // 通过@Scope注解来制定该bean的作用范围，也可以说是调整作用域
	@Bean("person2")
	public Person person2() {
		System.out.println("I'm creating an instance named LvBuwei");
		return new Person("LvBuwei", 56);
	}

	@Scope("thread")
	@Bean("person3")
	public Person person3() {
		System.out.println("I'm creating an instance named ZhaoJi");
		return new Person("ZhaoJi", 34);
	}
}
