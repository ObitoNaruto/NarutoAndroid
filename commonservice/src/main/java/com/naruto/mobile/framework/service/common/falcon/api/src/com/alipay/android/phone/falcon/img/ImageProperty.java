package com.naruto.mobile.framework.service.common.falcon.api.src.com.alipay.android.phone.falcon.img;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageProperty {

	private	int[] imageData;
	private	int width;
	private int height;
	
	public void InitImage(File file)
	{
		if (file != null) {
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Bitmap imgmap = BitmapFactory.decodeStream(fileInputStream);
			this.width = imgmap.getWidth();
			this.height = imgmap.getHeight();
			this.imageData = new int[this.width * this.height];
			imgmap.getPixels(this.imageData,0,this.width,0,0,this.width,this.height);
		}
		
	}
	
	public int[] getImageData()
	{
		return this.imageData;
	}
	
	public int getWidth()
	{
		return this.width;
	}
	
	public int getHeight()
	{
		return this.height;
	}
}
