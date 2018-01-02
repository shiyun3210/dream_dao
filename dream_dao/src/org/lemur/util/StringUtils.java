package org.lemur.util;

public class StringUtils
{
  public static String toUpperFirstWord(String str)
  {
    return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
  }

  public static String toLowerFirstWord(String str) {
    return str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
  }
}