package com.eric.condition;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Description: spring-parent
 * 后置处理器，初始化前后进行处理工作
 * 将后置处理器加入到容器中
 * @author zhangxiusen
 * @date 2021/1/23
 */
public class MyBeanPostProcessor implements BeanPostProcessor {

    /**
     * 在所有其他初始化操作（Bean指定、实现、JSR250）之前执行处理
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("postProcessBeforeInitialization---"+beanName+"---");
        return bean;
    }

    /**
     * 在所有初始化之后操作之后执行处理
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("postProcessAfterInitialization---"+beanName+"---");
        return bean;
    }
}
