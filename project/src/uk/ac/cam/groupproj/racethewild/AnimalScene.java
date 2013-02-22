package uk.ac.cam.groupproj.racethewild;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class AnimalScene extends Activity {

	Engine e = Engine.get();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animal_scene);
		// Get the message from the intent
	    Intent intent = getIntent();
	    int animalID = intent.getIntExtra(MainMenu.ANIMAL_ID, 0);
	    try {
	    	Animal animal = e.getAnimal(animalID);
	    	String name = animal.getName();
		    // Create the text view
		    TextView textView = (TextView) findViewById(R.id.animalName);
		    textView.setText(name);
		    TextView textview2 = (TextView) findViewById(R.id.animalFact);
		    
		    if(animal.getColour() == Colour.Black){
		    	String facts = animal.getFacts();
		    	textview2.setText(facts);
		    } else if(animal.getColour()==Colour.Grey){
		    	String hints = animal.getHint();
		    	textview2.setText(hints);
		    }
		    
		    Bitmap image = null;
		    
			try {
	
				InputStream BGinputstream = getAssets().open(animal.getGraphicPath());
	
				image = BitmapFactory.decodeStream(BGinputstream);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	
			if(image != null){
				ImageView imageView = (ImageView) findViewById(R.id.animalImg);
				imageView.setImageBitmap(image);
			}
	    } catch(AnimalNotFoundException e) {
	    	System.err.println("Attempted to draw animal screen for non-existent animal #" + 
	    			animalID);
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_animal_scene, menu);
		return true;
	}

}
