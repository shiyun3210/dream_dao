package com.fm.file.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;



/**
 * 图片工具类
 * @author syf
 *
 */
public class ImageUtils {

    /**
     * 图形验证码
     *
     * @param buffImage
     * @return 验证码
     */
    public static String getImageCode(BufferedImage buffImage) {
        Graphics g = buffImage.getGraphics();
        // 随机产生一个比较接近白色的底色
        g.setColor(getRandColor(230, 250));
        // 填充底色
        g.fillRect(0, 0, 70, 20);
        // 增加干扰线
        Random r = new Random();
        for (int i = 0; i < 133; i++) {
            g.setColor(getRandColor(150, 210));
            int x1 = r.nextInt(70);
            int y1 = r.nextInt(20);
            int x2 = r.nextInt(12);
            int y2 = r.nextInt(12);
            g.drawLine(x1, y1, x2 + x1, y2 + y1);
        }

        String regCode = "";
        g.setFont(new Font("Times New Roman", Font.BOLD, 18));
        // 产生5个验证字符
        for (int i = 0; i < 5; i++) {
            g.setColor(getRandColor(50, 150));
            int num = getCode();
            char temp;
            if (num > 10) { // 返回的是字符
                temp = (char) num;
                regCode += temp;
                g.drawString(String.valueOf(temp), 13 * i + 6, 16);
            } else {// 返回的是数字
                regCode += num;
                g.drawString(String.valueOf(num), 13 * i + 6, 16);
            }
        }
        return regCode;
    }

    /**
     * @param low
     * @param high
     * @return Color
     */
    private static Color getRandColor(int low, int high) {
        Random ran = new Random();
        int r = low + ran.nextInt(high - low);
        int g = low + ran.nextInt(high - low);
        int b = low + ran.nextInt(high - low);
        return new Color(r, g, b);
    }

    /**
     * @return int
     */
    private static int getCode() {
        /*
         * 1 : 0-9 2 : a-z 3 : A-Z
		 */
        Random r = new Random();
        int code = 0;

        int flag = r.nextInt(3);
        if (flag == 0) {
            code = r.nextInt(10);
        } else if (flag == 1) {
            code = getCodeByScope('a', 'z');
        } else {
            code = getCodeByScope('A', 'Z');
        }
        return code;
    }

    /**
     * @param low
     * @param high
     * @return int
     */
    private static int getCodeByScope(int low, int high) {
        Random ran = new Random();
        return low + ran.nextInt(high - low);
    }

   

