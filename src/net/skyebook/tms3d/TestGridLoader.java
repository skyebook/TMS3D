/**
 * 
 */
package net.skyebook.tms3d;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainGrid;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
  
/**
 * @author Skye Book
 *
 */
public class TestGridLoader extends SimpleApplication {
	
	private static final Logger logger = Logger.getLogger(TestGridLoader.class.getName());

	private TerrainGrid terrain;
	private long last = -1;
	
	private GLConverter converter;
	
	private Geometry groundBox;
	
	private BitmapText latLonDisplay;

	/**
	 * 
	 */
	public TestGridLoader() {}
	
	private void setupLatLonDisplay(){
		latLonDisplay = new BitmapText(guiFont, false);
		latLonDisplay.setLocalTranslation(0, fpsText.getLineHeight()*2, 0);
		latLonDisplay.setText("Location Display NOT_STARTED");
        guiNode.attachChild(latLonDisplay);
	}

	/* (non-Javadoc)
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	@Override
	public void simpleInitApp() {
		setupLatLonDisplay();
		setDisplayStatView(false);
		
		cam.setFrustumPerspective(45, (float)cam.getWidth()/cam.getHeight(), 1f, 10000);
		
		Logger.getLogger("com.jme3").setLevel(Level.SEVERE);
		
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
		TMSGridTileLoader tms = new TMSGridTileLoader(assetManager, 17, lat, lon);
		tms.setPatchSize(65);
		tms.setQuadSize(257);
		terrain = new TerrainGrid("Grid", 65, 1025, tms);
		
		rootNode.attachChild(terrain);

		TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
		control.setLodCalculator( new DistanceLodCalculator(65, 2.7f));
		terrain.addControl(control);

		this.getCamera().setLocation(new Vector3f(0, 20, 0));
		
		//this.viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
		
		Box boxMesh = new Box(2, 2, 2);
		groundBox = new Geometry("groundBox", boxMesh);
		Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		material.setColor("Color", ColorRGBA.Red);
		groundBox.setMaterial(material);
		rootNode.attachChild(groundBox);
		
		Geometry startBox = new Geometry("startBox", boxMesh);
		Material startMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		startMat.setColor("Color", ColorRGBA.Green);
		startBox.setMaterial(startMat);
		rootNode.attachChild(startBox);
		
		Geometry endBox = new Geometry("endBox", boxMesh);
		Material endMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		endMat.setColor("Color", ColorRGBA.Blue);
		endBox.setMaterial(endMat);
		rootNode.attachChild(endBox);
		
		converter = new GLConverter(terrain, tms, startBox, endBox);

	}


	@Override
	public void simpleUpdate(final float tpf) {
		if(System.currentTimeMillis()-last>1000){
			logger.fine("--\tSTART CONVERSION\t--");
			double[] location = converter.getPosition(cam.getLocation());
			logger.info("you are at "+location[0]+", "+location[1]);
			
			latLonDisplay.setText(location[0]+"\t"+location[1]);
			
			groundBox.setLocalTranslation(cam.getLocation().x, 0, cam.getLocation().z);
			
			last = System.currentTimeMillis();
			logger.fine("--\tEND CONVERSION\t--");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AppSettings settings = new AppSettings(true);
		settings.setFrameRate(30);
		TestGridLoader tgl = new TestGridLoader();
		tgl.setSettings(settings);
		tgl.start();
	}

}
