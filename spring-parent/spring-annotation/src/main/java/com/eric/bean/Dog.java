package com.eric.bean;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Description: spring-parent
 *
 * @author zhangxiusen
 * @date 2021/1/23
 */
@Component
public class Dog {

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
}
