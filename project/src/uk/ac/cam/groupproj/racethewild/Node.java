package uk.ac.cam.groupproj.racethewild;

import java.io.Serializable;
import java.util.List;

public class Node implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 748191422683935983L;
	List<Animal> grey; // Lists the grey animals associated with this zone.
	List<Animal> black; // Lists the black animals associated with this zone.
	String graphic; // Contains the filepath to a sprite for display on the node map
	public String scrollMapGraphic;
	
	public Node (String filename)
	{
		scrollMapGraphic = filename;
	}
	
	// Co-ordinates on the node map?

}