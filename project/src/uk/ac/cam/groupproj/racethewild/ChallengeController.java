package uk.ac.cam.groupproj.racethewild;

import java.util.Random;

import android.content.Context;

public class ChallengeController {                                // mti20 TODO

	boolean challengeInProgress;

	public static enum ChallengeStatus {
		inProgress, success, failure
	}

	/*
	 *  Called to initiate a challenge. Will get initial location.
	 *  Returns true if it's OK to do a challenge and false otherwise.
	 *  Leave a 10s delay after calling this before calling startMovementChallenge()
	 */
	public static boolean requestMovementChallenge(Context context) {
		return true;
	}

	/*
	 *  Call 10s after requestMovementChallenge() to start the challenge
	 */
	public static void startMovementChallenge(Context context) {
		return;
	}

	/*
	 *  Used to poll the status of the challenge
	 */
	public static ChallengeStatus checkMovementChallengeStatus(Context context) {
		Random generator = new Random();

		long startTime = System.currentTimeMillis();
		int waitSeconds = generator.nextInt(10)+7;
		while ((System.currentTimeMillis() - startTime) <  waitSeconds*1000) {

		}

		if (generator.nextInt(2) == 1) {
			return ChallengeStatus.success;
		} else {
			return ChallengeStatus.failure;
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
