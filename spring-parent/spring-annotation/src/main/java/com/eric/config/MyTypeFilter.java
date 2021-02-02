package com.eric.config;

import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

/**
 * Description: spring-parent
 *
 * @author Eric.Zhang
 * @date 2021-1-18
 */
public class MyTypeFilter implements TypeFilter {

	/**
	 *
	 * @param metadataReader        读取到当前正在扫描的类的信息
	 * @param metadataReaderFactory 可以获取到其他任何类的信息的（工厂）
	 * @return
	 * @throws IOException
	 */
	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
		// 获取当前类注解的信息
		AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
		// 获取当前类的类信息，包含类型、实现接口等
		ClassMetadata classMetadata = metadataReader.getClassMetadata();
		// 获取当前类的资源信息，比如类的路径信息
		Resource resource = metadataReader.getResource();
		// 获取当前类的类名
		String className = classMetadata.getClassName();
		System.out.println("--->" + className);
		if (className.contains("controller")){
			return true;
		}else {
			return false;
		}
	}
}
