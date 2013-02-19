package uk.ac.cam.groupproj.racethewild;

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AnimalScanner extends Activity implements OnTouchListener {

	public int screenwidth;
	public int screenheight;
	private Bitmap background;
	private int movePoints;
	private Animal animal;
	private ScAnimal scanimal;
	private ScanViewer scanviewer;
	int pointscosted=0;
	Engine engine;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		engine = Engine.get();
		movePoints = engine.getStats().getCurrentMovePoints();
		
		
		Intent intent = getIntent();
	    int animalID = intent.getIntExtra(MainMenu.ANIMAL_ID, 0);
	    try {
	    	animal = engine.getAnimal(animalID);
	    } catch (AnimalNotFoundException e) {
	    	// Shouldn't be possible, just kill the app if this happens.
	    	System.err.println("Animal #" + animalID + 
	    			" passed to the AnimalScanner doesn't exist. Killing the app.");
	    	System.exit(-4);
	    }
		String toLoadAnimal = animal.getSpritePath();
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screenwidth = displaymetrics.widthPixels;
		screenheight = displaymetrics.heightPixels;
		
		try {

			InputStream BGinputstream = getAssets().open("jumperwarped.jpg");

			background = BitmapFactory.decodeStream(BGinputstream);
			BGinputstream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		scanimal = new ScAnimal(screenwidth>>1, screenheight>>1, toLoadAnimal, 500, 20);
		scanviewer = new ScanViewer(this);
		scanviewer.setOnTouchListener(this);
		
		

		RelativeLayout layout =  (RelativeLayout)View.inflate(this, R.layout.activity_animal_scanner, null);
		layout.addView(scanviewer,0);

		setContentView(layout);

		
		
	}
	
	//@Override
	//public void onBackPressed() { 
		//HAHAHAHAHA This stops people backing out for now.
		
	//}
	public void backButton (View view){
		onBackPressed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_animal_scanner, menu);
		return true;
	}


	@Override
	protected void onPause()
	{
		super.onPause();
		scanviewer.pause();
		scanimal.pause();
	}

	protected void onResume()
	{
		super.onResume();
		scanviewer.resume();
		scanimal.resume();
	}
	
	class ScAnimal implements Runnable {
		
		Bitmap animalSprite;
		Paint color;
		Thread t = null;
		Boolean alive = false;
		ColorFilter[] colours = {new LightingColorFilter(Color.WHITE,1),
				new LightingColorFilter(Color.RED,1),
				new LightingColorFilter(Color.BLUE,1),
				new LightingColorFilter(Color.GREEN,1),
				new LightingColorFilter(Color.YELLOW,1),
				new LightingColorFilter(Color.BLACK,1)};
		int colouritem=0;
		float x;
		int maxhp;
		int hp;
		float y;
		int speed;
		
		public ScAnimal(float x, float y, String Sprite, int HP, int speed)
		{
			this.x=x;
			this.y=y;
			color = new Paint();
			this.hp = HP;
			maxhp = HP;
			this.speed = speed;
			color.setColorFilter(new LightingColorFilter(Color.WHITE,1));
			try {
				InputStream is = getAssets().open(Sprite);
			
				animalSprite = BitmapFactory.decodeStream(is);
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
		
		public void onDraw(Canvas c){
			c.drawBitmap(animalSprite,x,y, color);
		}
		
		public void updateColour()
		{
			colouritem++;
			if(colouritem >= colours.length) colouritem=0;
			color.setColorFilter(colours[colouritem]);
		}
		
		public boolean collisionCheck (float touchX, float touchY)
		{
			if((touchX > x) 
					&& (touchX < x+animalSprite.getWidth()) 
					&& (touchY > y)
					&& (touchY < y +animalSprite.getHeight()))
				return true;
			
			return false;
		}
		
		public void pause(){
			alive = false;
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
			alive = true;
			t = new Thread(this);
			t.start();
		}
	

		@Override
		public void run() {
			
			while(alive)
			{
				x+=-speed + Math.random()*(speed<<1);
				y+=-speed + Math.random()*(speed<<1);
				
				if(x>screenwidth-animalSprite.getWidth()) x=screenwidth-animalSprite.getWidth();
				if(y>screenheight-animalSprite.getHeight()) y = screenheight-animalSprite.getHeight();
				if(x<0)x=0;
				if(y<0)y=0;
				
				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		
	}

	class ScanViewer extends SurfaceView implements Runnable
	{

		
		Thread t = null; //gfx thread
		SurfaceHolder holder;
		boolean isItOkToRender = false;


		public ScanViewer(Context context) {
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
			
				scanimal.onDraw(c);
				holder.unlockCanvasAndPost(c);
			}

		}



	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		try {
			Thread.sleep(16);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(event.getAction()==MotionEvent.ACTION_DOWN 
				|| event.getAction()==MotionEvent.ACTION_MOVE ){
		
			float Touchx = event.getX();
			float Touchy = event.getY();

			if(scanimal.collisionCheck(Touchx, Touchy))
			{
				scanimal.hp--;
				scanimal.updateColour();
				
			}  
			
			if(movePoints>pointscosted/2)
			{
				pointscosted++;
			}
			
			updateText();
			
			if(scanimal.hp==0)
			{
				scanimal.hp--;
				engine.getStats().payMovePoints(pointscosted/2);
				engine.changeColour(animal.getID(), Colour.Black, this);
				Intent intent = new Intent(this, AnimalScene.class);  
				intent.putExtra(MainMenu.ANIMAL_ID, animal.getID());
	
			finish();
			startActivity(intent);
			}
			
			
		
		}

		return true;
	}
	
	void updateText()
	{
	
		TextView movepointsText = (TextView) findViewById(R.id.pointscost);
		movepointsText.setText(getString(R.string.points_spent) + pointscosted/2);
		

		TextView percent = (TextView) findViewById(R.id.scanpercentage);
		percent.setText(getString(R.string.animal_percent_scanned) + ((scanimal.maxhp-scanimal.hp) * 100/ scanimal.maxhp));
	
		
		
	}

	
}
