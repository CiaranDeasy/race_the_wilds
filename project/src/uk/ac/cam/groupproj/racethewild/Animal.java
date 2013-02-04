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
	private String hint;
	
	public int getID() { return id; }
	public String getGraphicPath() { return graphic; }
	public String getSpritePath() { return sprite; }
	public String getName() { return name; }
	public String getFacts() { return description; }
	public List<Node> getNodes() { return nodes; }
	public Colour getColour() { return colour; }
	public String getHint() { return hint; }
	
	public int compareTo(Animal other) {
		return this.id - other.id;
	}
	
	public void setGrey() { colour = Colour.Grey; }
	public void setBlack() { colour = Colour.Black; }
	
	public Animal(int id, String name, String description, String hint, String graphic, 
			String sprite) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.hint = hint;
		this.graphic = graphic;
		this.sprite = sprite;
		this.colour = Colour.White; // Default to unfound, and update from save data later.
	}
	
}