package com.eric.config;

import com.eric.aop.LogAspects;
import com.eric.aop.MathCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.security.auth.login.CredentialException;

/**
 * Description: spring-parent
 *
 * AOP：【动态代理】
 *      指在程序运行期间动态的将某段代码切入到指定方法指定位置进行运行的编程方式
 *
 * 1、导入aop模块，Spring AOP（apring-aspects）
 * 2、定义一个业务逻辑类（MathCalculator），在业务逻辑运行的时候将日志进行打印（方法之前、方法运行结束）
 * 3、定义一个日志切面类（LogAspects），切面类里面的方法需要动态感知MathCalculator.div运行到
 *      通知方法：
 *          前置通知（@Before）：logStart，在目标方法（div）运行之前运行
 *          后置通知（@After）：logEnd，在目标方法（div）运行结束之后运行（无论方法正常结束还是异常结束）
 *          返回通知（@AfterReturning）：logReturn，在目标方法（div）正常返回之后运行
 *          异常通知（@AfterThrowing）：logException，在目标方法（div）出现异常以后运行
 *          环绕通知（@Around）：动态代理，手动推进目标方法运行（joinPoint.proceed()）
 * 4、给切面类的目标方法标注何时何地运行的通知注解
 * 5、将切面类和业务逻辑类（目标所在类）都加入到容器中
 * 6、给切面类加一个注解@Aspect标明切面类
 * 7、给配置类中加入@EnableAspectJAutoProxy 【开启基于注解的aop模式】
 *      在Spring中有很多@EnableXXX
 *
 * 三步：
 *      1、将业务逻辑组件和切面类都加入到容器中，告诉Spring那个是切面类（@Aspect）
 *      2、在切面类上的每一个通知方法上标注通知注解，告诉Spring何时何地的运行（切入点表达式）
 *      3、开启基于注解的aop模式，@EnableAspectJAutoProxy
 *
 * AOP原理【看给容器中注册了什么组件，这个组件什么时候工作，这个组件的功能是什么】：
 *      `@EnableAspectJAutoProxy`
 *      1、@EnableAspectJAutoProxy是什么？
 *          `@Import({AspectJAutoProxyRegistrar.class})`：给容器中导入AspectJAutoProxy
 *              利用AspectJAutoProxyRegistrar自定义给容器中注册bean；
 *              internalAutoProxyCreator=AnnotationAwareAspectJAutoProxyCreator
 *          给容器中注册一个AnnotationAwareAspectJAutoProxyCreator
 *      2、AnnotationAwareAspectJAutoProxyCreator：
 *              AnnotationAwareAspectJAutoProxyCreator
 *                   -> AspectJAwareAdvisorAutoProxyCreator
 *                      -> AbstractAdvisorAutoProxyCreator
 *                          -> AbstractAutoProxyCreator
 *                              implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware
 *                          关注bean后置处理器（在bean初始化完成前后做的事情）、自动装配BeanFactory
 *
 * @author zhangxiusen
 * @date 2021/1/31
 */
@EnableAspectJAutoProxy
@Configuration
public class BeanConfigOfAOP {

    /**
     * 业务逻辑类加入到容器中
     * @return
     */
    @Bean
    public MathCalculator mathCalculator(){
        return new MathCalculator();
    }

    /**
     * 切面类加入到容器中
     * @return
     */
    @Bean
    public LogAspects logAspects(){
        return new LogAspects();
    }
}
