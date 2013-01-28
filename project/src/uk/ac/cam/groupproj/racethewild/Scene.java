package uk.ac.cam.groupproj.racethewild;

public interface Scene {

	public void render(); // Draw the scene to the screen.
	public void update(); // Updates the scene on the screen.
	public void close(); // Tell the scene to cleanup.

}