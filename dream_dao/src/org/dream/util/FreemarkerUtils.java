package org.dream.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class FreemarkerUtils{
  
	private static Configuration cfg = null;
	public static void process(
			Map<String, Object> rootMap,
			String ftl, 
			Class<?> clazz,
			String outputFile
	) {
		cfg = new Configuration();
		cfg.setClassForTemplateLoading(clazz, "temp");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		
		
		File file = new File(outputFile);
		Writer writer = null;
		
		// 设置模板文件位置
		Template template;
		try {
			template = cfg.getTemplate(ftl);
			template.setOutputEncoding("GBK");
			writer = new OutputStreamWriter(new FileOutputStream(file));
			template.process(rootMap,writer);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				writer = null;
			}
		}
	}
}