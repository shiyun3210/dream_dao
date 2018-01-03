package com.fm.file.commons.images; 
   
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;


/**
 * Thumbnailator 是一个优秀的图片处理的Google开源Java类库  
 * 支持的处理操作：图片缩放，区域裁剪，水印，旋转，保持比例
 * @author syf
 *
 */
public class ImageHandle{ 
    
	/**
	 * 按照原图清晰图，宽度不变，根据高度切割图片
	 * @param resourceFile
	 * @throws Exception
	 */
	public static void cutImage(String resourceFile,int cutHeight,String cutSrcPath){
		File file = new File(resourceFile);
		InputStream in = null;
		try {
			in =  new FileInputStream(file);
			BufferedImage sourceImg = ImageIO.read(in);
	        String imgFormat = resourceFile.substring(resourceFile.lastIndexOf(".")+1);
	        int height = sourceImg.getHeight();
	        int width = sourceImg.getWidth();
	        int count = height%cutHeight>0?height/cutHeight+1:height/cutHeight;
	        int x = 0;
	        int y = 0;
	        for(int i=1;i<=count;i++){
	        	y = i==1?0:cutHeight*(i-1);
	        	cutHeight = i==count?height-(cutHeight*(i-1)):cutHeight;
				Thumbnails.of(sourceImg).sourceRegion(x, y, width, cutHeight).size(width, cutHeight).outputFormat(imgFormat).outputQuality(1d).toFile(cutSrcPath+i);
	        }
	        System.out.println("切割成功！"+cutSrcPath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}