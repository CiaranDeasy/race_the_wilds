package uk.ac.cam.groupproj.racethewild;

public class ChallengeStatus {

	private final int distanceLeft;
	private final int timeLeft;
	private final boolean finished;
	private final boolean passed;

	public int getDistanceLeft() { return distanceLeft; }
	public int getTimeLeft() { return timeLeft; }
	public boolean isFinished() { return finished; }
	public boolean success() { return passed; }
	
	public ChallengeStatus(int distance, int time, boolean isFinished, boolean passed) {
		this.distanceLeft = distance;
		this.timeLeft = time;
		this.finished = isFinished;
		this.passed = passed;
	}
}