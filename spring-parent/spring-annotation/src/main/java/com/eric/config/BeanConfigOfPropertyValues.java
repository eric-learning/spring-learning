package com.eric.config;

import com.eric.bean.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Description: spring-parent
 * 用@PropertySource(读取外部配置文件中的k/v保存到运行的环境变量中（springboot会默认读取application.properties文件，
 * springcloud会默认读取bootstrap.properties文件）；加载完外部的配置文件以后使用${}取出配置文件的值
 * @author Eric.Zhang
 * @date 2021/1/23
 */
@Configuration
@PropertySource(value = {"classpath:/application.properties"})
public class BeanConfigOfPropertyValues {

    @Bean
    public Person person(){
        return new Person();
    }
}
