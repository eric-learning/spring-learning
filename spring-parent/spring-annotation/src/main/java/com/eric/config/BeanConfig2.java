package com.eric.config;

import com.eric.bean.Color;
import com.eric.bean.Person;
import com.eric.condition.ColorFactoryBean;
import com.eric.condition.MacCondition;
import com.eric.condition.MyImportBeanDefinitionRegistrar;
import com.eric.condition.MyImportSelector;
import org.springframework.context.annotation.*;

/**
 * Description: spring-parent
 *
 * @author Eric.Zhang
 * @date 2021/1/23
 */
@Configuration
@Conditional(MacCondition.class)
@Import({Color.class, MyImportSelector.class, MyImportBeanDefinitionRegistrar.class})
public class BeanConfig2 {

    @Bean("bill")
    public Person person(){
        System.out.println("create a new bean of bill");
        return new Person();
    }

    @Bean("eric")
    @Lazy
    public Person person2(){
        System.out.println("create a new bean of eric");
        return new Person();
    }

    @Bean
    public ColorFactoryBean colorFactoryBean(){
        return new ColorFactoryBean();
    }

}
