package uk.ac.cam.groupproj.racethewild;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class DisplayNode {

	private Bitmap bitmap;
	private float x;
	private float y;
	public String nodeName;
	
	public DisplayNode(float xcoord, float ycoord,String nodeName, Bitmap bitMap)
	{
		this.nodeName=nodeName;
		
		x = xcoord;
		y = ycoord;
		bitmap = bitMap;
		
		
	}
	
	public void onDraw(Canvas c, float viewCenterx, float viewCentery, float screenWidth, float screenHeight){
		
		float xCoord = x - viewCenterx + screenWidth/2 - bitmap.getWidth()/2;
		c.drawBitmap(bitmap,xCoord,y - viewCentery + screenHeight/2 - bitmap.getHeight()/2, null);
		
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
	

}