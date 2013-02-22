package uk.ac.cam.groupproj.racethewild;

public class Challenge {

	private final int challengeID; // uniquely identifies this challenge.
	private final int repetitions;
	private final int time; // in seconds.
	private final String text;
	private final int animalID; // of the animal awarded for the task.
	private ChallengeState state;
	private final ChallengeType type;
	
	public int getChallengeID() { return challengeID;} 
	public int getRepetitions() { return repetitions; }
	public int getTime() { return time; }
	public String getText() { return text; }
	public int getAnimalID() { return animalID; }
	public boolean completed() { return state == ChallengeState.complete; }
	public ChallengeState getState() { return state; }
	public ChallengeType getType() { return type; }
	
	public void setState(ChallengeState state) {
		this.state = state;
	}
	
	public Challenge(int challengeID, int repetitions, int time, String text, int animalID, ChallengeType type) {
		this.challengeID = challengeID;
		this.repetitions = repetitions;
		this.time = time;
		this.text = text;
		this.animalID = animalID;
		this.type = type;
	}
	
	
	
}
