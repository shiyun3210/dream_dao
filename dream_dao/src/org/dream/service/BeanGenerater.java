package org.dream.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dream.util.FreemarkerUtils;
import org.dream.util.StringUtils;

public class BeanGenerater implements Generatable{
	
	private static String TYPE = "bean";
	private final String ftl;
	private final String outputPath;
	private final String packagePath;
	
	
	@Override
	public String getTemplateFileName() {
	
		return ftl;
		
	}
	
	
	public BeanGenerater(String ftl, String outputPath, String packagePath){
		
		this.ftl = ftl;
		this.outputPath = outputPath;
		this.packagePath = packagePath;
	}
	
	
	@Override
	public String getOutputPath(){
		
		return outputPath;
	}
	
	
	@Override
	public String getPackage(){
		
		return packagePath;
	}

	@Override
	public void generate(String tableName, List<ColumnBean> columnList){
		
		Map<String, Object> root = new HashMap<String, Object>();
		String upperFirstWordType = StringUtils.toUpperFirstWord(TYPE);
		
		String packages = packagePath.replace("/", ".");
		root.put("packages", packages + "." + TYPE);
		
		for(ColumnBean bean : columnList){
			bean.setColumnName(camelName(bean.getColumnName()));
		}
		root.put("table", columnList);
		
		StringUtils.toUpperFirstWord(tableName);
		String className = StringUtils.transformTableNameToSpecialName(tableName, upperFirstWordType);
		root.put("className", className);
		
		String outputFile = outputPath + packagePath + "/" + TYPE + "/" + className + ".java";
		FreemarkerUtils.process(root, ftl, getClass(), outputFile);
		System.out.println("表[" + tableName + "]生成" + TYPE + "完毕!");
	}
	
	/** 
	 * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。</br> 
	 * 例如：HELLO_WORLD->HelloWorld 
	 * @param name 转换前的下划线大写方式命名的字符串 
	 * @return 转换后的驼峰式命名的字符串 
	 */  
	public static String camelName(String name) {  
	    StringBuilder result = new StringBuilder();  
	    // 快速检查  
	    if (name == null || name.isEmpty()) {  
	        // 没必要转换  
	        return "";  
	    } else if (!name.contains("_")) {  
	        // 不含下划线，仅将首字母小写  
	        return name.substring(0, 1).toLowerCase() + name.substring(1);  
	    }  
	    // 用下划线将原始字符串分割  
	    String camels[] = name.split("_");  
	    for (String camel :  camels) {  
	        // 跳过原始字符串中开头、结尾的下换线或双重下划线  
	        if (camel.isEmpty()) {  
	            continue;  
	        }  
	        // 处理真正的驼峰片段  
	        if (result.length() == 0) {  
	            // 第一个驼峰片段，全部字母都小写  
	            result.append(camel.toLowerCase());  
	        } else {  
	            // 其他的驼峰片段，首字母大写  
	            result.append(camel.substring(0, 1).toUpperCase());  
	            result.append(camel.substring(1).toLowerCase());  
	        }  
	    }  
	    return result.toString();  
	}  
	
	
	/** 
	 * 将驼峰式命名的字符串转换为下划线大写方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。</br> 
	 * 例如：HelloWorld->HELLO_WORLD 
	 * @param name 转换前的驼峰式命名的字符串 
	 * @return 转换后下划线大写方式命名的字符串 
	 */  
	public static String underscoreName(String name) {  
	    StringBuilder result = new StringBuilder();  
	    if (name != null && name.length() > 0) {  
	        // 将第一个字符处理成大写  
	        result.append(name.substring(0, 1).toUpperCase());  
	        // 循环处理其余字符  
	        for (int i = 1; i < name.length(); i++) {  
	            String s = name.substring(i, i + 1);  
	            // 在大写字母前添加下划线  
	            if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {  
	                result.append("_");  
	            }  
	            // 其他字符直接转成大写  
	            result.append(s.toUpperCase());  
	        }  
	    }  
	    return result.toString();  
	}  
}