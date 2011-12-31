/**
 * 
 */
package net.skyebook.tms3d;

import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainGrid;
import com.jme3.terrain.geomipmap.TerrainGridListener;
import com.jme3.terrain.geomipmap.TerrainQuad;

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
		terrain.addListener(new TerrainGridListener() {
			
			@Override
			public void tileDetached(Vector3f cell, TerrainQuad quad) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void tileAttached(Vector3f cell, TerrainQuad quad) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void gridMoved(Vector3f newCenter) {
				// TODO Auto-generated method stub
				
			}
		});
		
		this.tms = tileLoader;
		
		originX = tms.getStartingX();
		originY = tms.getStartingY();
		zoom = tms.getZoom();
	}
	
	public double[] getPosition(Vector3f location){
		Vector3f terrainCell = terrain.getCamCell(location);
		Tile tile = new Tile();
		tile.setX(originX+(int)terrainCell.x);
		tile.setY(originY+(int)terrainCell.y);
		tile.setZoom(zoom);
		
		Vector3f worldPositionOfTile = terrain.getWorldTranslation().clone();
		
		worldPositionOfTile.x+=(terrainCell.getX()*tms.getQuadSize());
		worldPositionOfTile.z+=(terrainCell.getY()*tms.getQuadSize());
		
		System.out.println("BLOCK AT " + worldPositionOfTile.x+", "+worldPositionOfTile.z);
		
		float startX = worldPositionOfTile.x;
		float startY = worldPositionOfTile.z;
		
		float endX = startX + tms.getQuadSize();
		float endY = startY + tms.getQuadSize();
		
		// localize it
		float localX = location.x - startX;
		float localY = location.z - startY;
		
		float percentX = localX/(float)tms.getQuadSize();
		float percentY = localY/(float)tms.getQuadSize();
		
		BoundingBox tileBoundingBox = TileUtils.tile2boundingBox(tile);
		
		double latStride = tileBoundingBox.getNorth()-tileBoundingBox.getSouth();
		double lonStride = tileBoundingBox.getEast()-tileBoundingBox.getWest();
		
		double lat = tileBoundingBox.getSouth()+(latStride*percentY);
		double lon = tileBoundingBox.getWest()+(lonStride*percentX);
		
		
		return new double[]{lat, lon};
	}
	
}
