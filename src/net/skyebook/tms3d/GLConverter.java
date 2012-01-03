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
	
	// the terrains current grid center
	private int terrainCenterX;
	private int terrainCenterY;
	
	// The x index of the tile at the OpenGL origin
	private int originX;
	// The x index of the tile at the OpenGL origin
	private int originY;
	private int zoom;
	
	private TerrainGrid terrain;
	private TMSGridTileLoader tms;
	
	public GLConverter(TerrainGrid terrain, TMSGridTileLoader tileLoader){
		this.terrain = terrain;
		
		Vector3f currentGrid = terrain.getCurrentCell();
		terrainCenterX = (int)currentGrid.x;
		terrainCenterY = (int)currentGrid.z;
		
		this.terrain.addListener(new TerrainGridListener() {
			
			@Override
			public void tileDetached(Vector3f cell, TerrainQuad quad) {}
			
			@Override
			public void tileAttached(Vector3f cell, TerrainQuad quad) {}
			
			@Override
			public void gridMoved(Vector3f newCenter) {
				System.out.println("Grid Moved: " + newCenter);
				terrainCenterX = (int)newCenter.x;
				terrainCenterY = (int)newCenter.z;
				
				originX = tms.getStartingX()+terrainCenterX;
				originY = tms.getStartingY()+terrainCenterY;
			}
		});
		
		this.tms = tileLoader;
		
		originX = tms.getStartingX()+terrainCenterX;
		originY = tms.getStartingY()+terrainCenterY;
		zoom = tms.getZoom();
	}
	
	public double[] getPosition(Vector3f location){
		System.out.println("-start-");
		Vector3f terrainCell = terrain.getCamCell(location);
		Tile tile = new Tile();
		tile.setX(originX+(int)terrainCell.x);
		tile.setY(originY+(int)terrainCell.y);
		tile.setZoom(zoom);
		
		//Vector3f worldPositionOfTile = terrain.getWorldTranslation().clone();
		Vector3f worldPositionOfTile = new Vector3f();
		
		System.out.println("TILE AT "+worldPositionOfTile);
		System.out.println("CAMERA AT "+location);
		
		
		worldPositionOfTile.x+=(terrainCell.getX()*tms.getQuadSize());
		worldPositionOfTile.z+=(terrainCell.getY()*tms.getQuadSize());
		
		//System.out.println("BLOCK AT " + worldPositionOfTile.x+", "+worldPositionOfTile.z);
		
		float startX = worldPositionOfTile.x;
		float startY = worldPositionOfTile.z;
		
		System.out.println("TILE AT "+worldPositionOfTile);
		
		float endX = startX + tms.getQuadSize();
		float endY = startY + tms.getQuadSize();
		
		// localize it
		float localX = location.x - startX;
		float localY = location.z - startY;
		
		float percentX = localX/(float)tms.getQuadSize();
		float percentY = localY/(float)tms.getQuadSize();
		
		System.out.println("Camera is "  + percentX + "% of X and " + percentY + "% of Y");
		
		BoundingBox tileBoundingBox = TileUtils.tile2boundingBox(tile);
		
		double latStride = tileBoundingBox.getNorth()-tileBoundingBox.getSouth();
		double lonStride = tileBoundingBox.getEast()-tileBoundingBox.getWest();
		
		double lat = tileBoundingBox.getSouth()+(latStride*percentY);
		double lon = tileBoundingBox.getWest()+(lonStride*percentX);
		
		System.out.println("-end-");
		return new double[]{lat, lon};
	}
	
}
