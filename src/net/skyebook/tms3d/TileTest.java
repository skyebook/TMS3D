/**
 * 
 */
package net.skyebook.tms3d;

/**
 * @author Skye Book
 *
 */
public class TileTest {

	/**
	 * 
	 */
	public static void main(String[] args) {
		Tile tileEast = TileUtils.generateTile(40.698410245010336, -74.01644217338799, 17);
		Tile tileWest = TileUtils.generateTile(40.698410245010336, -74.11644217338799, 17);
		
		System.out.println("Tile East " + tileEast.getX());
		System.out.println("Tile West " + tileWest.getX());
		
		Tile tileNorth = TileUtils.generateTile(40.798410245010336, -74.01644217338799, 17);
		Tile tileSouth = TileUtils.generateTile(40.698410245010336, -74.01644217338799, 17);
		
		System.out.println("Tile North " + tileNorth.getY());
		System.out.println("Tile South " + tileSouth.getY());
	}

}
