package org.dream.util;

public class StringUtils{
  
	public static String toUpperFirstWord(String str){
    
		return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
	}

  
	public static String toLowerFirstWord(String str) {
		
		return str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
	}
	
	
	public static String transformTableNameToVarName(String tableName, String lastWord){
		
		if(tableName == null || tableName.trim().isEmpty()){
			return tableName.toLowerCase();
		}
		
		String[] strArray = tableName.split("_");
		if(strArray.length == 1){
			return toLowerFirstWord(tableName) + lastWord;
		}
		
		String name = "";
		for(int i=1; i<strArray.length; i++){
			if(i == 1){
				name += strArray[1].toLowerCase();
			}else{
				name += toUpperFirstWord(strArray[i]);
			}
		}
		
		return name + lastWord;
	}
	
	
	public static String transformTableNameToSpecialName(String tableName, String lastWord){
		
		if(tableName == null || tableName.trim().isEmpty()){
			return tableName;
		}
		
		String[] strArray = tableName.split("_");
		if(strArray.length == 1){
			return toLowerFirstWord(tableName) + lastWord;
		}
		
		String name = "";
		for(int i=0; i<strArray.length; i++){
			name += toUpperFirstWord(strArray[i]);
		}
		
		return name + lastWord;
	}
	
	
	public static String transformTableNameToDaoVarName(String tableName){
		
		if(tableName == null || tableName.trim().isEmpty()){
			return tableName.toLowerCase();
		}
		
		String[] strArray = tableName.split("_");
		if(strArray.length == 1){
			return toLowerFirstWord(tableName);
		}
		
		String name = "";
		for(int i=1; i<strArray.length; i++){
			if(i == 1){
				name += strArray[1].toLowerCase();
			}else{
				name += toUpperFirstWord(strArray[i]);
			}
		}
		
		return name;
	}
	
	
	public static String transformTableNameToDaoName(String tableName){
		
		if(tableName == null || tableName.trim().isEmpty()){
			return tableName;
		}
		
		String[] strArray = tableName.split("_");
		if(strArray.length == 1){
			return toLowerFirstWord(tableName);
		}
		
		String name = "";
		for(int i=0; i<strArray.length; i++){
			name += toUpperFirstWord(strArray[i]);
		}
		
		return name;
	}
	
	
	public static String transformTableNameToURL(String tableName){
		
		if(tableName == null || tableName.trim().isEmpty()){
			return tableName;
		}
		
		String[] strArray = tableName.split("_");
		if(strArray.length == 1){
			return tableName.toLowerCase();
		}
		
		String name = strArray[0] + "/";
		
		for(int i=1; i<strArray.length; i++){
			if(i == 1){
				name += strArray[1].toLowerCase();
			}else{
				name += toUpperFirstWord(strArray[i]);
			}
		}
		
		return name;
	}
	
	
	public static String transformTableNameToViewPath(String tableName){
		
		if(tableName == null || tableName.trim().isEmpty()){
			return tableName;
		}
		
		String[] strArray = tableName.split("_");
		if(strArray.length == 1){
			return tableName.toLowerCase();
		}
		
		String name = strArray[0] + "/";
		
		for(int i=1; i<strArray.length; i++){
			if(i == 1){
				name += strArray[1].toLowerCase();
			}else{
				name += toUpperFirstWord(strArray[i]);
			}
		}
		
		return name;
	}
}