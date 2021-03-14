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
 *          给容器中注册一个AnnotationAwareAspectJAutoProxyCreator （自动代理创建器）
 *      2、AnnotationAwareAspectJAutoProxyCreator：
 *              AnnotationAwareAspectJAutoProxyCreator
 *                   -> AspectJAwareAdvisorAutoProxyCreator
 *                      -> AbstractAdvisorAutoProxyCreator
 *                          -> AbstractAutoProxyCreator
 *                              implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware
 *                          关注bean后置处理器（在bean初始化完成前后做的事情）、自动装配BeanFactory
 *      AbstractAutoProxyCreator.setBeanFactory()
 *      AbstractAutoProxyCreator.有后置处理器的逻辑；
 *      AbstractAdvisorAutoProxyCreator.setBeanFactory() ==> initBeanFactory()
 *      AnnotationAwareAspectJAutoProxyCreator.initBeanFactory()
 *
 * 流程：
 *      1、传入配置类，创建IOC容器
 *      2、注册配置类，调用refresh()刷新容器
 *      3、registerBeanPostProcessors(beanFactory);注册bean的后置处理来方便拦截bean的创建
 *          1）、先获取IOC容器中已经定义了的需要创建对象的所有BeanPostProcessor
 *          2）、给容器中添加别的BeanPostProcessor
 *          3）、优先注册实现了PriorityOrdered接口的BeanPostProcessor
 *          4）、在给容器中注册实现了Ordered接口的BeanPostProcessor
 *          5）、注册没实现优先级接口的BeanPostProcessor
 *          6）、注册BeanPostProcessor，实际上就是创建BeanPostProcessor对象，保存在容器中
 *              创建internalAutoProxyCreator的BeanPostProcessor【AnnotationAwareAspectJAutoProxyCreator】
 *              1、创建Bean实例
 *              2、populateBean：给Bean的各种属性赋值
 *              3、initializeBean：初始化Bean
 *                  1）、invokeAwareMethods()：处理Aware接口的方法回调
 *                  2）、applyBeanPostProcessorsBeforeInstantiation()：执行后置处理器的postProcessBeforeInitialization()
 *                  3）、invokeInitMethods()：执行自定义从初始化方法
 *                  4）、applyBeanPostProcessorsAfterInitialization()：执行后置处理器的postProcessAfterInitialization()
 *              4、BeanPostProcessor（AnnotationAwareAspectJAutoProxyCreator）创建成功
 *          7）、把BeanPostProcessor注册到BeanFactory中：
 *              beanFactory.addBeanPostProcessor(postProcessor);
 * =========以上是创建和注册AnnotationAwareAspectJAutoProxyCreator的过程===========
 *          AnnotationAwareAspectJAutoProxyCreator =》InstantiationAwareBeanPostProcessor
 *     4、finishBeanFactoryInitialization(beanFactory):完成BeanFactory初始化工作，创建剩下的单实例Bean
 *          1)、遍历获取容器中所有的Bean，一次创建对象
 *              getBean -> doGetBean() -> getSingleton()
 *          2）、创建Bean
 *          【AnnotationAwareAspectJAutoProxyCreator在所有Bean创建之前会有一个拦截， InstantiationAwareBeanPostProcessor会调用
 *          postProcessBeforeInstantiation()】
 *              1、先从缓存中获取当前Bean，如果能获取到，说明Bean是之前被创建过的，直接使用，否则再创建
 *              2、只要创建好的Bean都会被缓存起来
 *          3）、createBean()；创建Bean
 *              【BeanPostProcessor实在Bean对象创建完成初始化前后调用的】
 *              【InstantiationAwareBeanPostProcessor是在创建Bean实例之前先尝试用后置处理器返回对象的】
 *              【AnnotationAwareAspectJAutoProxyCreator会在任何Bean创建之前先尝试返回Bean的实例 】
 *              1、resolveBeforeInstantiation(beanName, mbdToUse);解析BeforeInstantiation
 *                  希望后置处理器在此能返回一个代理对象，如果返回代理对象就使用，否则就继续
 *                      1）、后置处理器先尝试返回对象：
 *                      bean = this.applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
 *                      拿到所有后置处理器，InstantiationAwareBeanPostProcessor就执行postProcessBeforeInstantiation
 *                     if (bean != null) {
 *                          bean = this.applyBeanPostProcessorsAfterInitialization(bean, beanName);
 *                     }
 *              2、doCreateBean(beanName, mbdToUse, args);真正的去创建一个Bean实例；和3.6流程一样
 *              3、
 * AnnotationAwareAspectJAutoProxyCreator【InstantiationAwareBeanPostProcessor】的作用：
 * 1、每一个Bean创建之前，调用postProcessBeforeInstantiation()
 *  关心MathCaculator和LogAspect的创建
 *      1）、判断当前Bean是否在advisedBeans中（保存了所有需要增强bean）
 *      2）、判断当前Bean是否是基础类型的Advice、Pointcut、Advisor、AOPINfrastructureBean，或者是否是切面（@Aspect）
 *      3）、是否需要跳过
 *
 * @author Eric.Zhang
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
