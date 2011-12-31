/**
 * 
 */
package net.skyebook.tms3d;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainGridTileLoader;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.grid.FractalTileLoader;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.HillHeightMap;
import com.jme3.texture.Texture;

/**
 * @author Skye Book
 *
 */
public class TMSGridTileLoader implements TerrainGridTileLoader {

	private int startingX;
	private int startingY;

	private AssetManager assetManager;

	private boolean debugMode = true;

	private int patchSize = 65;
	private int tileSize = 129;
	private FractalTileLoader.FloatBufferHeightMap heightMap;

	private int zoom = 15;

	private File localTileCache;

	int counter = 0;

	/**
	 * 
	 */
	public TMSGridTileLoader(AssetManager assetManager, int zoomLevel) {
		this.assetManager = assetManager;
		this.zoom=zoomLevel;

		// convert to real world coordinates
		double lat = 40.699667;
		double lon = -74.014229;

		// convert coordinate to tile
		Tile tile = TileUtils.generateTile(lat, lon, zoom);
		startingX=tile.getX();
		startingY=tile.getY();

		localTileCache = new File("tiles/");

		// register the tile server
		//assetManager.registerLocator("http://tile.openstreetmap.org/", UrlLocator.class);
		assetManager.registerLocator("tiles/", FileLocator.class);

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

		Tile tile = new Tile();
		tile.setZoom(zoom);
		//tile.setX(startingX+(((int)(location.getX()/tileSize))+location.getX()>0?1:-1));
		//tile.setY(startingY+(((int)(location.getZ()/tileSize))+location.getZ()>0?1:-1));
		tile.setX(startingX+(int)location.getX());
		tile.setY(startingY+(int)location.getZ());


		TerrainQuad terrainQuad = null;

		// check for debug mode
		if(debugMode){
			AbstractHeightMap debugHeightMap = null;
			try {
				debugHeightMap = new HillHeightMap(tileSize, 1, 1, 50, (byte) 50);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			debugHeightMap.load();
			// create the TerrainQuad
			terrainQuad = new TerrainQuad(tile.getZoom()+"/"+tile.getX()+"/"+tile.getZoom(), patchSize, tileSize, debugHeightMap.getHeightMap());

		}

		// create the Material for it to use
		Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		System.out.println("material created");

		// find file
		File tileFile = new File(localTileCache.toString()+"/"+TileUtils.generateTileRequest(tile));
		if(!tileFile.exists()){
			try {
				HTTPDownloader.download(new URL("http://tile.openstreetmap.org/"+TileUtils.generateTileRequest(tile)), tileFile, null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			System.out.println("TILE ALREADY EXISTS");
		}



		Texture texture = assetManager.loadTexture(TileUtils.generateTileRequest(tile));
		System.out.println("texture loaded");
		material.setTexture("ColorMap", texture);
		terrainQuad.setMaterial(material);

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
	private void setZoom(int zoom){
		this.zoom = zoom;
	}

}
