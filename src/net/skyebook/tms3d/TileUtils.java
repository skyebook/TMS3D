/**
 * 
 */
package net.skyebook.tms3d;

/**
 * Utility methods for working with tiles.  Derived from the
 * OpenStreetMap wiki: http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Java
 * @author OpenStreetMap contributors
 *
 */
public class TileUtils {

	public static BoundingBox tile2boundingBox(Tile tile) {
		BoundingBox bb = new BoundingBox();
		bb.setNorth(tile2lat(tile.getY(), tile.getZoom()));
		bb.setSouth(tile2lat(tile.getY() + 1, tile.getZoom()));
		bb.setWest(tile2lon(tile.getX(), tile.getZoom()));
		bb.setEast(tile2lon(tile.getX() + 1, tile.getZoom()));
		return bb;
	}

	public static double tile2lon(int x, int z) {
		return x / Math.pow(2.0, z) * 360.0 - 180;
	}

	public static double tile2lat(int y, int z) {
		double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
		return Math.toDegrees(Math.atan(Math.sinh(n)));
	}
	
	public static String generateTileRequest(String baseServer, Tile tile) {
		if(!baseServer.endsWith("/")) baseServer = baseServer+"/";
		
		return baseServer + generateTileRequest(tile);
	}

	public static String generateTileRequest(Tile tile) {
		
		return tile.getZoom() + "/" + tile.getX() + "/" + tile.getY() + ".png";
	}

	public static Tile generateTile(double lat, double lon, int zoom) {
		Tile tile = new Tile();
		tile.setX((int)Math.floor( (lon + 180) / 360 * (1<<zoom)));
		tile.setY((int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom)));
		tile.setZoom(zoom);
		return tile;
	}
	
	public static void main(String[] args){
		// test
		String baseServer = "http://tile.openstreetmap.org/";
		Tile tile = generateTile(40.68, -73.7, 15);
		System.out.println(generateTileRequest(baseServer, tile));
	}
}
