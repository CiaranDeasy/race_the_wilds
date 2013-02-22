/*
 *  TODO:
 *    - get to work with other GPS
 */

package uk.ac.cam.groupproj.racethewild;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ChallengeController {                                // mti20 TODO

	static boolean challengeInProgress;
	static long totalTime;             static long currentTime;
	static long startTime;
	static int totalDistance;          static long currentDistance;


	/*
	 *  Called to initiate a challenge. Will get initial location.
	 *  Returns true if it's OK to do a challenge and false otherwise.
	 *  Leave a 10s delay after calling this before calling startMovementChallenge()
	 */
	public static boolean requestMovementChallenge(Context context, long totalTime, int totalDistance) {
		ChallengeController.totalTime = totalTime*1000;
		ChallengeController.totalDistance = totalDistance;
		Intent intent = new Intent(context,ChallengeGPS.class);
		context.startService(intent);
		return true;
	}

	/*
	 *  Call 10s after requestMovementChallenge() to start the challenge
	 */
	public static void startMovementChallenge(Context context) {
		startTime = System.currentTimeMillis();
	}

	/*
	 *  Used to poll the status of the challenge
	 */
	public static ChallengeStatus checkMovementChallengeStatus(Context context) {
		int distance;      long time;

		time = System.currentTimeMillis() - startTime;
		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.gps_challenge_file_key), Context.MODE_PRIVATE); 
		distance = sharedPref.getInt("distance_moved",0);; 
		
		if (totalTime < time) {
			return new ChallengeStatus(0,distance,time);
		} else if ((time > totalTime) & (distance < totalDistance)) {
			context.stopService(new Intent(context, ChallengeGPS.class));
			return new ChallengeStatus(1,distance,time);
		} else {
			context.stopService(new Intent(context, ChallengeGPS.class));
			return new ChallengeStatus(2,distance,time);
		}
	}

	public static boolean requestAccelChallenge(Context context) {
		return true;
	}

	public static void startAccelChallenge(Context context) {
		return;
	}

//	public static ChallengeStatus checkMovementChallengeStatus(Context context) {
//		checkMovementChallengeStatus(context);
//	}

}
