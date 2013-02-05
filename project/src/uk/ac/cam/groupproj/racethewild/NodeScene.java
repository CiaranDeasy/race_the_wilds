package uk.ac.cam.groupproj.racethewild;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.support.v4.app.NavUtils;

public class NodeScene extends Activity implements OnTouchListener {


	NodeViewer nodeDisplay;
	Engine e;
	Bitmap worldMapBG;
	ArrayList<DisplayNode> displayNodes;
	Node selectedScene;

	//current center of the screen, in terms of (0,0) being the top leftmost part of the bg image.
	float currentCentery;

	int screenwidth;  //you get the idea.
	int screenheight;

	float PreTouchy;
	float RelTouchy; //most recent touch point.


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		e = Engine.get();

		nodeDisplay = new NodeViewer(this);
		nodeDisplay.setOnTouchListener(this);
		
		try {

			InputStream BGinputstream = getAssets().open("sample_node_scene.png");

			worldMapBG = BitmapFactory.decodeStream(BGinputstream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		List<Node> nodes = e.getNodeList();
		displayNodes = new ArrayList<DisplayNode>();
		for(Node n: nodes)
		{
			InputStream is;
			try {
				is = getAssets().open(n.getSprite());
			
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			
			
			
			displayNodes.add(new DisplayNode(n.getRelX()*worldMapBG.getWidth(),
					n.getRelY()*worldMapBG.getHeight(), n.getName(), bitmap));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		
		RelativeLayout layout =  (RelativeLayout)View.inflate(this, R.layout.activity_node_scene, null);
		layout.addView(nodeDisplay,0);

		setContentView(layout); 
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


	public void moveToScrollMap(View view) {
		if(selectedScene!=null) 
		{
			
			Intent intent = new Intent(this, ScrollMapScene.class); 
			e.setCurrentNode(selectedScene.getName());

			startActivity(intent);
		}
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
			
			currentCentery = screenheight/2;
			
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
								c.drawBitmap(worldMapBG, new Rect(0,
										(int)currentCentery - (screenheight>>1),
										(int)worldMapBG.getWidth(),
										(int)currentCentery + (screenheight>>1)), new Rect(0,0,screenwidth/2,screenheight) , null);
								for( DisplayNode node : displayNodes)
								{
									node.onDraw(c, currentCentery, screenwidth, screenheight);
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
				PreTouchy = currentCentery;//only scroll on y axis
				float Touchx = arg1.getX();  
				RelTouchy = arg1.getY();
			
				float xForCollisionChecker = Touchx;
				float yForCollisionChecker = currentCentery - screenheight/2 + RelTouchy;

				for (DisplayNode node : displayNodes)
				{
		
					if(node.collisionCheck(xForCollisionChecker,yForCollisionChecker))
					{
						try {
							selectedScene = e.lookupNode(node.nodeName);
							
						} catch (NodeNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				break;
			}
			case MotionEvent.ACTION_MOVE:{
				currentCentery= PreTouchy + RelTouchy - arg1.getY();
				if(currentCentery>worldMapBG.getHeight()-screenheight/2) currentCentery = worldMapBG.getHeight()-screenheight/2;
				if(currentCentery<screenheight/2) currentCentery = screenheight/2;
			
				break;
			}
		}

		return true;
	}

}
