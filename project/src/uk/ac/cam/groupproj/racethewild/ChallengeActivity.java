package uk.ac.cam.groupproj.racethewild;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ChallengeActivity extends Activity {

	Challenge challenge = null;
	TextView progressView;
	String progress;
	Engine e;
	boolean counting = false; // Indicates that the 10-second countdown is in progress.
	Thread thread;
	boolean buttonEnabled = true;
	boolean threadAllowedToLive = true; // Unset to instruct the thread to terminate.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Preamble.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_challenge);
		Intent intent = getIntent();
		String message = intent.getStringExtra(ChallengeList.CHALLENGE_NAME);
		e = Engine.get();

		// Lookup the challenge.
		int messageInt = Integer.parseInt(message);
		try {
			challenge = e.getChallenge(messageInt);
		} catch(ChallengeNotFoundException e) {
			// Shouldn't ever happen!!!
			System.err.println("ChallengeActivity got an invalid ID!");
			System.exit(-1);
		}

		// Display the challenge text.
		TextView challengeTitleTextView = (TextView) findViewById(R.id.challengeTitle);
		challengeTitleTextView.setTextSize(20);
		challengeTitleTextView.setText(challenge.getText());
		
		// Display the progress text.
		progressView = (TextView) findViewById(R.id.challengeProgress);
		progressView.setTextSize(25);
		progressView.setText("Loading challenge.");

		// Display the animal's image, or challenge complete image.
		try {
			ImageView animalImage = (ImageView) findViewById(R.id.challengeImageView);
			InputStream stream = null;
			if (challenge.isComplete()) stream = getAssets().open("ChallengeComplete.png");
			else stream = getAssets().open(e.getAnimal(challenge.getAnimalID()).getGraphicPath());
			Bitmap image = BitmapFactory.decodeStream(stream);
			stream.close();
			animalImage.setImageBitmap(image);
		} catch(IOException e) {
			System.err.println("IOException when displaying animal image in challenge.");
		} catch(AnimalNotFoundException e) {
			System.err.println("Challenge #" + challenge.getChallengeID() + ", animal not found!");
		}
		
		// Fork depending on challenge status.
		// If no challenge running, then start this one.
		if(ChallengeController.currentChallenge == null) {
			thread = new Thread() {
				public void run() {
					if(challenge.getType() == ChallengeType.distanceTime)
						startNewChallenge();
					else if(challenge.getType() == ChallengeType.accelerometer)
						; // Accelerometer challenges omitted from final release.
				}
			};
			thread.start();
		}
		// Else if this challenge is running, then just resume tracking.
		else if (ChallengeController.currentChallenge == challenge) {
			thread = new Thread() {
				public void run() {
					distanceChallengeStatus();
				}
			};
			thread.start();
		}
		// Else different challenge running, offer to cancel it.
		else {
			setProgressText("Another challenge is running. If you'd like to cancel and start " + 
					"this challenge instead, press below.");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_challenge, menu);
		return true;
	}

	/** The main loop, which queries challenge status every second and updates the display */
	private void distanceChallengeStatus() {
		ChallengeStatus status = ChallengeController.checkMovementChallengeStatus(this);
		while (!status.isFinished() && status.getTimeLeft() > 0) {
			setProgressText("Distance Remaining: " + status.getDistanceLeft() + "m\n Time Remaining: " +
					formatTime(status.getTimeLeft()));
			status = ChallengeController.checkMovementChallengeStatus(this);
			if(!threadAllowedToLive) return; // Allows us to end the thread.
		}
		
		// Check success.
		if(status.success()) {
			setProgressText("Challenge Passed!");
			this.completeChallenge();
		} else {
			setProgressText("Challenge Failed.");
			ChallengeController.stopChallenge(this);
			buttonEnabled = false;
		}
		
		// Clean up.
		ChallengeController.stopChallenge(this);
	}
	
	/*public void jumpChallengeStatus(Challenge challenge){
		// TODO: REPAIR
		ChallengeStatus status = ChallengeController.checkJumpChallengeStatus(this);
		while (status.isFinished()){
			status = ChallengeController.checkJumpChallengeStatus(this);
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
	}	*/

	/** Gives a ten-second countdown. */
	private void countDown() {
		this.counting = true;
		try {
			for(int i=10;i>0;i--){
				setProgressText(((Integer)i).toString());
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {}
		this.counting = false;
	}
	
	/** Requests a challenge, gives a countdown, and starts the tracking. */
	private void startNewChallenge() {
		// Disable the button during the countdown.
		buttonEnabled = false;
		
		// Request the challenge.
		ChallengeController.requestMovementChallenge(this, challenge);
		
		// Do the countdown.
		countDown();
		
		// Start the challenge.
		ChallengeController.startMovementChallenge(this);
		buttonEnabled = true;
		
		// Regularly query challenge progress.
		this.distanceChallengeStatus();
	}
	
	/** The challenge button will cancel the current challenge. If the challenge being cancelled is
	 *  not the challenge associated with this activity, then that challenge is started. */
	public void onButtonPress(View view) {
		if(!buttonEnabled) return;
		// Record whether to start a new challenge after cancelling.
		boolean startChallenge = false;
		if (this.challenge != ChallengeController.currentChallenge) {
			startChallenge = true;
		}
		// Kill the thread.
		if (thread != null) threadAllowedToLive = false;
		// Stop the challenge.
		ChallengeController.stopChallenge(this);
		// Start new challenge, if appropriate.
		if (startChallenge) {
			thread = new Thread() {
				public void run() {
					startNewChallenge();
				}
			};
			thread.start();
		}
		else {
			setProgressText("Challenge cancelled");
			buttonEnabled = false;
		}
	}
	
	/** Returns to the challenge list when back button is pressed, unless countdown is in progress.
	 *  */
	@Override
	public void onBackPressed() {
		if (counting) return; // Disable back button during countdown.
		else super.onBackPressed();
	}
	
	/** Tells the thread to kill itself when ending the activity. */
	@Override
	public void onDestroy() {
		if(thread != null) threadAllowedToLive = false;
		super.onDestroy();
	}
	
	/** Update the display and the back-end data with challenge completion. */
	private void completeChallenge() {
		// Disable the cancel challenge button.
		buttonEnabled = false;
		if(challenge.isComplete()) 
			setProgressText("You completed the challenge again! You're so ace!");
		// Release the animal and mark the challenge as completed.
		e.completeChallenge(challenge.getChallengeID(), this);
		// Inform the player.
		try {
			setProgressText("Challenge completed! A " + 
					e.getAnimal(challenge.getAnimalID()).getName() + 
					" has been released into the wild!");
		} catch(AnimalNotFoundException e) {
			System.err.println("Completed challenge, but animal was not found!");
		}
		// End the challenge.
		ChallengeController.stopChallenge(this);
	}
	
	/** Returns a minutes-seconds string representation of an integer number of seconds. */
	private String formatTime(int seconds) {
		if (seconds < 60) return seconds + " seconds";
		else return seconds/60 + "min, " + seconds%60 + "sec";
	}
	
	/** Updates the on-screen text in a thread-safe manner. */
	private void setProgressText(String text) {
		progress = text;
		handler.sendEmptyMessage(0);
	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message){
			progressView.setText(progress);
		}
	};
}
