package org.dream.service;

/**
 * 生成对象BEAN
 * @author houzhijia
 * @2014-4-10
 * @下午8:56:51
 */
public class GenerateBean{
	
	private String target;
	
	private boolean generate;
	
	private GenerateBean(){}
	
	public GenerateBean(String target,boolean generate){
		this.target = target;
		this.generate = generate;
	}
	
  
}