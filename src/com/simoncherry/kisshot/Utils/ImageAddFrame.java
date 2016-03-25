package com.simoncherry.kisshot.Utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

public class ImageAddFrame{
		
	public Bitmap AddFrameToImage(Bitmap bmp, Bitmap frameBitmap)
	{
		int width = bmp.getWidth();  
	    int height = bmp.getHeight();
	    int w = frameBitmap.getWidth();
	    int h = frameBitmap.getHeight();
	    float scaleX = width*1F / w;
	    float scaleY = height*1F / h;
	    Matrix matrix = new Matrix();
	    matrix.postScale(scaleX, scaleY);
	    Bitmap copyBitmap =  Bitmap.createBitmap(frameBitmap, 0, 0, w, h, matrix, true);
	    
	    int layColor = 0;    
	    int layA = 0;  
	    
	    for (int i = 0; i < width; i++)  
	    {  
	        for (int k = 0; k < height; k++)  
	        {
	        	layColor = copyBitmap.getPixel(i, k);
	        	layA = Color.alpha(layColor);
	        	if (layA > 0)
	            {  
	        		bmp.setPixel(i, k, layColor);
	            }
	        }  
	    }  
	    return bmp;
	}
}