    /**
     * 图片加边框
     *
     * @param image
     * @param frame
     * @return BufferedImage
     */
    public static BufferedImage makeFrame(BufferedImage image, String frame) {
        BufferedImage newImage = null;
        try {
            // 水印文件
            File file = new File(frame);
            BufferedImage frameImage = ImageIO.read(file);
            int frameWidth = frameImage.getWidth();
            int frameHeight = frameImage.getHeight();
            Graphics2D g2d = frameImage.createGraphics();
            newImage = g2d.getDeviceConfiguration().createCompatibleImage(frameWidth, frameHeight, Transparency.TRANSLUCENT);
            g2d.dispose();

            // 目标加水印
            int imgWidth = image.getWidth();
            int imgHeight = image.getHeight();
            g2d = newImage.createGraphics();
            g2d.drawImage(frameImage, 0, 0, frameWidth, frameHeight, null);
            g2d.drawImage(image, (frameWidth - imgWidth) / 2, (frameHeight - imgHeight - 1) / 2, imgWidth, imgHeight, null);
            g2d.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newImage;
    }

    /**
     * 图片加水印
     *
     * @param image
     * @param watermark
     * @return BufferedImage
     */
    public static BufferedImage makeWatermark(BufferedImage image, String watermark) {
        try {
            // 目标文件
            int width = image.getWidth();
            int height = image.getHeight();
            Graphics2D g2d = image.createGraphics();
            g2d.drawImage(image, 0, 0, width, height, null);

            // 目标加水印
            File wmFile = new File(watermark);
            Image wmImage = ImageIO.read(wmFile);
            int wmWidth = wmImage.getWidth(null);
            int wmHeight = wmImage.getHeight(null);
            g2d.drawImage(wmImage, (width - wmWidth) / 2, (height - wmHeight) / 2, wmWidth, wmHeight, null);
            g2d.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 下载网络图片到本地
     *
     * @param url 图片地址
     * @param destPath 存储路径
     * @return 结果
     */
    public static boolean downloadImage(String url, String destPath) {
        boolean result = false;
        BufferedInputStream bis = null;
        OutputStream bos = null;
        try {
            File file = new File(destPath);
            FileUtils.createDirs(file.getParent(), true); // 创建文件夹

            URL _url = new URL(url);
            bis = new BufferedInputStream(_url.openStream());
            bos = new FileOutputStream(file);
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = bis.read(bytes)) > 0) {
                bos.write(bytes, 0, len);
            }
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 根据图片文件路径生成图片缩略图
     *
     * @param imagePath 原图片路径
     * @param thumbPath 缩略图路径
     * @param width 生成图片宽度
     * @param height 生成图片高度
     * @param mode 模式(1.补白)
     * @return 结果
     */
    public static boolean makeFileThumb(String imagePath, String thumbPath, int width, int height, int mode) {
        boolean result = false;
        try {
            BufferedImage srcImage = ImageIO.read(new File(imagePath)); // 构造Image对象
            if (thumbPath.toLowerCase().endsWith("png")) {
                result = makePngThumb(srcImage, thumbPath, width, height, mode);
            } else {
                result = makeJpgThumb(srcImage, thumbPath, width, height, mode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据图片网络地址生成图片缩略图
     *
     * @param url 图片地址
     * @param thumbPath 缩略图路径
     * @param width 生成图片宽度
     * @param height 生成图片高度
     * @param mode 模式(1.补白)
     * @return 结果
     */
    public static boolean makeUrlThumb(String url, String thumbPath, int width, int height, int mode) {
        boolean result = false;
        try {
            URL _url = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection) _url.openConnection();
            httpConn.setDoOutput(false); // 设置是否输出流，因为这个是GET请求，所以设为false, 默认情况下是false
            httpConn.setDoInput(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            // 判断请求状态，获取返回数据
            if (httpConn.getResponseCode() == 200) {
                BufferedImage srcImage = ImageIO.read(httpConn.getInputStream());
                if (thumbPath.toLowerCase().endsWith("png")) {
                    result = makePngThumb(srcImage, thumbPath, width, height, mode);
                } else {
                    result = makeJpgThumb(srcImage, thumbPath, width, height, mode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成JPG图片缩略图
     *
     * @param srcImage 源图形图像
     * @param thumbPath 缩略图路径
     * @param width 生成图片宽度
     * @param height 生成图片高度
     * @param mode 模式(1.补白)
     * @return 结果
     */
    private static boolean makeJpgThumb(BufferedImage srcImage, String thumbPath, int width, int height, int mode) {
        boolean result = false;
        try {
            int oldWidth = srcImage.getWidth(null); // 原图宽
            int oldHeight = srcImage.getHeight(null); // 原图高

            // 计算宽高比例
            float scaleWidth = 1f;
            float scaleHeight = 1f;

            if (width > 0 && height > 0) {
                if (mode == 1) {
                    if (oldWidth > width && oldHeight > height) {
                        scaleWidth = width / (oldWidth * 1f);
                        scaleHeight = height / (oldHeight * 1f);
                    } else if (oldWidth > width) {
                        scaleWidth = width / (oldWidth * 1f);
                        scaleHeight = scaleWidth;
                    } else if (oldHeight > height) {
                        scaleHeight = height / (oldHeight * 1f);
                        scaleWidth = scaleHeight;
                    } else {
                        scaleWidth = width / (oldWidth * 1f);
                        scaleHeight = height / (oldHeight * 1f);
                    }
                } else {
                    scaleWidth = width / (oldWidth * 1f);
                    scaleHeight = height / (oldHeight * 1f);
                }
            } else if (width > 0) {
                scaleWidth = width / (oldWidth * 1f);
                scaleHeight = scaleWidth;
            } else if (height > 0) {
                scaleHeight = height / (oldHeight * 1f);
                scaleWidth = scaleHeight;
            }

            // 转为整型比例(四舍五入)
            int newWidth = Math.round(oldWidth * scaleWidth);
            int newHeight = Math.round(oldHeight * scaleHeight);

            BufferedImage newImage = null;
            if (mode == 1) {
                newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // 初始化一个图像
                Graphics2D g2d = newImage.createGraphics();
                g2d.setBackground(Color.WHITE);
                g2d.clearRect(0, 0, width, height); // 使用当前绘图表面的背景色进行填充来清除指定的矩形
                g2d.dispose(); // 释放图像资源
                newImage.getGraphics().drawImage(srcImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), (width - newWidth) / 2, (height - newHeight - 1) / 2, newWidth, newHeight, Color.WHITE, null); // 合并缩略图与底图
            } else {
                newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB); // 初始化一个图像
                newImage.getGraphics().drawImage(srcImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, Color.WHITE, null);
            }

            File file = new File(thumbPath);
            FileUtils.createDirs(file.getParent(), true); // 先创建文件夹
            ImageIO.write(newImage, "jpg", file);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成PNG图片缩略图，支持背景透明
     *
     * @param srcImage 源图形图像
     * @param thumbPath 缩略图路径
     * @param width 生成图片宽度
     * @param height 生成图片高度
     * @param mode 模式(1.补白)
     * @return 结果
     */
    private static boolean makePngThumb(BufferedImage srcImage, String thumbPath, int width, int height, int mode) {
        boolean result = false;
        try {
            int oldWidth = srcImage.getWidth(null); // 原图宽
            int oldHeight = srcImage.getHeight(null); // 原图高

            // 计算宽高比例
            float scaleWidth = 1f;
            float scaleHeight = 1f;

            if (width > 0 && height > 0) {
                if (mode == 1) {
                    if (oldWidth > width && oldHeight > height) {
                        scaleWidth = width / (oldWidth * 1f);
                        scaleHeight = height / (oldHeight * 1f);
                    } else if (oldWidth > width) {
                        scaleWidth = width / (oldWidth * 1f);
                        scaleHeight = scaleWidth;
                    } else if (oldHeight > height) {
                        scaleHeight = height / (oldHeight * 1f);
                        scaleWidth = scaleHeight;
                    } else {
                        scaleWidth = width / (oldWidth * 1f);
                        scaleHeight = height / (oldHeight * 1f);
                    }
                } else {
                    scaleWidth = width / (oldWidth * 1f);
                    scaleHeight = height / (oldHeight * 1f);
                }
            } else if (width > 0) {
                scaleWidth = width / (oldWidth * 1f);
                scaleHeight = scaleWidth;
            } else if (height > 0) {
                scaleHeight = height / (oldHeight * 1f);
                scaleWidth = scaleHeight;
            }

            // 转为整型比例(四舍五入)
            int newWidth = Math.round(oldWidth * scaleWidth);
            int newHeight = Math.round(oldHeight * scaleHeight);

            BufferedImage newImage = null;
            if (mode == 1) {
                Graphics2D g2d = srcImage.createGraphics();
                newImage = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT); // 创建一个透明图像
                g2d.setBackground(Color.WHITE);
                g2d.clearRect(0, 0, width, height); // 使用当前绘图表面的背景色进行填充来清除指定的矩形
                g2d.dispose(); // 释放图像资源
                newImage.getGraphics().drawImage(srcImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), (width - newWidth) / 2, (height - newHeight - 1) / 2, newWidth, newHeight, Color.WHITE, null); // 合并缩略图与底图
            } else {
                Graphics2D g2d = srcImage.createGraphics();
                newImage = g2d.getDeviceConfiguration().createCompatibleImage(newWidth, newHeight, Transparency.TRANSLUCENT); // 创建一个透明图像
                g2d.dispose(); // 释放图像资源
                newImage.getGraphics().drawImage(srcImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null); // 合并缩略图与底图
            }
            File file = new File(thumbPath);
            FileUtils.createDirs(file.getParent(), true); // 先创建文件夹
            ImageIO.write(newImage, "png", file);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取图片规格(宽x高)
     *
     * @param is 输入流
     * @return 验证码
     */
    public static String getSpec(InputStream is) {
        String spec = null;
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(is);
            spec = bi.getWidth() + "x" + bi.getHeight();// 宽x高
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bi != null) {
                bi.flush();
            }
        }
        return spec;
    }
    
    /**
     * 获取图片规格(宽x高)
     *
     * @param is 输入流
     * @return 验证码
     */
    public static String getSpec(File file) {
    	FileInputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        String spec = null;
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(is);
            spec = bi.getWidth() + "x" + bi.getHeight();// 宽x高
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bi != null) {
                bi.flush();
            }
            if(is!=null){
            	try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        return spec;
    }
    
    /**
     * 获取图片规格(宽x高)
     *
     * @param path 图片地址
     * 
     */
    public static String getSpec(String path) {
    	File file=new File(path);
    	if(!file.exists()){
    		return "0x0";
    	}
    	FileInputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        String spec = null;
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(is);
            spec = bi.getWidth() + "x" + bi.getHeight();// 宽x高
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bi != null) {
                bi.flush();
            }
            if(is!=null){
            	try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        return spec;
    }
    
    /**
     * 创建图片文件
     *
     * @param html
     * @return 图片路径
     */
    public static boolean createImgSrc(int w,int h,String filePath,Color color) {
    	boolean flag=false;
    	
		File file=new File(filePath);
		BufferedImage buffImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		//BufferedImage buffImage = new BufferedImage(280, 200, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = buffImage.createGraphics();
		//g2d.setBackground(Color.WHITE);
		g2d.setBackground(color);
		g2d.clearRect(0, 0, w, h);
	   
        try {
        	//String formatName="";
        	
        	filePath.substring(filePath.lastIndexOf("."));
			FileUtils.createDirs(file.getParent(), true);
			flag=ImageIO.write(buffImage, "jpg", file);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 先创建文件夹
        
		return flag;
       
    }

    /**
     * 获取HTML中图片列表
     *
     * @param html
     * @return 图片列表
     */
    public static List<String> getImgSrc(String html) {
        Pattern pattern = Pattern.compile("<img\\s+(?:[^>]*)src\\s*=\\s*([^>]+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(html);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group(1);
            if (group != null) {
                if (group.startsWith("'")) {
                    list.add(group.substring(1, group.indexOf("'", 1)));
                } else if (group.startsWith("\"")) {
                    list.add(group.substring(1, group.indexOf("\"", 1)));
                } else {
                    list.add(group.split("\\s")[0]);
                }
            }
        }
        return list;
    }
}
