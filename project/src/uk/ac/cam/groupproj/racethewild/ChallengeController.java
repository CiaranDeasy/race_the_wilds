/*
 *  TODO:
 *    - get to work with other GPS
 */

package uk.ac.cam.groupproj.racethewild;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;

public class ChallengeController {                                // mti20 TODO

	static boolean challengeInProgress;
	static long totalTime;             static long currentTime;
	static long startTime;
	static int totalDistance;          static long currentDistance;
	static int totalJumps;
	private static ChallengeJump jumpChal;
	static Intent gpsIntent;           static boolean wasTrackingMovement;



	/*
	 *  Called to initiate a challenge. Will get initial location.
	 *  Returns true if it's OK to do a challenge and false otherwise.
	 *  Leave a 10s delay after calling this before calling startMovementChallenge()
	 */
	public static boolean requestMovementChallenge(Context context, long time, int totalDistance) {
		System.out.println("Movement challenge requested.");
		
		/** Reset distance moved **/
		SharedPreferences sharedPref2 = context.getSharedPreferences(context.getString(R.string.gps_challenge_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor2 = sharedPref2.edit();
		editor2.putInt("distance_moved", 0);
		editor2.commit();
		
		/** Stop other GPS process for duration of challenge **/
		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.gps_main_file_key), Context.MODE_PRIVATE);
		boolean currentStatus = sharedPref.getBoolean("gps_wanted", false);
		if (currentStatus) {
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean("gps_wanted", false);
			editor.commit();
		}
		
		/** Start the GPS service **/
		totalTime = time*1000;
		ChallengeController.totalDistance = totalDistance;
		gpsIntent = new Intent(context,ChallengeGPS.class);
		context.startService(gpsIntent);

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
		int distance;      long time;

		// prevent service from becoming starved of CPU time through eager polling
		try{
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// do nothing
		}

		time = System.currentTimeMillis() - startTime;
		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.gps_challenge_file_key), Context.MODE_PRIVATE); 
		distance = sharedPref.getInt("distance_moved",0);
		
		if (time < totalTime) {
			return new ChallengeStatus(0,distance,time);
		} else if ((time > totalTime) & (distance < totalDistance)) {
			context.stopService(gpsIntent);
			reenableMovementTracking(context);
			return new ChallengeStatus(1,distance,time);
		} else {
			context.stopService(gpsIntent);
			reenableMovementTracking(context);
			return new ChallengeStatus(2,distance,time);
		}
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
			return new ChallengeStatus(0,jumps,time);
		} else if ((time <= totalTime) && (jumps >=totalJumps)) {
			jumpChal.haltChallengeJump();
			System.out.println("Challenge complete.");
			return new ChallengeStatus(1,jumps,time);
		} else {
			jumpChal.haltChallengeJump();
			System.out.println("Challenge failed due to time out.");
			return new ChallengeStatus(2,jumps,time);
		}
				
		
	}

//	public static ChallengeStatus checkMovementChallengeStatus(Context context) {
//		checkMovementChallengeStatus(context);
//	}

}
