/**
 * 
 */
package net.skyebook.tms3d;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
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

	private TerrainGrid terrain;
	private long last = -1;

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
		cam.setFrustumPerspective(45, cam.getWidth()/cam.getHeight(), 1f, 1000f);
		
		//assetManager.registerLocator("data/", FileLocator.class);
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

		flyCam.setMoveSpeed(1000f);
		TMSGridTileLoader tms = new TMSGridTileLoader(assetManager, 12);
		tms.setPatchSize(129);
		tms.setQuadSize(513);
		terrain = new TerrainGrid("Grid", 129, 1025, tms);

		
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
		if(System.currentTimeMillis()-last>1000){
			System.out.println(cam.getLocation());
			last = System.currentTimeMillis();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestGridLoader tgl = new TestGridLoader();
		tgl.start();
	}

}
