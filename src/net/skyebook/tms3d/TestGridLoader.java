/**
 * 
 */
package net.skyebook.tms3d;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainGrid;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.texture.Texture;

/**
 * @author Skye Book
 *
 */
public class TestGridLoader extends SimpleApplication {

	private TerrainGrid terrain;

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
		assetManager.registerLocator("data/", FileLocator.class);
		System.out.println("loaded");
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

		flyCam.setMoveSpeed(100f);
		TMSGridTileLoader tms = new TMSGridTileLoader(assetManager);
		tms.setPatchSize(129);
		tms.setQuadSize(513);
		terrain = new TerrainGrid("Grid", 129, 513, tms);

		// create the Material for it to use
		Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

		System.out.println("material created");
		//material.setBoolean("UseMaterialColors", true);
		//material.setColor("Diffuse", ColorRGBA.Red);
		//material.setColor("Ambient", ColorRGBA.Red);
		//material.setTexture("ColorMap", assetManager.loadTexture(TileUtils.generateTileRequest(tile)));
		Texture texture = assetManager.loadTexture("12405.png");
		System.out.println("texture loaded");
		material.setTexture("ColorMap", texture);
		terrain.setMaterial(material);

		rootNode.attachChild(terrain);

		TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
		control.setLodCalculator( new DistanceLodCalculator(65, 2.7f));
		terrain.addControl(control);

		final BulletAppState bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);

		this.getCamera().setLocation(new Vector3f(0, 20, 0));

		//this.viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

	}


	@Override
	public void simpleUpdate(final float tpf) {
		//System.out.println(cam.getLocation());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestGridLoader tgl = new TestGridLoader();
		tgl.start();
	}

}
