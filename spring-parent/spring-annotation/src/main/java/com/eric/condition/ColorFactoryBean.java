package com.eric.condition;

import com.eric.bean.Color;
import org.springframework.beans.factory.FactoryBean;

/**
 * Description: spring-parent
 *
 * @author zhangxiusen
 * @date 2021/1/23
 */
public class ColorFactoryBean implements FactoryBean<Color> {

    /**
     * 返回的bean对象
     * @return
     * @throws Exception
     */
    @Override
    public Color getObject() throws Exception {
        return new Color();
    }

    /**
     * 返回的bean类型
     * @return
     */
    @Override
    public Class<Color> getObjectType() {
        return Color.class;
    }

    /**
     * 是否单例
     * @return
     */
    @Override
    public boolean isSingleton() {
        return true;
    }
}
