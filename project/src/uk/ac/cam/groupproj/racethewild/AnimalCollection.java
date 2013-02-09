package uk.ac.cam.groupproj.racethewild;

import java.util.List;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;

/*
 *  TODOs
 *  Implement onPause() and other transition state methods for fragments
 *  Some more stuff as well
 */

public class AnimalCollection extends FragmentActivity {
	
	Engine e = Engine.get();
	List<Animal> animals = e.getAllAnimals();

	@SuppressLint("NewApi") //Allows us to have the upEnabled setting. Could remove later. Android 4.1 Feature.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {
			return;
		}
		
		@SuppressWarnings("unused")
		InfoFragment infoFragment = new InfoFragment();
		ListFragment listFragment = new ListFragment();
		
		listFragment.setArguments(getIntent().getExtras());
		
//		/getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, listFragment).commit();
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

}
