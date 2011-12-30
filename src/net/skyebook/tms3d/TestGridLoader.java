/**
 * 
 */
package net.skyebook.tms3d;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainGrid;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;

/**
 * @author Skye Book
 *
 */
public class TestGridLoader extends SimpleApplication {
	
	private TerrainGrid terrainGrid;

	/**
	 * 
	 */
	public TestGridLoader() {
	}

	/* (non-Javadoc)
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	@Override
	public void simpleInitApp() {
		// create lights
		ColorRGBA diffuseLightColor = new ColorRGBA(1f, 1f, 1f, 1f);
		ColorRGBA diffuseLightColor2 = new ColorRGBA(.3f,.4f,.45f,.3f);

		DirectionalLight directionalLight = new DirectionalLight();
		directionalLight.setDirection(new Vector3f(.25f, -.85f, .75f));
		directionalLight.setColor(diffuseLightColor);

		DirectionalLight directionalLight2 = new DirectionalLight();
		directionalLight2.setDirection(new Vector3f(-.25f,.85f,-.75f));
		directionalLight2.setColor(diffuseLightColor2);

		rootNode.addLight(directionalLight);
		rootNode.addLight(directionalLight2);
		
		flyCam.setMoveSpeed(100);
		getCamera().setLocation(new Vector3f(0, 200, 0));
		
		terrainGrid = new TerrainGrid("Grid", 65, 257, new TMSGridTileLoader(assetManager));
		
		rootNode.attachChild(terrainGrid);
		
		TerrainLodControl control = new TerrainLodControl(terrainGrid, getCamera());
		control.setLodCalculator( new DistanceLodCalculator(65, 2.7f));
		//terrainGrid.addControl(control);
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestGridLoader tgl = new TestGridLoader();
		tgl.start();
	}

}
