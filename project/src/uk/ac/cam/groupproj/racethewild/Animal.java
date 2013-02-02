package uk.ac.cam.groupproj.racethewild;

import java.util.List;

public class Animal implements Comparable<Animal> {
	
	private int id;
	private String graphic; // Contains the filepath of the photo image.
	private String sprite; // The sprite shown on the scrolly map.
	private String name;
	private String description;
	private List<Node> nodes;
	private Colour colour;
	
	public int getID() { return id; }
	public String getGraphicPath() { return graphic; }
	public String getSpritePath() { return sprite; }
	public String getName() { return name; }
	public String getFacts() { return description; }
	public List<Node> getNodes() { return nodes; }
	public Colour getColour() { return colour; }
	
	public int compareTo(Animal other) {
		return this.id - other.id;
	}
	
	// Temporary, incomplete constructor.
	public Animal(int id, String name, Colour colour) {
		this.id = id;
		this.graphic = "";
		this.sprite = "";
		this.name = name;
		this.description = "Placeholder";
		this.nodes = null;
		this.colour = colour;
	}
	
}