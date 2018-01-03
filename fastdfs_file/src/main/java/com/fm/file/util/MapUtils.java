package com.fm.file.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * jackson 
 * @author syf
 *
 */
public class MapUtils {
	/**
	 * 将Map参数构建成 按照字段名的 ASCII 码 从小到大排序 的字符串
	 * @param params
	 * @return
	 */
	public static String getStringSort(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		// 对所有传入参数按照字段名的 ASCII 码 从小到大排序（字典序）
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}
	
	/**
	 * 将Map参数构建 key="value" 格式字符串
	 * @param params
	 * @return
	 */
	public static String getStringToString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		// 对所有传入参数按照字段名的 ASCII 码 从小到大排序（字典序）
		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=\"" + value+"\"";
			} else {
				prestr = prestr + key + "=\"" + value + "\"&";
			}
		}

		return prestr;
	}
	/**
	 * json 转 map
	 * @param json
	 * @return
	 */
	public static Map<String,Object> jsonToMap(String json){
		Map<String,Object> map = new HashMap<String,Object>();
		ObjectMapper om = new ObjectMapper();
		om.configure(SerializationFeature.INDENT_OUTPUT, false);
		try {
			map = om.readValue(json, new TypeReference<Map<String,Object>>(){});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
}
