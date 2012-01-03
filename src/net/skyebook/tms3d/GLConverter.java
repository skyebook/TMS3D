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

	public double[] getPosition(Vector3f location){
		int usableQuadSize = tms.getQuadSize()-1;

		int cellX = (int)FastMath.floor(location.x/usableQuadSize);
		int cellY = (int)FastMath.floor(location.z/usableQuadSize);

		// increment both of the cells
		//cellX+=1;
		//cellY+=1;

		System.out.println("Looks like you're in: " + cellX+", "+cellY);

		float startX = cellX*usableQuadSize;
		float startY = cellY*usableQuadSize;

		System.out.println("CAMERA AT "+location);
		System.out.println("TILE AT "+startX+", "+startY);

		float endX = startX + usableQuadSize;
		float endY = startY + usableQuadSize;

		System.out.println("endX: " + endX);

		//System.out.println("GL Stride: "+(endX-startX)+", "+(endY-startY));

		startBox.setLocalTranslation(startX, 0, startY);
		endBox.setLocalTranslation(endX, 0, endY);

		// localize it
		float localX = location.x - startX;
		float localY = location.z - startY;

		float percentX = localX/(float)usableQuadSize;
		float percentY = localY/(float)usableQuadSize;

		//percentX*=-1;
		//percentY*=-1;

		System.out.println("Camera is "  + percentX + " of X and " + percentY + " of Y");

		Tile tile = new Tile();
		tile.setX(originX+cellX);
		tile.setY(originY+cellY);
		tile.setZoom(zoom);

		BoundingBox tileBoundingBox = TileUtils.tile2boundingBox(tile);

		System.out.println("Inside of " + tileBoundingBox);

		double latStride = tileBoundingBox.getNorth()-tileBoundingBox.getSouth();
		double lonStride = tileBoundingBox.getEast()-tileBoundingBox.getWest();

		System.out.println("latStride\t"+latStride);
		System.out.println("lonStride\t"+lonStride);

		double lat = tileBoundingBox.getSouth()+(latStride*percentY);
		double lon = tileBoundingBox.getWest()+(lonStride*percentX);

		return new double[]{lat, lon};
	}

}
