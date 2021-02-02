package com.eric.config;

import com.eric.bean.Car;
import com.eric.condition.MyBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Description: spring-parent
 * bean的生命周期：
 *      bean创建---初始化---销毁的过程
 *      容器管理bean的生命周期；
 *      我们可以自定义初始化和销毁方法；容器在bean进行到当前生命周期的时候来调用我们自定义的初始化和销毁方法
 * 构造（对象创建）：
 *      单实例：在容器启动时构建对象
 *      多实例：在获取时创建对象
 * 初始化：
 *      对象创建完成，并赋值好，调用初始化方法
 * 销毁：
 *      单实例：容器关闭的时候
 *      多实例：容器不会管理这个bean；容器不会调用销毁方法
 *
 *      遍历得到容器中所有的BeanPostProcessor，挨个执行beforeInitialization，
 *      一旦返回null，跳出for循环，不会执行后面的BeanPostProcessor
 *
 *      BeanPostProcessor原理：
 *      populateBean(beanName, mbd, instanceWrapper);       给bean进行属性赋值
 *      initializeBean(beanName, exposedObject, mbd){
 *          applyBeanPostProcessorsBeforeInitialization(bean, beanName);
 *          invokeInitMethods(beanName, wrappedBean, mbd);  执行自定义初始化
 *          applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
 *      };
 *
 *      1)、指定初始化和销毁方法
 *          通过@Bean指定initMethod和destroyMethod
 *      2)、通过让bean实现InitializingBean(定义初始化逻辑)
 *                      DisposableBean(定义销毁逻辑)
 *      3)、可以使用JSR250
 *          `@PostConstruct`在bean创建完成并且属性赋值完成，来执行初始化方法
 *          `@PreDestory`在容器销毁bean之前执行销毁工作
 *      4)、BeanPostProcessor【interface】：bean的后置处理器
 *          在bean初始化前后进行一些处理工作
 *          postProcessorBeforeInitialization:在初始化之前工作
 *          postProcessorAfterInitialization:在初始化之后工作
 *
 * Spring底层对BeanPostProcessor的使用：
 *      bean赋值，注入其他组件，@Autowired，生命周期注解功能，@Async，---BeanPostProcessor
 * @author Eric.Zhang
 * @date 2021/1/23
 */
@ComponentScan("com.eric.bean")
@Configuration
@Import(MyBeanPostProcessor.class)
public class BeanConfigOfLifeCycle {

    // @Scope
    @Bean(initMethod = "init", destroyMethod = "destroy")
    public Car car(){
        return new Car();
    }
}
