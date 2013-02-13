package uk.ac.cam.groupproj.racethewild;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {

	private static final long serialVersionUID = 748191422683935983L;
	private String name; // The name of the node. Uniquely identifies the node.
	private String background; // Filename of the background image displayed on the scroll map.
	private String preview; // Filename of a preview image for display alongside the node map.
	private String sprite; // Filename of a sprite for display on the node map.
	private float relX;   // Fractional x and y for where the node appears on the world map.
	private float relY;
	private List<Animal> animals; // Lists the animals that appear in this node.

	public String getName() {	return name;	}
	public String getBackground() { return background; }
	public String getPreview() { return preview; }
	public String getSprite() { return sprite; }
	public float getRelX() { return relX; }
	public float getRelY() { return relY; }
	public List<Animal> getAnimalList() { return animals; }
	
	/** Returns true if the name of the node is the given name. */
	public boolean hasName(String name) { return this.name.equals(name); }
	
	/** Adds the given animal to the list of animals that appear in this node. */
	public void addAnimal(Animal animal) { animals.add(animal); }
	
	public Node (String name, String background, String preview, String sprite, 
			float RelX, float RelY)
	{
		this.name = name;
		this.background = background;
		this.preview = preview;
		this.sprite = sprite;
		this.animals = new ArrayList<Animal>();
		this.relY = RelY;
		this.relX = RelX;
	}

}