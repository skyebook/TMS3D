/**
 * 
 */
package net.skyebook.tms3d;

import java.util.logging.Logger;

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
	
	Logger logger = Logger.getLogger(GLConverter.class.getName());

	// The x index of the tile at the OpenGL origin
	private int originX;
	// The x index of the tile at the OpenGL origin
	private int originY;
	// The level of zoom in the TMS grid
	private int zoom;

	private TerrainGrid terrain;
	private TMSGridTileLoader tms;

	private Geometry startBox;
	private Geometry endBox;

	public GLConverter(TerrainGrid terrain, TMSGridTileLoader tileLoader, Geometry startBox, Geometry endBox){
		this.terrain = terrain;
		this.startBox = startBox;
		this.endBox = endBox;

		this.terrain.addListener(new TerrainGridListener() {

			@Override
			public void tileDetached(Vector3f cell, TerrainQuad quad) {}

			@Override
			public void tileAttached(Vector3f cell, TerrainQuad quad) {}

			@Override
			public void gridMoved(Vector3f newCenter) {
				//logger.fine("Grid Moved: " + newCenter);
			}
		});

		this.tms = tileLoader;

		originX = tms.getStartingX();
		originY = tms.getStartingY();
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

		logger.fine("Camera is over Cell:\tx:" + cellX+"\ty:"+cellY);

		float startX = cellX*usableQuadSize;
		float startY = cellY*usableQuadSize;

		float endX = startX + usableQuadSize;
		float endY = startY + usableQuadSize;

		logger.fine("CAMERA AT\t"+usableLocation.x+"\t"+usableLocation.z);

		logger.fine("TILE Extents\t("+startX+","+endX+")\t("+startY+","+endY+")");

		startBox.setLocalTranslation(startX, 0, startY);
		endBox.setLocalTranslation(endX, 0, endY);

		// localize it
		float localX = usableLocation.x - startX;
		float localY = usableLocation.z - startY;

		float percentX = localX/(float)usableQuadSize;
		float percentY = localY/(float)usableQuadSize;
		// flip the polarity of the Y-tile
		percentY = 1f-percentY;

		logger.fine("Camera is "  + percentX + " of X and " + percentY + " of Y");

		Tile tile = new Tile();
		tile.setX(originX+cellX);
		tile.setY(originY+cellY);
		tile.setZoom(zoom);

		logger.fine("tile is " + tile.getX()+","+tile.getY());

		BoundingBox tileBoundingBox = TileUtils.tile2boundingBox(tile);

		logger.fine("Inside of " + tileBoundingBox);

		double latStride = tileBoundingBox.getNorth()-tileBoundingBox.getSouth();
		double lonStride = tileBoundingBox.getEast()-tileBoundingBox.getWest();

		logger.fine("latStride\t"+latStride);
		logger.fine("lonStride\t"+lonStride);

		double lat = tileBoundingBox.getSouth()+(latStride*percentY);
		double lon = tileBoundingBox.getWest()+(lonStride*percentX);

		return new double[]{lat, lon};
	}

}
