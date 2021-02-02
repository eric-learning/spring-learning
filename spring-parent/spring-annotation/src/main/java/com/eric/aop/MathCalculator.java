package com.eric.aop;

/**
 * Description: spring-parent
 *
 * @author Eric.Zhang
 * @date 2021/1/31
 */
public class MathCalculator {

    public int div(int a, int b){
        System.out.println("MathCalculator---div---");
        return a/b;
    }

    public int add(int a, int b){
        return a+b;
    }
}
