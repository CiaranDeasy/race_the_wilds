package uk.ac.cam.groupproj.racethewild;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
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

public class ScrollMapScene extends Activity implements OnTouchListener{

	ScrollViewer mapDisplay;
	Bitmap background;
	Engine e;
	ArrayList<BitmapDisplayAnimal> animals;  //Change to a special displayAnimal class in future.
	Node thisNode;
	
	
	float currentCenterx;  //current center of the screen, in terms of (0,0) being the top leftmost part of the bg image.
	float currentCentery;

	int screenwidth;  //you get the idea.
	int screenheight;
	
	int bgWidth;
	int bgHeight;

	float PreTouchx; //for when we scroll.
	float RelTouchx;
	float PreTouchy;
	float RelTouchy; //most recent touch point.
	
	
	public void moveToCollection(View view) {
		Intent intent = new Intent(this, ScrollAnimalCollection.class);

		// intent.putExtra(ENGINE_MESSAGE, engine); //for when we start sending
		// around Engine.
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		e = Engine.get();
		super.onCreate(savedInstanceState);
		mapDisplay = new ScrollViewer(this);
		mapDisplay.setOnTouchListener(this);

		try {
			thisNode = e.lookupNode(e.getStats().getCurrentNode());
			//thisNode = e.lookupNode("Arctic");
		} catch (NodeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {

			InputStream BGinputstream = getAssets().open(thisNode.getBackground());


			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Config.RGB_565;
			background = BitmapFactory.decodeStream(BGinputstream, null, options);
			bgHeight = background.getHeight()*3;
			
			bgWidth = background.getWidth()*3;
			//background = BitmapFactory.decodeStream(BGinputstream);
			BGinputstream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		currentCenterx = bgWidth/2;
		PreTouchx = currentCenterx;
		currentCentery = bgHeight/2;
		PreTouchy = currentCentery;

		animals = new ArrayList<BitmapDisplayAnimal>();

		for(Animal a : thisNode.getAnimalList()) //TODO: change to animal list
		{
			try {
				if(a.getColour()==Colour.Grey)
				{
					
					InputStream is = getAssets().open(a.getSpritePath());
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					is.close();
					float xcoord = (float) (bitmap.getWidth()/2 + Math.random()*(bgWidth - bitmap.getWidth()/2 ));
					float ycoord = (float) (bitmap.getHeight()/2 + Math.random()*(bgHeight- bitmap.getHeight()/2));
					BitmapDisplayAnimal animal = new BitmapDisplayAnimal(xcoord, ycoord, a.getID(), bitmap,a.getColour());
					animals.add(animal);
				}
				else if(a.getColour()==Colour.Black)
				{
					InputStream is = getAssets().open(a.getSpritePath());
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					is.close();
					
					for(int x=0; x<10; x++)
					{
					
						float xcoord = (float) (bitmap.getWidth()/2 + Math.random()*(bgWidth - bitmap.getWidth() ));
						float ycoord = (float) (bitmap.getHeight()/2 + Math.random()*(bgHeight- bitmap.getHeight()));
						BitmapDisplayAnimal animal = new BitmapDisplayAnimal(xcoord, ycoord, a.getID(), bitmap,a.getColour());
						animals.add(animal);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Collections.sort(animals);
			
		}
		
		


		RelativeLayout layout =  (RelativeLayout)View.inflate(this, R.layout.activity_scroll_map_scene, null);
		layout.addView(mapDisplay,0);

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
		mapDisplay.pause();
	}

	protected void onResume()
	{
		super.onResume();
		mapDisplay.resume();
		
		for (BitmapDisplayAnimal a : animals)
		{
			//checks if an animal has changed colour due to the animal scanner.
			if(a.colour == Colour.Grey){
				Animal anima;
				try {
					anima = e.getAnimal(a.animalCode);
					if(anima.getColour()==Colour.Black)
						a.colour = Colour.Black;
				} catch (AnimalNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		}
	}

	class ScrollViewer extends SurfaceView implements Runnable
	{

		Thread t = null; //gfx thread
		SurfaceHolder holder;
		boolean isItOkToRender = false;


		public ScrollViewer(Context context) {
			super(context);
			holder = getHolder();
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			screenwidth = displaymetrics.widthPixels;
			screenheight = displaymetrics.heightPixels;
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
				float widthratio = (float)background.getWidth()/(float)bgWidth;
				float heightratio = (float)background.getHeight()/(float)bgHeight;
				c.drawBitmap(background, new Rect((int)((currentCenterx - (screenwidth>>1))*widthratio),
						(int)((currentCentery - (screenheight>>1))*heightratio),
						(int)((currentCenterx + (screenwidth>>1))*widthratio),
						(int)((currentCentery + (screenheight>>1))*heightratio)), new Rect(0,0,screenwidth,screenheight) , null);
				for( BitmapDisplayAnimal a : animals)
				{
					a.onDraw(c, currentCenterx, currentCentery, screenwidth, screenheight);
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
			PreTouchx = currentCenterx;
			PreTouchy = currentCentery;
			RelTouchx = arg1.getX();
			RelTouchy = arg1.getY();

			float xForCollisionChecker = currentCenterx - screenwidth/2 + RelTouchx;
			float yForCollisionChecker = currentCentery - screenheight/2 + RelTouchy;
			
			for (BitmapDisplayAnimal a : animals)
			{
	
				if(a.collisionCheck(xForCollisionChecker,yForCollisionChecker))
				{
					if(a.colour == Colour.Grey) {
								
						Intent intent = new Intent(this, AnimalScanner.class);  
						//Moves us to the scanner scene.
						intent.putExtra(MainMenu.ANIMAL_ID, a.animalCode);
						startActivity(intent);
				
					
					//The real method here which moves us to animal screen.
				
						
					}
					
					//TODO: Implement the real method here which moves us to animal screen.

				}
			}



			break;
		}

		case MotionEvent.ACTION_MOVE:{
			currentCenterx= PreTouchx + RelTouchx - arg1.getX();
			currentCentery= PreTouchy + RelTouchy - arg1.getY();
			if(currentCenterx>bgWidth-screenwidth/2) currentCenterx = bgWidth-screenwidth/2;
			if(currentCenterx<screenwidth/2) currentCenterx = screenwidth/2;
			if(currentCentery>bgHeight-screenheight/2) currentCentery = bgHeight-screenheight/2;
			if(currentCentery<screenheight/2) currentCentery = screenheight/2;

			break;
		}

		}

		return true;
	}

}
