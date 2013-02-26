package uk.ac.cam.groupproj.racethewild;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.TextView;

public class ChallengeActivity extends Activity {

	Challenge challenge = null;
	TextView progressView;
	String progress;
	Engine e;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_challenge);

		Intent intent = getIntent();
		String message = intent.getStringExtra(ChallengeList.CHALLENGE_NAME);

		e = Engine.get();

		List<Challenge> challenges = e.getAllChallenges();
		int messageInt = Integer.parseInt(message);
		for(Challenge c:challenges){
			if (c.getChallengeID()==messageInt){
				challenge = c;
			}
		}

		message = challenge.getText();

		TextView challengeTitleTextView = (TextView) findViewById(R.id.challengeTitle);
		challengeTitleTextView.setTextSize(20);
		challengeTitleTextView.setText(message);

		progressView = (TextView) findViewById(R.id.challengeProgress);
		progressView.setTextSize(40);
		progressView.setText("Click to start challenge");


		Thread runChallenge = new Thread(){
			@Override
			public void run(){
				runChallenge(challenge);
			}
		};
		runChallenge.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_challenge, menu);
		return true;
	}

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message){
			progressView.setText(progress);
		}
	};

	public void runChallenge(Challenge challenge){
		if(challenge==null){
			return;
		}
		boolean tryChallenge = false;
		if(challenge.getType()==ChallengeType.distanceTime){
			tryChallenge = ChallengeController.requestMovementChallenge(getApplicationContext(), challenge.getTime(), challenge.getRepetitions());
		} else {
			tryChallenge = ChallengeController.requestJumpChallenge(getApplicationContext(), challenge.getTime(), challenge.getRepetitions());
		}
		
		if(!tryChallenge){
			return;
		}
		countDown(progressView);
		//TODO implement challenges
		progress = "Start Challenge";
		handler.sendEmptyMessage(0);
		if(challenge.getType()==ChallengeType.distanceTime){
			ChallengeController.startMovementChallenge(getApplicationContext());
			distanceChallengeStatus(challenge);
		} else {
			ChallengeController.startJumpChallenge(getApplicationContext());
			jumpChallengeStatus(challenge);
		}
	}

	public void distanceChallengeStatus(Challenge challenge){
		ChallengeStatus status = ChallengeController.checkMovementChallengeStatus(getApplicationContext());
		int distanceRemaining = challenge.getRepetitions() - status.distanceMoved;
		long timeRemaining = (challenge.getTime()) - (status.timeTaken/1000);
		while ((distanceRemaining > 0)&&(timeRemaining > 0)){
			status = ChallengeController.checkMovementChallengeStatus(getApplicationContext());
			distanceRemaining = challenge.getRepetitions() - status.distanceMoved;
			timeRemaining = (challenge.getTime()) - (status.timeTaken/1000);
			String distance = Integer.toString(distanceRemaining);
			String time = Long.toString(timeRemaining);
			progress = "Distance Remaining: " + distance + " Time Remaining: " + time;
			handler.sendEmptyMessage(0);
		}

		// just in case for some reason the above loop exited erroneously
		ChallengeController.stopAllGPSinteraction(getApplicationContext());
		
		if(distanceRemaining<0){
			progress = "Challenge Passed!";
			e.completeChallenge(challenge.getChallengeID(), this);
		} else {
			progress = "Challenge Failed";
		}
		handler.sendEmptyMessage(0);
	}
	
	public void jumpChallengeStatus(Challenge challenge){
		ChallengeStatus status = ChallengeController.checkJumpChallengeStatus(getApplicationContext());
		int distanceRemaining = challenge.getRepetitions() - status.distanceMoved;
		long timeRemaining = (challenge.getTime()) - (status.timeTaken/1000);
		while ((distanceRemaining > 0)&&(timeRemaining > 0)){
			status = ChallengeController.checkJumpChallengeStatus(getApplicationContext());
			distanceRemaining = challenge.getRepetitions() - status.distanceMoved;
			timeRemaining = (challenge.getTime()) - (status.timeTaken/1000);
			String distance = Integer.toString(distanceRemaining);
			String time = Long.toString(timeRemaining);
			progress = "Jumps Remaining: " + distance + " Time Remaining: " + time;
			handler.sendEmptyMessage(0);
		}

		// just in case for some reason the above loop exited erroneously
		ChallengeController.stopAllGPSinteraction(getApplicationContext());
		
		if(distanceRemaining<0){
			progress = "Challenge Passed!";
			e.completeChallenge(challenge.getChallengeID(), this);
		} else {
			progress = "Challenge Failed";
		}
		handler.sendEmptyMessage(0);
	}	

	public void countDown(TextView tview){
		try {
			for(int i=10;i>0;i--){
				progress = ((Integer)i).toString();
				handler.sendEmptyMessage(0);
				//tview.setText(n);
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {}

	}
}
