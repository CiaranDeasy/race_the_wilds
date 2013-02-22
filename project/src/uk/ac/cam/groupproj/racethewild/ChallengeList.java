package uk.ac.cam.groupproj.racethewild;

import java.util.List;



import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChallengeList extends ListActivity {
	 
	public final static String CHALLENGE_NAME ="uk.ac.cam.groupproj.racethewild.MESSAGE";
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	Engine e = Engine.get();

	
	public void onResume(){
		super.onResume();
		List<Challenge> challenges = e.getAllChallenges();
		String[] challengeString = new String[challenges.size()];
		int size = challenges.size();
		for(int i=0;i<size;i++){
			Challenge c = challenges.get(i);
			challengeString[i]=c.getText();
		}
 
		setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_challenge_list,challengeString));
 
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
 
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			    // When clicked, show a toast with the TextView text
			    String challengeName = (((TextView) view).getText()).toString();
			    runIntent(challengeName);
			}
		});
	}
	
	public void runIntent(String challengeName){
		Intent intent = new Intent(this, ChallengeActivity.class);
		List<Challenge> challenges = e.getAllChallenges();
		Challenge challenge = null;
		for(Challenge c:challenges){
			if (c.getText().equals(challengeName)){
				challenge = c;
			}
		}
		String message = ((Integer)challenge.getChallengeID()).toString();
    	intent.putExtra(CHALLENGE_NAME, message);
		startActivity(intent);
	}
 
}