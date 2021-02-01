package com.eric.test;

import com.eric.aop.MathCalculator;
import com.eric.config.BeanConfigOfAOP;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description: spring-parent
 *
 * 流程：
 *      1、传入配置类，创建IOC容器
 *      2、注册配置类，调用refresh(); 刷新容器
 *      3、registerBeanPostProcessors(beanFactory); 注册bean的后置处理器来方便拦截bean的创建
 *          1）先获取IOC容器中已经定义了的需要创建对象的所有BeanPostProcessor
 *          2）给容器中加别的BeanPostProcessor
 *          3）优先注册实现了PriorityOrdered接口的BeanPostProcessor
 *          4）再给容器中注册实现了Ordered接口的BeanPostProcessor
 *          5）注册没实现优先级接口的BeanPostProcessor
 *          6）注册BeanPostProcessor，实际上就是创建BeanPostProcessor对象，保存在容器中
 *              创建internalAutoProxyCreator的BeanPostProcessor【AnnotationAwareAspectJAutoProxy】
 *              1、创建Bean的实例
 *              2、populateBean：给bean的各种属性赋值
 *              3、initializeBean：初始化bean
 *                  1）invokeAwareMethods()：处理Aware接口的方法回调
 *                  2）applyBeanPostProcessorBeforeInitialization()：应用后置处理器的postProcessorBeforeInitialization()
 *                  3）invokeInitMethods()：执行自定义的初始化方法
 *                  4）applyBeanPostProcessorAfterInitialization()：应用后置处理器的postProcessorAfterInitialization()
 *              4、BeanPostProcessor(AnnotationAwareAspectJAutoProxyCreator)创建成功。--> aspectJAdvisorsBuilder
 *          7）把BeanPostProcessor注册到BeanFacotry中：
 *              beanFactory.addBeanPostProcessor(postProcessor);
 *
 * @author zhangxiusen
 * @date 2021/1/31
 */
public class IOCTest_AOP {

    @Test
    public void test01(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(BeanConfigOfAOP.class);

        // 1、不要自己创建对象
//        MathCalculator mathCalculator = new MathCalculator();
//        mathCalculator.div(1, 1);

        MathCalculator mathBean = ac.getBean(MathCalculator.class);
        mathBean.div(1, 0);
        ac.close();
    }
}
