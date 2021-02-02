package com.eric.test;

import com.eric.config.BeanConfigOfProfile;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;

/**
 * Description: spring-parent
 *
 * @author Eric.Zhang
 * @date 2021/1/23
 */
public class IOCTest_Profile {

    /**
     * 1、使用命令行参数：在虚拟机参数位置加上 -Dspring.profiles.active=test
     * 2、使用代码的方式激活某种环境：ac.getEnvironment().setActiveProfiles("test", "dev");
     */
    @Test
    public void test01() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ac.getEnvironment().setActiveProfiles("test", "dev");
        ac.register(BeanConfigOfProfile.class);
        ac.refresh();
        String[] names = ac.getBeanNamesForType(DataSource.class);
        for (String name : names) {
            System.out.println(name);
        }
        ac.close();
    }
}
