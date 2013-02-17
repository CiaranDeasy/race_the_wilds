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
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

public class AnimalScanner extends Activity implements OnTouchListener {

	public int screenwidth;
	public int screenheight;
	private Bitmap background;
	private Animal animal;
	private ScAnimal scanimal;
	private ScanViewer scanviewer;
	int pointscosted=0;
	Engine engine;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		engine = Engine.get();
		
		
		Intent intent = getIntent();
	    int animalID = intent.getIntExtra(MainMenu.ANIMAL_ID, 0);
		animal = engine.getAnimal(animalID);
		String toLoadAnimal = animal.getSpritePath();
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screenwidth = displaymetrics.widthPixels;
		screenheight = displaymetrics.heightPixels;
		
		scanimal = new ScAnimal(screenwidth>>1, screenheight>>1, toLoadAnimal);
		scanviewer = new ScanViewer(this);
		scanviewer.setOnTouchListener(this);
		
		

		RelativeLayout layout =  (RelativeLayout)View.inflate(this, R.layout.activity_animal_scanner, null);
		layout.addView(scanviewer,0);

		setContentView(layout);

		
		
	}
	
	@Override
	public void onBackPressed() { 
		//HAHAHAHAHA This stops people backing out for now.
		
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
		Thread t = null;
		Boolean alive = false;
		float x;
		int hp = 50;
		float y;
		
		public ScAnimal(float x, float y, String Sprite)
		{
			this.x=x;
			this.y=y;
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
			c.drawBitmap(animalSprite,x,y, null);
		}
		
		public boolean collisionCheck (float touchX, float touchY)
		{
			if((touchX > x-animalSprite.getWidth()/2) 
					&& (touchX < x+animalSprite.getWidth()/2) 
					&& (touchY > y-animalSprite.getHeight()/2)
					&& (touchY < y +animalSprite.getHeight()/2))
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
				x+=-20 + Math.random()*40;
				y+=-20 + Math.random()*40;
				
				if(x>screenwidth) x=screenwidth-10;
				if(y>screenheight) y = screenheight-10;
				if(x<0)x=10;
				if(y<0)y=10;
				
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
				
			}  
			
			pointscosted++;
			if(scanimal.hp==0)
			{
				Intent intent = new Intent(this, AnimalScene.class);  
				intent.putExtra(MainMenu.ANIMAL_ID, animal.getID());
	
			finish();
			startActivity(intent);
			}
			
			
		
		}

		return true;
	}

	
}
