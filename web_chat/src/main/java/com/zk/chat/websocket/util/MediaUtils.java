package com.zk.chat.websocket.util;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.AudioInfo;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncoderProgressListener;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.FFMPEGLocator;
import it.sauronsoftware.jave.InputFormatException;
import it.sauronsoftware.jave.MultimediaInfo;
import it.sauronsoftware.jave.VideoAttributes;
import it.sauronsoftware.jave.VideoInfo;
import it.sauronsoftware.jave.VideoSize;

import java.io.File;

public class MediaUtils {
	//ffmpeg -i f:\asd1231.mp4 -map 0 -vcodec libx264 -acodec aac -f segment -segment_list f:\live\playlist.m3u8 -segment_list_flags +live -segment_time 10 f:\live\out%03d.ts
	
	//ffmpeg -i f:\abcabc.mp4 -acodec aac -vcodec libopenh264 -movflags empty_moov -brands mp42,avc1,iso6 -f mp4 -frag_size 70000000 -frag_duration 6000000 -min_frag_duration 1000 -y f:\output1.mp4
	
	//ffmpeg -i f:\abcabc.mp4 -acodec aac -vcodec libopenh264 -movflags empty_moov -brand mp42,avc1 -f mp4 -y f:\output1.mp4
	public static void handleAudio(String sourceFile,String targetFile){
		File file = new File(sourceFile);
		if(file.isFile()){
			AudioAttributes audioAtr = new AudioAttributes();
			audioAtr.setBitRate(new Integer(128000));
			audioAtr.setChannels(new Integer(2));
			audioAtr.setSamplingRate(new Integer(11025));
			audioAtr.setVolume(new Integer(100));
			audioAtr.setCodec("libmp3lame");
			
			try {
				Encoder encoder = new Encoder();
//				MultimediaInfo mediaInfo = encoder.getInfo(file);
				
//				AudioInfo audioInfo = mediaInfo.getAudio();
				
//				System.out.println("时长："+mediaInfo.getDuration());
				EncodingAttributes attrs = new EncodingAttributes();  
				attrs.setFormat("mp3");  
				attrs.setAudioAttributes(audioAtr);
//				attrs.setDuration(new Float(1000.5));
//				attrs.setOffset(new Float(0.00));
				encoder.encode(file, new File(targetFile), attrs);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InputFormatException e) {
				e.printStackTrace();
			} catch (EncoderException e) {
				//捕获不输出
				System.out.println("捕获");
			}
		}
	}
	
	public static void main(String[] args) {
		
		handleVideo("F:\\abcdd.mp4", "F:\\abcaaa.mp4");
	}
	public static void handleVideo(String sourceFile,String targetFile){
		File file = new File(sourceFile);
		if(file.isFile()){
			try {
				Encoder encoder = new Encoder();
				MultimediaInfo mediaInfo = encoder.getInfo(file);
				VideoInfo videoInfo = mediaInfo.getVideo();
				VideoAttributes videoAtr = new VideoAttributes();
				videoAtr.setBitRate(videoInfo.getBitRate());
				videoAtr.setFrameRate((int)videoInfo.getFrameRate());
				videoAtr.setSize(videoInfo.getSize());
				videoAtr.setCodec("mpeg4");
				
				AudioInfo audioInfo = mediaInfo.getAudio();
				AudioAttributes audioAtr = new AudioAttributes();
				audioAtr.setBitRate(audioInfo.getBitRate());
				audioAtr.setChannels(audioInfo.getChannels());
				audioAtr.setSamplingRate(audioInfo.getSamplingRate());
				audioAtr.setVolume(new Integer(100));
//				audioAtr.setCodec(AudioAttributes.DIRECT_STREAM_COPY);
				EncodingAttributes attrs = new EncodingAttributes();  
				attrs.setFormat("mp4");
				attrs.setAudioAttributes(audioAtr);
				attrs.setVideoAttributes(videoAtr);
				encoder.encode(file, new File(targetFile), attrs);
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InputFormatException e) {
				e.printStackTrace();
			} catch (EncoderException e) {
				e.printStackTrace();
				//捕获不输出
				System.out.println("捕获");
			}
		}
	}
	
}
