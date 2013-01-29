package uk.ac.cam.groupproj.racethewild;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BitmapDisplayAnimal {
	
	public Bitmap bitmap;
	public float x;
	public float y;
	
	public void onDraw(Canvas c, float viewCenterx, float viewCentery, float screenWidth, float screenHeight){
		
		c.drawBitmap(bitmap, x - viewCenterx + screenWidth/2 - bitmap.getWidth()/2,
				y - viewCentery + screenHeight/2 - bitmap.getHeight()/2, null);
		
	}
	
	

}
