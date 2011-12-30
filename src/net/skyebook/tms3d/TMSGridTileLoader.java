/**
 * 
 */
package net.skyebook.tms3d;

import java.io.IOException;

import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.plugins.UrlLocator;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainGridTileLoader;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.grid.FractalTileLoader;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.HillHeightMap;

/**
 * @author Skye Book
 *
 */
public class TMSGridTileLoader implements TerrainGridTileLoader {

	private AssetManager assetManager;
	
	private boolean debugMode = true;

	private int patchSize = 5;
	private int tileSize = 513;
	private FractalTileLoader.FloatBufferHeightMap heightMap;

	private int zoom;

	/**
	 * 
	 */
	public TMSGridTileLoader(AssetManager assetManager) {
		this.assetManager = assetManager;
		
		// register the tile server
		assetManager.registerLocator("http://tile.openstreetmap.org/", UrlLocator.class);
		
		System.out.println("TMSGridTileLoader created");
	}

	/* (non-Javadoc)
	 * @see com.jme3.export.Savable#write(com.jme3.export.JmeExporter)
	 */
	@Override
	public void write(JmeExporter ex) throws IOException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.jme3.export.Savable#read(com.jme3.export.JmeImporter)
	 */
	@Override
	public void read(JmeImporter im) throws IOException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.jme3.terrain.geomipmap.TerrainGridTileLoader#getTerrainQuadAt(com.jme3.math.Vector3f)
	 */
	@Override
	public TerrainQuad getTerrainQuadAt(Vector3f location) {
		System.out.println("Requesting TerrainQuad for " + location.toString());
		
		// convert to real world coordinates
		double lat = 40;
		double lon = -74;

		// convert coordinate to tile
		Tile tile = TileUtils.generateTile(lat, lon, zoom);

		TerrainQuad terrainQuad = null;

		// check for debug mode
		if(debugMode){
			AbstractHeightMap debugHeightMap = null;
			try {
				debugHeightMap = new HillHeightMap(513, 1000, 50, 100, (byte) 10);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			System.out.println("generating");

			debugHeightMap.load();
			
			System.out.println("generated");

			// create the TerrainQuad
			terrainQuad = new TerrainQuad("Quad", patchSize, tileSize, debugHeightMap.getHeightMap());
			
			System.out.println("hello");
		}
		
		System.out.println("about to laod texture");

		// create the Material for it to use
		Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		material.setColor("Color", ColorRGBA.Red);
		//material.setTexture("ColorMap", assetManager.loadTexture(TileUtils.generateTileRequest(tile)));
		terrainQuad.setMaterial(material);
		
		System.out.println("texture loaded");
		
		// return the TerrainQuad
		return terrainQuad;
	}

	/* (non-Javadoc)
	 * @see com.jme3.terrain.geomipmap.TerrainGridTileLoader#setPatchSize(int)
	 */
	@Override
	public void setPatchSize(int patchSize) {
		this.patchSize = patchSize;
	}

	/* (non-Javadoc)
	 * @see com.jme3.terrain.geomipmap.TerrainGridTileLoader#setQuadSize(int)
	 */
	@Override
	public void setQuadSize(int quadSize) {
		this.tileSize = quadSize;
	}

	/**
	 * Sets the zoom level for this tile loader
	 * @param zoom
	 */
	public void setZoom(int zoom){
		this.zoom = zoom;
	}

}
