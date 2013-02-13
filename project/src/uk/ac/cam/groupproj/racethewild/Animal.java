package uk.ac.cam.groupproj.racethewild;

import java.util.List;

public class Animal implements Comparable<Animal> {
	
	private int id; // Uniquely identifies the animal.
	private String graphic; // Filename of the photo image.
	private String sprite; // Filename of the sprite image for the scroll map.
	private String name;
	private String description; // Description shown for black animals.
	private Node[] nodes;
	private Colour colour;
	private String hint; // Description shown for grey animals.
	private int distancePerDay;
	
	public int getID() { return id; }
	public String getGraphicPath() { return graphic; }
	public String getSpritePath() { return sprite; }
	public String getName() { return name; }
	public String getFacts() { return description; }
	public Node[] getNodes() { return nodes; }
	public Colour getColour() { return colour; }
	public String getHint() { return hint; }
	public int getDistancePerDay() { return distancePerDay; }
	
	/** Comparison by ID in ascending order. */
	public int compareTo(Animal other) {
		return this.id - other.id;
	}

	public void setColour(Colour colour) { 
		this.colour = colour;
		System.out.println("Colour of " + name + " set to " + colour);
	}
	
	public Animal(int id, String name, String description, String hint, String graphic, 
			String sprite, int distancePerDay, List<String> nodeNames) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.hint = hint;
		this.graphic = graphic;
		this.sprite = sprite;
		this.colour = Colour.White; // Default to unfound, and update from save data later.
		this.distancePerDay = distancePerDay;
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