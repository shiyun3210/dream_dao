package com.fm.file.util;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

/**
 * Jackson JSON解析器工具类
 * @author dell
 *
 */
public class JacksonUtils {

	protected static ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}

	/**
	 * 将Java对象转化为JSON
	 * 
	 * @param object Java对象,可以是List<POJO>, POJO[], POJO ,也可以Map名值对, 将被转化为JSON字符串
	 * @return JSON字符串
	 */
	public static String toJson(Object object) {
		String json = null;
		try {
			json = mapper.writeValueAsString(object);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 将Java对象转化为JSON
	 * 
	 * @param object Java对象,可以是List<POJO>, POJO[], POJO ,也可以Map名值对, 将被转化为JSON字符串
	 * @param callback 回调方法名
	 * @return JSON字符串
	 */
	public static String toJsonP(Object object, String callback) {
		String json = null;
		try {
			json = mapper.writeValueAsString(new JSONPObject(callback, object));
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 将Java对象转化为JSON
	 * 
	 * @param json
	 * @param type
	 * @return JSON字符串
	 */
	public static <T> T toObject(String json, Class<T> type) {
		T obj = null;
		try {
			obj = mapper.readValue(json, type);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 将Java对象转化为JSON
	 * 
	 * @param json
	 * @param type
	 * @return JSON字符串
	 */
	public static <T> T toObject(String json, TypeReference<T> type) {
		T obj = null;
		try {
			obj = mapper.readValue(json, type);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
