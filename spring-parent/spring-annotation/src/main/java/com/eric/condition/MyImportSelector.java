package com.eric.condition;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Description: spring-parent
 *
 * @author zhangxiusen
 * @date 2021/1/23
 */
public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{"com.eric.bean.Blue"};
    }
}
