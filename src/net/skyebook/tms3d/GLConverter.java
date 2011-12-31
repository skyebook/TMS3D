/**
 * 
 */
package net.skyebook.tms3d;

import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainGrid;

/**
 * @author Skye Book
 *
 */
public class GLConverter {
	
	// The x index of the tile at the OpenGL origin
	private int originX;
	// The x index of the tile at the OpenGL origin
	private int originY;
	private int zoom;
	
	private TerrainGrid terrain;
	private TMSGridTileLoader tms;
	
	public GLConverter(TerrainGrid terrain, TMSGridTileLoader tileLoader){
		this.terrain = terrain;
		this.tms = tileLoader;
		
		originX = tms.getStartingX();
		originY = tms.getStartingY();
		zoom = tms.getZoom();
	}
	
	public BoundingBox getPosition(Vector3f location){
		Vector3f terrainCell = terrain.getCamCell(location);
		Tile tile = new Tile();
		tile.setX(originX+(int)terrainCell.x);
		tile.setY(originY+(int)terrainCell.y);
		tile.setZoom(zoom);
		
		Vector3f worldPositionOfTile = terrain.getWorldTranslation().clone();
		worldPositionOfTile.x+=(tile.getX()*tms.getQuadSize());
		worldPositionOfTile.z+=(tile.getY()*tms.getQuadSize());
		
		BoundingBox tileBoundingBox = TileUtils.tile2boundingBox(tile);
		return tileBoundingBox;
	}
	
}
