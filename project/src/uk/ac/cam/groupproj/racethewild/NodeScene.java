package uk.ac.cam.groupproj.racethewild;

import java.util.ArrayList;

import uk.ac.cam.groupproj.racethewild.ScrollMapScene.ScrollViewer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.support.v4.app.NavUtils;

public class NodeScene extends Activity implements OnTouchListener {


	NodeViewer nodeDisplay;
	Engine e;
	Bitmap background;
	ArrayList<Node> nodes;

	//current center of the screen, in terms of (0,0) being the top leftmost part of the bg image.
	float currentCentery;

	int screenwidth;  //you get the idea.
	int screenheight;

	float PreTouchy;
	float RelTouchy; //most recent touch point.


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		nodeDisplay = new NodeViewer(this);
		nodeDisplay.setOnTouchListener(this);

		setContentView(R.layout.activity_node_scene); //TODO Change this
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_node_scene, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onPause()
	{
		super.onPause();
		nodeDisplay.pause();
	}

	protected void onResume()
	{
		super.onResume();
		nodeDisplay.resume();
	}

	class NodeViewer extends SurfaceView implements Runnable
	{

		Thread t = null; //gfx thread
		SurfaceHolder holder;
		boolean isItOkToRender = false;


		public NodeViewer(Context context) {
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
				//				c.drawBitmap(background, new Rect((int)currentCenterx - (screenwidth>>1),
				//						(int)currentCentery - (screenheight>>1),
				//						(int)currentCenterx + (screenwidth>>1),
				//						(int)currentCentery + (screenheight>>1)), new Rect(0,0,screenwidth,screenheight) , null);
				//				for( BitmapDisplayAnimal a : animals)
				//				{
				//					a.onDraw(c, currentCenterx, currentCentery, screenwidth, screenheight);
				//				}
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
				PreTouchy = currentCentery;//only scroll on y axis
				float Touchx = arg1.getX();  
				RelTouchy = arg1.getY();
			
				float xForCollisionChecker = Touchx;
				float yForCollisionChecker = currentCentery - screenheight/2 + RelTouchy;

				break;
			}
			case MotionEvent.ACTION_MOVE:{
				currentCentery= PreTouchy + RelTouchy - arg1.getY();
				if(currentCentery>background.getHeight()-screenheight/2) currentCentery = background.getHeight()-screenheight/2;
				if(currentCentery<screenheight/2) currentCentery = screenheight/2;
			
				break;
			}
		}

		return true;
	}

}
