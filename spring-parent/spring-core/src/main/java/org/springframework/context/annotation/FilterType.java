package org.springframework.context.annotation;

/**
 * Description: spring-parent
 *
 * @author Eric.Zhang
 * @date 2021-1-18
 */
public enum FilterType {
	/*按照注解进行包含或者排除*/
	ANNOTATION,
	/*按照给定的类型（bean类型）进行包含或者排除*/
	ASSIGNABLE_TYPE,
	/*按照ASPECTJ表达式进行包含或者排除（不常用）*/
	ASPECTJ,
	/*按照正则表达式进行包含或者排除（不常用）*/
	REGEX,
	/*按照自定义规则进行包含或者排除*/
	CUSTOM;

	private FilterType() {
	}
}