package uk.ac.cam.groupproj.racethewild;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable{

	private static final long serialVersionUID = 748191422683935983L;
	private String name; // The name of the node.
	private String sprite; // Contains the filepath to a sprite for display on the node map
	private String background; // Filepath to the background image 
	private float relX;   // fractional x and y for where it goes on the picture.
	private float relY;
	private List<Animal> animals; // Lists the animals that appear in this node.

	public float getRelX(){ return relX;}
	public float getRelY(){ return relY;}
	public String getSprite() { return sprite; }
	public String getBackground() { return background; }
	public String getName() {	return name;	}
	
	/** Returns true if the name of the node is the given name. */
	public boolean hasName(String name) { return this.name.equals(name); }
	
	public List<Animal> getAnimalList(){
		return animals;
	}
	
	/** Adds the given animal to the list of animals that appear in this node. */
	public void addAnimal(Animal animal) { animals.add(animal); }
	
	public Node (String name, String background, String sprite, float RelX, float RelY)
	{
		this.name = name;
		this.background = background;
		this.sprite = sprite;
		this.animals = new ArrayList<Animal>();
		this.relY = RelY;
		this.relX = RelX;
	}
	
	// Co-ordinates on the node map?

}