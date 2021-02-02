package com.eric.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: spring-parent
 * 默认加在IOC容器中的组件，Spring容器在启动时会调用对象的无参构造器创建对象，再进行初始化赋值等操作
 * @author Eric.Zhang
 * @date 2021/1/24
 */
@Component
public class Boss {

    private Car car;

    public Car getCar() {
        return car;
    }

    // 构造器要用的组件都是从容器中获取
    @Autowired
    public Boss(Car car){
        this.car = car;
    }


    // 标注在方法上，Spring容器创建当前对象，就会调用该方法，完成赋值
    // 方法是用的参数，自定义类型的值从IOC容器中获取
//    @Autowired
    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public String toString() {
        return "Boss{" +
                "car=" + car +
                '}';
    }
}
