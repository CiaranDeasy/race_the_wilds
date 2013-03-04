package uk.ac.cam.groupproj.racethewild;

public class Challenge {

	private final int challengeID; // uniquely identifies this challenge.
	private final int repetitions;
	private final int time; // in seconds.
	private final String text;
	private final int animalID; // of the animal awarded for the task.
	private boolean complete;
	private final ChallengeType type;
	
	public int getChallengeID() { return challengeID;} 
	public int getRepetitions() { return repetitions; }
	public int getTime() { return time; }
	public String getText() { return text; }
	public int getAnimalID() { return animalID; }
	public boolean isComplete() { return complete; }
	public ChallengeType getType() { return type; }
	
	public void setComplete() {
		this.complete = true;
	}
	
	public Challenge(int challengeID, int repetitions, int time, String text, int animalID, ChallengeType type) {
		this.challengeID = challengeID;
		this.repetitions = repetitions;
		this.time = time;
		this.text = text;
		this.animalID = animalID;
		this.type = type;
		this.complete = false;
	}
	
	
	
}
