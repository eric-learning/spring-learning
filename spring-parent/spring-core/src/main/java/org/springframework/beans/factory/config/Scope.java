package org.springframework.beans.factory.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.lang.Nullable;

/**
 * Description: 作用域接口
 *
 * @author zhangxiusen
 * @date 2021-1-19
 */
public interface Scope {

	/**
	 * 返回当前作用域中name对应的bean对象
	 *
	 * @param name          需要检索的bean对象的名称
	 * @param objectFactory 如果name对应的bean对象在当前作用域没找到，name可以调用这个objectFactory来创建这个对象
	 * @return
	 */
	Object get(String name, ObjectFactory<?> objectFactory);

	/**
	 * 将name对应的bean对象从当前作用域中移除
	 *
	 * @param name
	 * @return
	 */
	@Nullable
	Object remove(String name);

	/**
	 * 用于注册 销毁回调，若想要销毁相应的对象，则由Spring容器注册相应的销毁回调，而由自定义作用域选择是不是要销毁相应的对象
	 *
	 * @param name
	 * @param callback
	 */
	void registerDestructionCallback(String name, Runnable callback);

	/**
	 * 用于解析相应的上下文数据，比如request作用域将返回request中的属性
	 *
	 * @param key
	 * @return
	 */
	@Nullable
	Object resolveContextualObject(String key);

	/**
	 * 作用域的会话标识，比如session作用域的会话标识是sessionId
	 *
	 * @return
	 */
	@Nullable
	String getConversationId();
}
