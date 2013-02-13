package uk.ac.cam.groupproj.racethewild;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class CollectionDisplayAnimal extends BitmapDisplayAnimal {
	String bmpName;
	Boolean alive = true;

	public CollectionDisplayAnimal(float xcoord, float ycoord, int animalCode,
			Bitmap bitMap, Colour colour,String bitmapName) {
		
		super(xcoord, ycoord, animalCode, bitMap, colour);
		bmpName = bitmapName;
	
	}
	
	public Boolean onScreen(float currentCenterY, float screenWidth, float screenHeight)
	{
		float yCoord = y - currentCenterY + screenHeight/2;
		if(x >0 && x< screenWidth && yCoord >-200 && yCoord < screenHeight)
			return true;
		return false;
	}
	
public synchronized void onDraw(Canvas c, float currentCenterY, float screenWidth, float screenHeight){
		
	float yCoord = y - currentCenterY + screenHeight/2;
	
		if(x >0 && x< screenWidth && yCoord >-200 && yCoord < screenHeight && alive){	
		
		c.drawBitmap(bitmap,new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),new Rect((int)x,(int)yCoord, (int)x+200,(int) yCoord+200),null);
		} else {
			if(alive)
			{
				bitmap.recycle();
				alive=false;
			}
		}
		
	} 

}
