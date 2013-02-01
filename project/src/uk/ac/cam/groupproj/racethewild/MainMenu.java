package uk.ac.cam.groupproj.racethewild;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainMenu extends Activity {
	
	//public final static String ENGINE_MESSAGE = "uk.ac.cam.groupproj.racethewild.ENGINE";

	//Engine engine;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		//engine = new Engine();
	}
	
	public void moveToCollection(View view) {
		Intent intent = new Intent(this, AnimalCollection.class);
		
		//intent.putExtra(ENGINE_MESSAGE, engine);  //for when we start sending around Engine.
		startActivity(intent);
	}
	
	public void moveToNodeMap(View view) {
		Intent intent = new Intent(this, ScrollMapScene.class);  //TODO: change to NodeMapScene when implemented properly!
		intent.putExtra(Engine.NODEPASS_MESSAGE,new Node("island.jpg"));
		
		//intent.putExtra(ENGINE_MESSAGE, engine);  //for when we start sending around Engine.
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}

}
// Random change to test commits via Eclipse.
