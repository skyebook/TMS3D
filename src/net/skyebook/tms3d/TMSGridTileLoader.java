/**
 * 
 */
package net.skyebook.tms3d;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.skyebook.tms3d.HTTPDownloader.DownloadCompleteCallback;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainGridTileLoader;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;

/**
 * @author Skye Book
 *
 */
public class TMSGridTileLoader implements TerrainGridTileLoader {

	private int startingX;
	private int startingY;

	private AssetManager assetManager;

	private int patchSize = 65;
	private int tileSize = 257;
	private int zoom;

	private File localTileCache;

	int counter = 0;

	/**
	 * 
	 */
	public TMSGridTileLoader(AssetManager assetManager, int zoomLevel, double lat, double lon) {
		this.assetManager = assetManager;
		this.zoom=zoomLevel;

		// convert coordinate to tile
		Tile tile = TileUtils.generateTile(lat, lon, zoom);
		startingX=tile.getX();
		startingY=tile.getY();

		localTileCache = new File("tiles/");
		localTileCache.mkdirs();

		// register the tile server
		assetManager.registerLocator("tiles/", FileLocator.class);
		assetManager.registerLocator("data/", FileLocator.class);
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

		final Tile tile = new Tile();
		tile.setZoom(zoom);
		//tile.setX(startingX+(((int)(location.getX()/tileSize))+location.getX()>0?1:-1));
		//tile.setY(startingY+(((int)(location.getZ()/tileSize))+location.getZ()>0?1:-1));
		tile.setX(startingX+(int)location.getX());
		tile.setY(startingY+(int)location.getZ());

		// create the TerrainQuad
		final TerrainQuad terrainQuad = new TerrainQuad(tile.getZoom()+"/"+tile.getX()+"/"+tile.getZoom(), patchSize, tileSize, null);
		terrainQuad.setLocked(true);

		//System.out.println("terrain quad created");

		// create the Material for it to use
		final Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Texture texture = null;
		//System.out.println("material created");

		// find file
		File tileFile = new File(localTileCache.toString()+"/"+TileUtils.generateCachePath(TileUtils.GOOGLE_KEY, tile));
		if(!tileFile.exists()){
			try {
				// Use a default tile
				//texture = assetManager.loadTexture("blank_tile.png");
				
				
				//System.out.println("downloading tile");
				HTTPDownloader.download(new URL(TileUtils.generateGoogleTileRequest(tile)), tileFile, null, new DownloadCompleteCallback() {

					@Override
					public void downloadComplete(URL originalURL, File fileLocation,
							long timeToDownload) {
						//Texture texture = assetManager.loadTexture(TileUtils.generateCachePath(TileUtils.OSM_KEY, tile));
						//System.out.println("texture loaded");
						//material.setTexture("ColorMap", texture);
						//terrainQuad.setMaterial(material);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			texture = assetManager.loadTexture(TileUtils.generateCachePath(TileUtils.GOOGLE_KEY, tile));
			
		}

		material.setTexture("ColorMap", texture);
		terrainQuad.setMaterial(material);

		// Force the cleansing of unused buffers
		System.gc();

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
	 * @return the size of patches within the underlying TerrainQuads
	 */
	public int getPatchSize() {
		return patchSize;
	}

	/**
	 * @return the size of the underlying TerrainQuads
	 */
	public int getQuadSize() {
		return tileSize;
	}

	/**
	 * Not safe to use this yet.  Keep private until it is ready.
	 * Sets the zoom level for this tile loader
	 * @param zoom
	 */
	private void setZoom(int zoom){
		this.zoom = zoom;
	}

	/**
	 * @return the x index of the starting tile
	 */
	public int getStartingX() {
		return startingX;
	}

	/**
	 * @return the y index of the starting tile
	 */
	public int getStartingY() {
		return startingY;
	}

	/**
	 * @return the zoom
	 */
	public int getZoom() {
		return zoom;
	}

}