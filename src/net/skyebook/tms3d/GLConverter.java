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

		originX = tms.getStartingX();//-terrainCenterX;
		originY = tms.getStartingY();//-terrainCenterY;
		zoom = tms.getZoom();
	}

	public double[] getPosition(Vector3f location){
		int usableQuadSize = tms.getQuadSize()-1;

		// The camera's location needs to be offset by the size of a TerrainQuad within the grid
		Vector3f usableLocation = location.clone();
		usableLocation.x+=usableQuadSize;
		usableLocation.z+=usableQuadSize;

		int cellX = (int)FastMath.floor(usableLocation.x/usableQuadSize);
		int cellY = (int)FastMath.floor(usableLocation.z/usableQuadSize);

		// increment both of the cells
		//cellX+=1;
		//cellY+=1;

		System.out.println("Camera is over Cell:\tx:" + cellX+"\ty:"+cellY);

		float startX = cellX*usableQuadSize;
		float startY = cellY*usableQuadSize;

		float endX = startX + usableQuadSize;
		float endY = startY + usableQuadSize;

		System.out.println("CAMERA AT\t"+usableLocation.z+"\t"+usableLocation.z);

		System.out.println("TILE Extents\t("+startX+","+endX+")\t"+startY+","+endY+")");

		System.out.println("endX: " + endX);

		//System.out.println("GL Stride: "+(endX-startX)+", "+(endY-startY));

		startBox.setLocalTranslation(startX, 0, startY);
		endBox.setLocalTranslation(endX, 0, endY);

		// localize it
		float localX = usableLocation.x - startX;
		float localY = usableLocation.z - startY;

		float percentX = localX/(float)usableQuadSize;
		float percentY = localY/(float)usableQuadSize;

		//percentX*=-1;
		//percentY*=-1;

		System.out.println("Camera is "  + percentX + " of X and " + percentY + " of Y");

		Tile tile = new Tile();
		tile.setX(originX+cellX);
		tile.setY(originY+cellY);
		tile.setZoom(zoom);

		System.out.println("tile is " + tile.getX()+","+tile.getY());

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
