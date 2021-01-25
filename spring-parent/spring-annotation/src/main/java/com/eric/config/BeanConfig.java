package com.eric.config;

import com.eric.bean.Person;
import com.eric.controller.BookController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

/**
 * Description: spring_parent
 *
 * @author zhangxiusen
 * @date 2021-1-18
 */
// @ComponentScan(value = "com.eric", includeFilters = {
// 		// type: 指定你要过滤进来的规则，是按照扫描注解过滤，还是按照给定的类型过滤，还是按照正则表达式过滤；
// 		// classes: 我们需要spring在扫描时，只包含@Controller注解标识的类
// 		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {BookController.class})
// }, useDefaultFilters = false)
// 可以使用多个@ComponentScan注解，得益于@ComponentScan中的 @Repeatable(ComponentScans.class)
@ComponentScan(value = "com.eric", includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {BookController.class})
}, useDefaultFilters = false)
@ComponentScan(value = "com.eric", includeFilters = {
		// 自定义过滤规则
		@ComponentScan.Filter(type = FilterType.CUSTOM, classes = {MyTypeFilter.class})
}, useDefaultFilters = false)
@Configuration
public class BeanConfig {

	@Bean
	public Person person() {
		return new Person("YingZheng", 28);
	}
}
