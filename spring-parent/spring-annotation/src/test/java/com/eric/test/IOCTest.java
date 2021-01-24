package com.eric.test;

import com.eric.bean.Boss;
import com.eric.bean.Car;
import com.eric.bean.Color;
import com.eric.bean.Red;
import com.eric.config.BeanConfig;
import com.eric.config.BeanConfig2;
import com.eric.config.BeanConfigOfAutowired;
import com.eric.service.BookService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Calendar;

/**
 * Description: spring-parent
 *
 * @author zhangxiusen
 * @date 2021-1-18
 */
public class IOCTest {

    @Test
    public void test() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(BeanConfig.class);
        String[] beanNames = ac.getBeanDefinitionNames();
        for (String name : beanNames) {
            System.out.println(name);
        }
    }

    @Test
    public void testLazy() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(BeanConfig2.class);
        ac.getBean("eric");
        ac.getBean("eric");
        ac.getBean("bill");
    }

    /**
     * 给容器中注册组件：
     * 1、包扫描+组件标注注解（@@ComponentScan + Controller/@Service/@Repository/@Component）
     * 2、@Bean[导入的第三方包里面的组件]
     * 3、@Import[快速给容器中导入一个组件]
     * 1)、@Import(要导入容器中的组件)；容器中就会自动注册这个组件，id默认是全类名
     * 2)、ImportSelector：返回需要导入的组件的全类名数组
     * 3)、ImportSelectorDefinitionRegistrar：手动注册bean到容器中
     * 4、调用FactoryBean创建Spring定义的bean
     * 1)、默认获取到的是工厂bean调用getObject创建的对象
     * 2)、要获取工厂bean本身，需要在id前面加一个&：&colorFactoryBean
     */
    @Test
    public void testCreateBean() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(BeanConfig2.class);
        String[] beanNames = ac.getBeanDefinitionNames();
        for (String name : beanNames) {
            System.out.println(name);
        }

        // 工厂bean获取的是调用betObject创建的对象
        Object bean2 = ac.getBean("colorFactoryBean");
        System.out.println("bean2的类型：" + bean2.getClass());
        Object bean3 = ac.getBean("&colorFactoryBean");
        System.out.println("bean3的类型：" + bean3.getClass());
    }

    @Test
    public void testAutowired() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(BeanConfigOfAutowired.class);
        BookService bookService = ac.getBean(BookService.class);
        System.out.println(bookService);

        Boss boss = ac.getBean(Boss.class);
        System.out.println(boss);
        Car car = ac.getBean(Car.class);
        System.out.println(car);

        Color color = ac.getBean(Color.class);
        System.out.println(color);
        System.out.println(ac);
        ac.close();
    }
}
