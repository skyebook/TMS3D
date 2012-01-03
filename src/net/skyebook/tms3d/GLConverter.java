/**
 * 
 */
package net.skyebook.tms3d;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
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

	private Geometry startBox;
	private Geometry endBox;

	public GLConverter(TerrainGrid terrain, TMSGridTileLoader tileLoader, Geometry startBox, Geometry endBox){
		this.terrain = terrain;
		this.startBox = startBox;
		this.endBox = endBox;

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

				//originX = tms.getStartingX()-terrainCenterX;
				//originY = tms.getStartingY()-terrainCenterY;
			}
		});

		this.tms = tileLoader;

		originX = tms.getStartingX()-terrainCenterX;
		originY = tms.getStartingY()-terrainCenterY;
		zoom = tms.getZoom();
	}

	private double[] alt(Vector3f location){
		int cellX = (int)FastMath.floor(location.x/tms.getQuadSize());
		int cellY = (int)FastMath.floor(location.z/tms.getQuadSize());

		float startX = cellX*tms.getQuadSize();
		float startY = cellY*tms.getQuadSize();

		System.out.println("CAMERA AT "+location);
		System.out.println("TILE AT "+startX+", "+startY);

		float endX = startX + tms.getQuadSize();
		float endY = startY + tms.getQuadSize();

		startBox.setLocalTranslation(startX, 0, startY);
		endBox.setLocalTranslation(endX, 0, endY);

		// localize it
		float localX = location.x - startX;
		float localY = location.z - startX;

		float percentX = localX/(float)tms.getQuadSize();
		float percentY = localY/(float)tms.getQuadSize();

		System.out.println("Camera is "  + percentX + " of X and " + percentY + " of Y");

		Tile tile = new Tile();
		tile.setX(originX+cellX+1);
		tile.setY(originY+cellY);
		tile.setZoom(zoom);

		BoundingBox tileBoundingBox = TileUtils.tile2boundingBox(tile);
		
		System.out.println("Inside of " + tileBoundingBox);

		double latStride = tileBoundingBox.getNorth()-tileBoundingBox.getSouth();
		double lonStride = tileBoundingBox.getEast()-tileBoundingBox.getWest();
		
		System.out.println("latStride\t"+latStride);
		System.out.println("lonStride\t"+lonStride);

		double lat = tileBoundingBox.getSouth()-(latStride*percentY);
		double lon = tileBoundingBox.getWest()+(lonStride*percentX);

		return new double[]{lat, lon};
	}

	public double[] getPosition(Vector3f location){
		if(true){
			return alt(location);
		}
		
		Vector3f terrainCell = terrain.getCamCell(location);

		System.out.println("Terrain Cell " + terrainCell.toString());

		int cellX = (int)FastMath.floor(location.x/tms.getQuadSize());
		int cellY = (int)FastMath.floor(location.z/tms.getQuadSize());

		System.out.println("I came up with "+cellX+", "+cellY);


		Vector3f worldPositionOfTile = new Vector3f();
		worldPositionOfTile.x=(terrainCell.getX()*tms.getQuadSize());
		worldPositionOfTile.z=(terrainCell.getZ()*tms.getQuadSize());

		float startX = worldPositionOfTile.x;
		float startY = worldPositionOfTile.z;

		System.out.println("CAMERA AT "+location);
		System.out.println("TILE AT "+worldPositionOfTile);

		float endX = startX + tms.getQuadSize();
		float endY = startY + tms.getQuadSize();

		startBox.setLocalTranslation(startX, 0, startY);
		endBox.setLocalTranslation(endX, 0, endY);

		// localize it
		float localX = location.x - startX;
		float localY = location.z - startY;

		float percentX = localX/(float)tms.getQuadSize();
		float percentY = localY/(float)tms.getQuadSize();

		System.out.println("Camera is "  + percentX + " of X and " + percentY + " of Y");

		Tile tile = new Tile();
		tile.setX(originX+(int)terrainCell.x);
		tile.setY(originY+(((int)terrainCell.z)*-1));
		tile.setZoom(zoom);

		BoundingBox tileBoundingBox = TileUtils.tile2boundingBox(tile);

		double latStride = tileBoundingBox.getNorth()-tileBoundingBox.getSouth();
		double lonStride = tileBoundingBox.getEast()-tileBoundingBox.getWest();

		double lat = tileBoundingBox.getSouth()+(latStride*percentY);
		double lon = tileBoundingBox.getWest()+(lonStride*percentX);

		return new double[]{lat, lon};
	}

}
