/*
 *  TODO:
 *    - get to work with other GPS
 */

package uk.ac.cam.groupproj.racethewild;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ChallengeController {                                // mti20 TODO

	static Challenge currentChallenge;
	static long totalTime;   // Times are in milliseconds. 
	static long startTime;
	static int totalDistance;         
	static int totalJumps;
	private static ChallengeJump jumpChal;
	static Intent gpsIntent;           
	static boolean wasTrackingMovement;



	/*
	 *  Called to initiate a challenge. Will get initial location.
	 *  Returns true if it's OK to do a challenge and false otherwise.
	 *  Leave a 10s delay after calling this before calling startMovementChallenge()
	 */
	public static boolean requestMovementChallenge(Context context, Challenge challenge) {
		System.out.println("Movement challenge requested.");
		
		/** Stop other GPS process for duration of challenge **/
		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(
				R.string.gps_main_file_key), Context.MODE_PRIVATE);
		boolean currentStatus = sharedPref.getBoolean("gps_wanted", false);
		Editor editor = sharedPref.edit();
		if (currentStatus) {
			editor.putBoolean("gps_wanted", false);
		}
		
		/** Set up the variables. */
		SharedPreferences challengeSharedPref = context.getSharedPreferences(context.getString(
				R.string.gps_challenge_file_key), Context.MODE_PRIVATE);
		Editor challengeEditor = challengeSharedPref.edit();
		currentChallenge = challenge;
		totalTime = challenge.getTime();
		totalDistance = challenge.getRepetitions();
		challengeEditor.putInt("distance_left", challenge.getRepetitions());
		challengeEditor.putBoolean("challenge_over", false);
		challengeEditor.putBoolean("challenge_passed", false);
		challengeEditor.commit();
		
		/** Start the challenge GPS service **/
		totalTime = challenge.getTime()*1000;
		ChallengeController.totalDistance = challenge.getRepetitions();
		gpsIntent = new Intent(context,ChallengeGPS.class);
		context.startService(gpsIntent);
		currentChallenge = challenge;
		System.out.println("Finished processing request for challenge.");
		
		return true;
	}

	/*
	 *  Call 10s after requestMovementChallenge() to start the challenge
	 */
	public static void startMovementChallenge(Context context) {
		System.out.println("Movement challenge started.");
		startTime = System.currentTimeMillis();
	}
	
	/*
	 *  If movement tracking was previously enabled and was disabled by the process of 
	 *  undertaking a challenge, this method turns it back on
	 */
	private static void reenableMovementTracking(Context context) {
		if (wasTrackingMovement) {
			SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.gps_main_file_key), Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean("gps_wanted", true);
			editor.commit();
		}
	}
	
	/*
	 *  Used to poll the status of the challenge
	 */
	public static ChallengeStatus checkMovementChallengeStatus(Context context) {

		// prevent service from becoming starved of CPU time through eager polling
		try{
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// do nothing
		}

		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(
				R.string.gps_challenge_file_key), Context.MODE_PRIVATE); 
		int distanceLeft = sharedPref.getInt("distance_left", -1);
		boolean challengeOver = sharedPref.getBoolean("challenge_over", false);
		boolean passed = sharedPref.getBoolean("challenge_passed", false);
		int timeLeft = 0;
		if (currentChallenge != null) // In case the challenge is cancelled, but the thread hasn't ended yet.
		timeLeft = currentChallenge.getTime() - 
				(int) ((System.currentTimeMillis() - startTime)/1000);
		
		return new ChallengeStatus(distanceLeft, timeLeft, challengeOver, passed);
	}
	
	/*
	 *  Can be called if challenge is erroneously exited to kill
	 *  all interaction with the GPS
	 */
	public static void stopAllGPSinteraction(Context context) {
		context.stopService(gpsIntent);
	}

	public static boolean requestJumpChallenge(Context context,long totalTime, int totalJumps) {
		try {
			jumpChal = new ChallengeJump(context);
			ChallengeController.totalTime = totalTime*1000;
			ChallengeController.totalJumps = totalJumps;
			return true;
		} catch (Exception e) {
			System.err.println("Can not initialize jump Challenge");
			return false;
		}
	}

	public static void startJumpChallenge(Context context) {
		try {
			startTime = System.currentTimeMillis();
			jumpChal.startChallengeJump();
		} catch (Exception e) {
			System.err.println("Can not start jump Challenge");
		}
	}
	
	public static ChallengeStatus checkJumpChallengeStatus(Context context) {
		int jumps;
		long time;
		
		time = System.currentTimeMillis() - startTime;
		jumps = jumpChal.getJumps();
		
		if ((time <= totalTime) && (jumps <totalJumps)){
			return new ChallengeStatus(jumps, (int)(time/1000), false, false);
		} else if ((time <= totalTime) && (jumps >=totalJumps)) {
			jumpChal.haltChallengeJump();
			System.out.println("Challenge complete.");
			return new ChallengeStatus(jumps, (int)(time/1000), true, true);
		} else {
			jumpChal.haltChallengeJump();
			System.out.println("Challenge failed due to time out.");
			return new ChallengeStatus(jumps,(int)(time/1000), true, false);
		}
	}
	
	/** Ends the challenge and resets the challenge controller, ready for a new challenge. */
	public static boolean stopChallenge(Context context) {
		// Return the GPS to normal.
		context.stopService(gpsIntent);
		reenableMovementTracking(context);

		SharedPreferences sharedPref2 = context.getSharedPreferences(context.getString(R.string.gps_challenge_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor2 = sharedPref2.edit();
		boolean challengePassed = sharedPref2.getBoolean("challenge_passed", false);
		
		// Reset challenge variables.
		totalTime = 0;
		startTime = 0;
		totalDistance = 0;
		totalJumps = 0;
		currentChallenge = null;
		editor2.putInt("distance_left", 0);
		editor2.putBoolean("challenge_over", false);
		editor2.putBoolean("challenge_passed", false);
		editor2.commit();
		
		return challengePassed;
	}

}
