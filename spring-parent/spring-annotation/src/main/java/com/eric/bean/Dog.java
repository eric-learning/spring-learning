package com.eric.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Description: spring-parent
 *
 * @author Eric.Zhang
 * @date 2021/1/23
 */
@Component
public class Dog implements ApplicationContextAware {

//    @Autowired
    private ApplicationContext applicationContext;

    public Dog(){
        System.out.println("dog constructor---");
    }

    /**
     * 对象创建并赋值之后调用
     */
    @PostConstruct
    public void init(){
        System.out.println("dog postConstructor---");
    }

    /**
     * 在容器移除对象之前调用
     */
    @PreDestroy
    public void preDestroy(){
        System.out.println("dog preDestroy---");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
