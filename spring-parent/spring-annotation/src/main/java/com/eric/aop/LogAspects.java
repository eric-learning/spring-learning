package com.eric.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

import java.util.Arrays;

/**
 * Description: spring-parent
 * @Aspect：告诉SPring这是一个切面类
 *
 * @author zhangxiusen
 * @date 2021/1/31
 */
@Aspect
public class LogAspects {

    /**
     * 抽取公共的切入点表达式
     * 1、本类引用
     * 2、其他的切面引用
     */
    @Pointcut("execution(public int com.eric.aop.MathCalculator.*(..))")
    public void pointCut(){}

    /**
     * @Before 在目标方法之前切入，切入点表达式（指定在哪个方法切入，*及..表示所有方法的任意匹配参数）
     */
    @Before("execution(public int com.eric.aop.MathCalculator.*(..))")
    public void logStart(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        System.out.println(joinPoint.getSignature().getName()+"方法运行@Before---参数列表是：{"+ Arrays.asList(args)+"}");
    }

    @After("pointCut()")
    public void logEnd(JoinPoint joinPoint){
        System.out.println(joinPoint.getSignature().getName()+"方法结束@After---");
    }

    /**
     * JoinPoint一定要放在参数表的第一位
     * @param joinPoint
     * @param result
     */
    @AfterReturning(value = "com.eric.aop.LogAspects.pointCut()", returning = "result")
    public void logReturn(JoinPoint joinPoint, Object result){
        System.out.println(joinPoint.getSignature().getName()+"方法正常返回@AfterReturning---运行结果：{"+result+"}");
    }

    @AfterThrowing(value = "pointCut()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception){
        System.out.println(joinPoint.getSignature().getName()+"方法报异常@AfterThrowing---异常信息：{"+exception.getMessage()+"}");
    }

//    @Around("pointCut()")
    public void logAround(){
        System.out.println("除法方法环绕执行@Around---");
    }
}
