package uk.ac.cam.groupproj.racethewild;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainMenu extends Activity {

	// public final static String ENGINE_MESSAGE =
	// "uk.ac.cam.groupproj.racethewild.ENGINE";

	Engine engine;
	SatNavUpdate snu;
	int movePoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		Engine.initialise(this); // Initialise the engine.
		engine = Engine.get();
		snu = engine.fetchSatNavData(getApplicationContext());
		movePoints = snu.getMovePoints();
		TextView textView = (TextView) findViewById(R.id.infoText);
		textView.setText("Check in now for " + movePoints + " movement points");
	}

	public void moveToCollection(View view) {
		Intent intent = new Intent(this, ScrollAnimalCollection.class);

		// intent.putExtra(ENGINE_MESSAGE, engine); //for when we start sending
		// around Engine.
		startActivity(intent);
	}

	public void moveToNodeMap(View view) {
		Intent intent = new Intent(this, NodeScene.class); 
		startActivity(intent);
	}
	
	public void checkIn(View view){
		Intent intent = new Intent(this, CheckInScene.class);
		startActivity(intent);		
	}
	
	public void reset(View view){
		Engine.reset(this);
	}
	
	public void gpsdebug(View view){
		Intent intent = new Intent(this, GPSdebug.class);
		startActivity(intent);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}

}
