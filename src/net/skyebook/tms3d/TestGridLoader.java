/**
 * 
 */
package net.skyebook.tms3d;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
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
	
	private GLConverter converter;
	
	private Geometry groundBox;

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
		cam.setFrustumPerspective(45, (float)cam.getWidth()/cam.getHeight(), 1f, 10000);
		
		Logger.getLogger("com.jme").setLevel(Level.OFF);
		
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
		double lat = 40.699667;
		double lon = -74.014229;
		TMSGridTileLoader tms = new TMSGridTileLoader(assetManager, 9, lat, lon);
		tms.setPatchSize(65);
		tms.setQuadSize(257);
		terrain = new TerrainGrid("Grid", 65, 1025, tms);
		
		rootNode.attachChild(terrain);

		TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
		control.setLodCalculator( new DistanceLodCalculator(65, 2.7f));
		terrain.addControl(control);

		this.getCamera().setLocation(new Vector3f(0, 20, 0));
		
		converter = new GLConverter(terrain, tms);

		//this.viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
		
		Box groundBoxMesh = new Box(2, 2, 2);
		groundBox = new Geometry("groundBox", groundBoxMesh);
		Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		material.setColor("Color", ColorRGBA.Red);
		groundBox.setMaterial(material);
		rootNode.attachChild(groundBox);
		

	}


	@Override
	public void simpleUpdate(final float tpf) {
		if(System.currentTimeMillis()-last>1000){
			System.out.println(cam.getLocation());
			last = System.currentTimeMillis();
			
			double[] location = converter.getPosition(cam.getLocation());
			System.out.println("you are at "+location[0]+", "+location[1]);
			
			groundBox.setLocalTranslation(cam.getLocation().x, 0, cam.getLocation().z);
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
