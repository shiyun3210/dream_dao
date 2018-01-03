package com.fm.file.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

public class HttpClientRequest {
    /**
     * 连接HttpUrl
     * @param httpUrl
     * @param paramsMap
     * @param method GET,POST,
     * @return
     */
    public static String connectToHttpUrl(String httpUrl, Map<String, String> paramsMap,String method){
        // 返回结果
        String result = null;
        HttpURLConnection httpURLConnection = null;
        OutputStream os = null;
        InputStream is = null;
        StringBuffer params = null;
        // 设置参数
        if (paramsMap != null && !paramsMap.isEmpty()) {
            params = new StringBuffer();
            for(Entry<String, String> entry: paramsMap.entrySet()) {
                params.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        try {
            URL url = new URL(httpUrl);// 创建url
            httpURLConnection = (HttpURLConnection) (url.openConnection());// 打开http连接
            // 设置http连接属性
            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(1000 * 10);// 连接超时10秒
            // 建立连接
            httpURLConnection.connect();
            // 发送请求数据，并设置为UTF-8编码
            if (params != null) {
                os = httpURLConnection.getOutputStream();
                os.write(params.toString().replaceFirst("&", "").getBytes("UTF-8"));
                os.close();
            }
            // 接收返回数据
            is = httpURLConnection.getInputStream();
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            int n;
            while ((n = is.read()) != -1) {
                bao.write(n);
            }
            is.close();
            httpURLConnection.disconnect();
            // 返回响应数据，并设置为utf-8编码
            result = new String(bao.toByteArray(), "ISO-8859-1");
            result = new String(result.getBytes("ISO-8859-1"), "UTF-8");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            if (e.toString().contains("java.net.SocketException") || e.toString().contains("java.net.SocketTimeoutException")) {
                System.out.println("连接超时！");
            }
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }
}
