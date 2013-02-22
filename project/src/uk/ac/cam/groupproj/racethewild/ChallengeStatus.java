package uk.ac.cam.groupproj.racethewild;

public class ChallengeStatus {
	public enum Status {
		inProgress, success, failure
	};
	public int distanceMoved;
	public long timeTaken;
	public Status status;
	
	public ChallengeStatus(int statCode, int distance, long time) {
		distanceMoved = distance;
		timeTaken = time;
		switch(statCode) {
			case 0 : status = Status.inProgress;
			case 1 : status = Status.success;
			case 2 : status = Status.failure;
		}
		
	}
}
