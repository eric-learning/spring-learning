package com.eric.scope;


import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Description: 自定义Scope实现，线程级别的bean作用域
 *
 * @author zhangxiusen
 * @date 2021-1-19
 */
public class ThreadScope implements Scope {

	public static final String THREAD_SCOPE = "thread";

	private ThreadLocal<Map<String, Object>> beanMap = ThreadLocal.withInitial(() -> new HashMap<>());

	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		Object bean = beanMap.get().get(name);
		if (Objects.isNull(bean)){
			bean = objectFactory.getObject();
			beanMap.get().put(name, bean);
		}
		return bean;
	}

	@Override
	public Object remove(String name) {
		return beanMap.get().remove(name);
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
		System.out.println(name);
	}

	@Override
	public Object resolveContextualObject(String key) {
		return null;
	}

	@Override
	public String getConversationId() {
		return Thread.currentThread().getName();
	}
}
