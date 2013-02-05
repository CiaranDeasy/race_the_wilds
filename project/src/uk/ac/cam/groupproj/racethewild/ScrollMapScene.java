package uk.ac.cam.groupproj.racethewild;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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

	float PreTouchx; //for when we scroll.
	float RelTouchx;
	float PreTouchy;
	float RelTouchy; //most recent touch point.

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mapDisplay = new ScrollViewer(this);
		mapDisplay.setOnTouchListener(this);

		Intent intent = getIntent();
		thisNode = (Node) intent.getSerializableExtra(Engine.NODEPASS_MESSAGE);

		try {
			InputStream BGinputstream = getAssets().open(thisNode.getBackground());

			background = BitmapFactory.decodeStream(BGinputstream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		currentCenterx = background.getWidth()/2;
		PreTouchx = currentCenterx;
		currentCentery = background.getHeight()/2;
		PreTouchy = currentCentery;

		animals = new ArrayList<BitmapDisplayAnimal>();
		for(int x=0; x<20; x++)
		{
			try {
				float xcoord = (float) (Math.random()*background.getWidth());
				float ycoord = (float) (Math.random()*background.getHeight());
				InputStream is = getAssets().open("donkey.png");
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				BitmapDisplayAnimal animal = new BitmapDisplayAnimal(xcoord, ycoord, 1, bitmap);
				animals.add(animal);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

		@Override
		public void run() {
			while(isItOkToRender)
			{
				if (!holder.getSurface().isValid())
				{
					continue;
				}

				Canvas c = holder.lockCanvas();
				c.drawBitmap(background, new Rect((int)currentCenterx - (screenwidth>>1),
						(int)currentCentery - (screenheight>>1),
						(int)currentCenterx + (screenwidth>>1),
						(int)currentCentery + (screenheight>>1)), new Rect(0,0,screenwidth,screenheight) , null);
				for( BitmapDisplayAnimal a : animals)
				{
					a.onDraw(c, currentCenterx, currentCentery, screenwidth, screenheight);
				}
				holder.unlockCanvasAndPost(c);
			}

		}



	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub

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
				System.out.print("hello");
				if(a.collisionCheck(xForCollisionChecker,yForCollisionChecker))
				{
					//TODO: Implement the real method here which moves us to animal screen.
					//This remove as is causes concurrency bugs; add synchronized here and in graphics thread.

					animals.remove(a);
					break;
				}
			}



			break;
		}

		case MotionEvent.ACTION_MOVE:{
			currentCenterx= PreTouchx + RelTouchx - arg1.getX();
			currentCentery= PreTouchy + RelTouchy - arg1.getY();
			if(currentCenterx>background.getWidth()-screenwidth/2) currentCenterx = background.getWidth()-screenwidth/2;
			if(currentCenterx<screenwidth/2) currentCenterx = screenwidth/2;
			if(currentCentery>background.getHeight()-screenheight/2) currentCentery = background.getHeight()-screenheight/2;
			if(currentCentery<screenheight/2) currentCentery = screenheight/2;

			break;
		}

		}

		return true;
	}

}
