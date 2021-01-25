package com.eric.bean;

import org.springframework.stereotype.Component;

/**
 * Description: spring-parent
 *
 * @author zhangxiusen
 * @date 2021/1/23
 */
@Component
public class Car {

    public Car(){
        System.out.println("car constructor---");
    }

    public void init(){
        System.out.println("car init---");
    }

    public void destroy(){
        System.out.println("car destory---");
    }
}
