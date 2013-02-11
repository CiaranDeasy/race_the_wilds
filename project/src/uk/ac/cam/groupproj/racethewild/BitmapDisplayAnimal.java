package uk.ac.cam.groupproj.racethewild;



import android.graphics.Bitmap;

import android.graphics.Canvas;


public class BitmapDisplayAnimal implements Comparable<BitmapDisplayAnimal>{
	
	protected Bitmap bitmap;
	protected float x;
	protected float y;
	public final int animalCode;
	public Colour colour;
	
	public BitmapDisplayAnimal(float xcoord, float ycoord, int animalCode, Bitmap bitMap, Colour colour )
	{
		this.animalCode=animalCode;
		
		x = xcoord;
		y = ycoord;
		bitmap = bitMap;
		
		this.colour = colour;
		
	}
	
	public void onDraw(Canvas c, float viewCenterx, float viewCentery, float screenWidth, float screenHeight){
		
		float xCoord = x - viewCenterx + screenWidth/2 - bitmap.getWidth()/2;
		float yCoord = y - viewCentery + screenHeight/2 - bitmap.getHeight()/2;
		if(xCoord >-bitmap.getWidth() && xCoord < screenWidth && yCoord >-bitmap.getHeight() && yCoord < screenHeight)
		c.drawBitmap(bitmap,xCoord, yCoord, null);
		
	}
	
	public boolean collisionCheck (float touchX, float touchY)
	{
		if((touchX > x-bitmap.getWidth()/2) 
				&& (touchX < x+bitmap.getWidth()/2) 
				&& (touchY > y-bitmap.getHeight()/2)
				&& (touchY < y +bitmap.getHeight()/2))
			return true;
		
		return false;
	}


	public int compareTo(BitmapDisplayAnimal another) {
		if(y>another.y)	return 1;
		else if(y<another.y)return -1;
		return 0;
	}
	

}
