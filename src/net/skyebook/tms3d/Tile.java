/**
 * 
 */
package net.skyebook.tms3d;

/**
 * Data structure for a tile
 * @author Skye Book
 *
 */
public class Tile {
	
	private int x=0;
	private int y=0;
	private int zoom=0;

	/**
	 * 
	 */
	public Tile() {
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x){
		if(x<0) throw new IllegalArgumentException("Tile's X value must be greater than 0");
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		if(y<0) throw new IllegalArgumentException("Tile's Y value must be greater than 0");
		this.y = y;
	}

	/**
	 * @return the zoom
	 */
	public int getZoom() {
		return zoom;
	}

	/**
	 * @param zoom the zoom to set
	 */
	public void setZoom(int zoom) {
		if(zoom<0) throw new IllegalArgumentException("Tile's zoom value must be greater than 0");
		else if(zoom>18) throw new IllegalArgumentException("Tile's zoom value must be less than or equal to 18");
		this.zoom = zoom;
	}
	
}
