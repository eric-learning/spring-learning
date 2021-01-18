package com.eric.config;

import com.eric.bean.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description: spring_parent
 *
 * @author zhangxiusen
 * @date 2021-1-18
 */
@Configuration
public class BeanConfig {

	@Bean
	public Person person(){
		return new Person("YingZheng", 28);
	}
}
