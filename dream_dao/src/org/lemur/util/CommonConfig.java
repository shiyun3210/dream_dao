package org.lemur.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonConfig
{
  private static Properties prop;

  public static URL getResource(String resource)
  {
    ClassLoader classLoader = null;
    URL url = null;
    try
    {
      classLoader = Thread.currentThread().getContextClassLoader();
      if (classLoader != null) {
        System.out.println("Trying to find [" + resource + "] using context classloader " + classLoader + ".");
        url = classLoader.getResource(resource);

        if (url != null) {
          return url;
        }

      }

      classLoader = CommonConfig.class.getClassLoader();
      if (classLoader != null) {
        System.out.println("Trying to find [" + resource + "] using " + classLoader + " class loader.");
        url = classLoader.getResource(resource);
        if (url != null)
          return url;
      }
    }
    catch (Throwable t) {
      t.printStackTrace();

      System.out.println("Trying to find [" + resource + 
        "] using ClassLoader.getSystemResource().");
      url = ClassLoader.getSystemResource(resource);
      if (url == null)
	      System.out.println("has a error url:" + resource); 
    }
    return url;
  }

  public static String find(String regex, String content) {
    Matcher m = null;
    m = Pattern.compile(regex).matcher(content);
    if (m.find()) {
      return m.group(1);
    }
    return null;
  }

  public static String getPropertyValue(String key) {
    String configFileName = "config.properties";
    if (prop == null) {
      String cronPath = getResource(configFileName).getFile();
      File cronFile = new File(cronPath);
      System.out.println("cronPath: " + cronPath);
      String cronSux = find("file:(/.*?/)[^/]+?\\.jar", cronPath);
      if (cronSux != null) {
        cronSux = cronSux + configFileName;
        cronFile = new File(cronSux);
        System.out.println("cronPath_SUX: " + cronSux);
      } else {
        cronSux = find("(/.*/)[^/]+?", cronPath);
        if (cronSux != null) {
          cronSux = cronSux.substring(0, cronSux.substring(0, cronSux.length() - 2).lastIndexOf("/")) + "/" + configFileName;
          cronFile = new File(cronSux);
          System.out.println("cronPath_SUX: " + cronSux);
        }
      }
      if (cronFile.canRead()) {
        try {
          prop = new Properties();
          prop.load(new FileInputStream(cronFile));
          prop.setProperty("classdir", cronSux.substring(0, cronSux.lastIndexOf("/")));
        } catch (Exception ex) {
        	ex.printStackTrace();
        	System.err.println("�����ļ�����!");
        }
      }
      System.out.println("���������ļ�:" + cronSux);
    }

    return prop.getProperty(key);
  }
  
  public static boolean getBooleanValue(String key){
	  
	  return Boolean.parseBoolean(getPropertyValue(key));
  }

  public static void main(String[] args) throws UnsupportedEncodingException
  {
    System.out.println("value: " + Boolean.parseBoolean(null));
  }
}