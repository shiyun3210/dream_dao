package com.fm.file.util;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeUtils {
	
	private static final String CHARSET = "utf-8";
	private static final String FORMAT_NAME = "jpg";

	// LOGO宽度
	private static final int WIDTH = 65;
	// LOGO高度
	private static final int HEIGHT = 65;
	
	
	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;
	
	public static void main(String[] args) {
		String text = "http://www.51lxsh.com";
		try {
			generateLogoQRCode(300,text, new FileInputStream(new File("f://aaaa.jpg")) , new ByteArrayOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
//		generateQRCode("f://test.jpg","http://www.baidu.com");
	}
	/**
	 * 生成简单二维码图
	 * @param tagFile
	 * @param content
	 * @return
	 */
	public static String generateQRCode(String tagFile,String content){
		MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		Map<EncodeHintType,String> hints = new HashMap<EncodeHintType,String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		try {
			BitMatrix bitMatrix = multiFormatWriter.encode(content,BarcodeFormat.QR_CODE, 300, 300, hints);
			writeToFile(bitMatrix, "jpg",  new File(tagFile));
		} catch (WriterException e1) {
			e1.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 生成内嵌logo图的二维码
	 * @param content
	 * @param imgPath
	 * @param destPath
	 * @param needCompress
	 * @throws Exception
	 */
	public static void generateLogoQRCode(int qrcodeSize,String content, InputStream logoIns,ByteArrayOutputStream out) throws Exception {
		BufferedImage image = createImage(qrcodeSize,content, logoIns,true);
		
		ImageIO.write(image, FORMAT_NAME, out);
	}


	private static BufferedImage toBufferedImage(BitMatrix matrix){
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
			}
		}
		return image;
	}
	
	private static void writeToFile(BitMatrix matrix, String format, File file){
		BufferedImage image = toBufferedImage(matrix);
		try {
			ImageIO.write(image, format, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static BufferedImage createImage(int qrcodeSize,String content, InputStream logoIns, boolean needCompress) throws Exception {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
				BarcodeFormat.QR_CODE, qrcodeSize, qrcodeSize, hints);
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
						: 0xFFFFFFFF);
			}
		}
		if (logoIns == null ) {
			return image;
		}
		// 插入图片
		insertImage(qrcodeSize,image, logoIns, needCompress);
		return image;
	}

	/**
	 * 插入LOGO
	 * @param source
	 * @param logoImgFile
	 * @param needCompress
	 * @throws Exception
	 */
	private static void insertImage(int qrcodeSize,BufferedImage source, InputStream logoIns, boolean needCompress) throws Exception {
		Image src = ImageIO.read(logoIns);
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		if (needCompress) { // 压缩LOGO
			if (width > WIDTH) {
				width = WIDTH;
			}
			if (height > HEIGHT) {
				height = HEIGHT;
			}
			Image image = src.getScaledInstance(width, height,Image.SCALE_SMOOTH);
			BufferedImage tag = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
			Graphics g = tag.getGraphics();
			g.drawImage(image, 0, 0, null); // 绘制缩小后的图
			g.dispose();
			src = image;
		}
		// 插入LOGO
		Graphics2D graph = source.createGraphics();
		int x = (qrcodeSize - width) / 2;
		int y = (qrcodeSize - height) / 2;
		graph.drawImage(src, x, y, width, height, null);
		Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);//圆角 
		graph.setStroke(new BasicStroke(3f));//圆角
		graph.draw(shape);
		graph.dispose();
	}

}
