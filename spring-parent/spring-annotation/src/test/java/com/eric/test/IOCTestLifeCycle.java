package com.eric.test;

import com.eric.bean.Car;
import com.eric.config.BeanConfigOfLifeCycle;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description: spring-parent
 *
 * @author zhangxiusen
 * @date 2021/1/23
 */
public class IOCTestLifeCycle {

    @Test
    public void test01(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(BeanConfigOfLifeCycle.class);
        System.out.println("容器创建完成---");
        ac.getBean(Car.class);
        ac.close();
    }
}
