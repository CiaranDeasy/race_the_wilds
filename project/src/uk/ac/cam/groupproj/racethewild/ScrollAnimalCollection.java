package uk.ac.cam.groupproj.racethewild;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import uk.ac.cam.groupproj.racethewild.ScrollMapScene.ScrollViewer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

public class ScrollAnimalCollection extends Activity implements OnTouchListener {

	ScrollViewer backgroundDisplay;
	Bitmap background;
	Engine e;
	ArrayList<CollectionDisplayAnimal> animals;  //Change to a special displayAnimal class in future.	
	
	float currentCentery;

	int screenwidth;  //you get the idea.
	int screenheight;
	
	float xcoord = 40; //coord of last animal
	float ycoord = 40;

	float PreTouchy;
	float RelTouchy; //most recent touch point.
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		e = Engine.get();
		super.onCreate(savedInstanceState);
		backgroundDisplay = new ScrollViewer(this);
		backgroundDisplay.setOnTouchListener(this);

	
		try {

			InputStream BGinputstream = getAssets().open("me.jpg");

			background = BitmapFactory.decodeStream(BGinputstream);
			BGinputstream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screenwidth = displaymetrics.widthPixels;
		screenheight = displaymetrics.heightPixels;
		currentCentery = 0;
		PreTouchy = 0;
		
		animals = new ArrayList<CollectionDisplayAnimal>();

	
		
		for(Integer animalInt : e.getStats().getGreyAnimals())
		{
			Animal a = e.getAnimal(animalInt);
			try {
				InputStream is = getAssets().open(a.getSpritePath());
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				is.close();
				CollectionDisplayAnimal animal = new CollectionDisplayAnimal(xcoord, ycoord, a.getID(), bitmap,a.getColour(), a.getSpritePath());
				animals.add(animal);
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
		
			
			if(xcoord > (screenwidth-400))
			{
				ycoord+=200;
				xcoord=40;
			} else xcoord+=200;
			
			
		}
		ycoord+=300;
		xcoord = 40;
		for(Integer animalInt : e.getStats().getBlackAnimals())
		{
			Animal a = e.getAnimal(animalInt);
			try {
				InputStream is = getAssets().open(a.getSpritePath());
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				is.close();
				CollectionDisplayAnimal animal = new CollectionDisplayAnimal(xcoord, ycoord, a.getID(), bitmap,a.getColour(), a.getSpritePath());
				animals.add(animal);
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}

			if(xcoord > (screenwidth-400))
			{
				ycoord+=200;
				xcoord=40;
			} else xcoord+=200;
			
			
		}
		
			
		
		
		


		RelativeLayout layout =  (RelativeLayout)View.inflate(this, R.layout.activity_scroll_animal_collection,null);
		layout.addView(backgroundDisplay,0);

		setContentView(layout);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_scroll_map_scene, menu);
		return true;
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		backgroundDisplay.pause();
	}

	protected void onResume()
	{
		super.onResume();
		backgroundDisplay.resume();
	}

	class ScrollViewer extends SurfaceView implements Runnable
	{

		Thread t = null; //gfx thread
		SurfaceHolder holder;
		boolean isItOkToRender = false;


		public ScrollViewer(Context context) {
			super(context);
			holder = getHolder();
			
		}

		public void pause(){
			isItOkToRender = false;
			while(true)
			{
				try{
					t.join();

				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				break;

			}
		}

		public void resume()
		{
			isItOkToRender = true;
			t = new Thread(this);
			t.start();
		}

		public void run() {
			while(isItOkToRender)
			{
				if (!holder.getSurface().isValid())
				{
					continue;
				}

				Canvas c = holder.lockCanvas();
				c.drawBitmap(background, new Rect(0,
						0,
						background.getWidth(),
						background.getHeight()), new Rect(0,0,screenwidth,screenheight) , null);
				for( CollectionDisplayAnimal a : animals)
				{
					a.onDraw(c, currentCentery, screenwidth, screenheight);
				}
				holder.unlockCanvasAndPost(c);
			}

		}

	


}
	
	public boolean onTouch(View arg0, MotionEvent arg1) {

		try {
			Thread.sleep(16);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		switch(arg1.getAction()){
		case MotionEvent.ACTION_DOWN:{
	
			PreTouchy = currentCentery;
			float RelTouchx = arg1.getX();
			RelTouchy = arg1.getY();

			float xForCollisionChecker = RelTouchx;
			float yForCollisionChecker = currentCentery - screenheight/2 + RelTouchy;
			
			for (BitmapDisplayAnimal a : animals)
			{
	
				if(a.collisionCheck(xForCollisionChecker,yForCollisionChecker))
				{
					
						
						
						/*Intent intent = new Intent(this, AnimalScene.class);  
						//TODO: change to proper scene when implemented properly!
						intent.putExtra(Engine.ANIMAL_NUMBER_MESSAGE, a.animalCode);
						startActivity(intent);*/
				
					
					//TODO: Implement the real method here which moves us to animal screen.

				}
			}



			break;
		}

		case MotionEvent.ACTION_MOVE:{
			
			currentCentery= PreTouchy + RelTouchy - arg1.getY();
			
			if(currentCentery>ycoord) currentCentery = ycoord;
			if(currentCentery<0) currentCentery = 0;
			

			break;
		}

		}

		return true;
	}
}
