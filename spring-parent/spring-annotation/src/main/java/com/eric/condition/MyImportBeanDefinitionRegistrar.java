package com.eric.condition;

import com.eric.bean.Rainbow;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Description: spring-parent
 *
 * @author zhangxiusen
 * @date 2021/1/23
 */
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     *
     * @param importingClassMetadata    当前类的注解信息
     * @param registry                  BeanDefinition注册类
     *        把所有需要添加到容器中的bean，调用
     *        BeanDefinitionRegistry.registerBeanDefinition收工注册进来
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean blueDefinition = registry.containsBeanDefinition("com.eric.bean.Blue");
        if (blueDefinition){
            RootBeanDefinition beanDefinition = new RootBeanDefinition(Rainbow.class);
            registry.registerBeanDefinition("rainbow", beanDefinition);
        }
    }
}
