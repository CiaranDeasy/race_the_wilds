package uk.ac.cam.groupproj.racethewild;

import java.util.List;

public class Animal implements Comparable<Animal> {
	
	private final int id; // Uniquely identifies the animal.
	private final String graphic; // Filename of the photo image.
	private final String sprite; // Filename of the sprite image for the scroll map.
	private final String name;
	private final String description; // Description shown for black animals.
	private Node[] nodes;
	private Colour colour;
	private final String hint; // Description shown for grey animals.
	private final int distancePerDay;
	private final int hitpoints; // How long the animal must be touched to capture.
	private final int speed; // How fast the animal moves when being captured.
	private boolean challenge; // True if the animal is a challenge reward.
	
	public int getID() { return id; }
	public String getGraphicPath() { return graphic; }
	public String getSpritePath() { return sprite; }
	public String getName() { return name; }
	public String getFacts() { return description; }
	public Node[] getNodes() { return nodes; }
	public Colour getColour() { return colour; }
	public String getHint() { return hint; }
	public int getDistancePerDay() { return distancePerDay; }
	public int getHitpoints() { return hitpoints; }
	public int getSpeed() { return speed; }
	public boolean isChallenge() { return challenge; }
	
	/** Comparison by ID in ascending order. */
	public int compareTo(Animal other) {
		return this.id - other.id;
	}

	public void setColour(Colour colour) { 
		this.colour = colour;
		System.out.println("Colour of " + name + " set to " + colour);
	}
	
	/** Marks the animal as only available through challenges. */
	public void setChallenge() {
		this.challenge = true;
	}
	
	public Animal(int id, String name, String description, String hint, String graphic, 
			String sprite, int distancePerDay, int hitpoints, int speed, List<String> nodeNames) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.hint = hint;
		this.graphic = graphic;
		this.sprite = sprite;
		this.colour = Colour.White; // Default to unfound, and update from save data later.
		this.distancePerDay = distancePerDay;
		this.hitpoints = hitpoints;
		this.speed = speed;
		this.nodes = new Node[nodeNames.size()];
		for (int i = 0; i < nodeNames.size(); i++)
			try {
				nodes[i] = Engine.get().lookupNode(nodeNames.get(i));
			} catch(NodeNotFoundException e) {
				System.err.println("Attempted to add animal " + name + "<#" + id + 
						"> to invalid node \"" + nodeNames.get(i) + "\".");
			}
	}
	
}