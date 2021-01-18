package com.eric.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: spring_parent
 *
 * @author zhangxiusen
 * @date 2021-1-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

	private String name;

	private Integer age;

	@Override
	public String toString(){
		return "Person [name=" + name + ", age=" + age + "]";
	}
}
