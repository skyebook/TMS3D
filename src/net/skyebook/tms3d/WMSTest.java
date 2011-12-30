/**
 * 
 */
package net.skyebook.tms3d;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wms.request.GetMapRequest;
import org.geotools.data.wms.response.GetMapResponse;
import org.geotools.ows.ServiceException;

/**
 * @author Skye Book
 *
 */
public class WMSTest {
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ServiceException 
	 */
	public static void main(String[] args) throws ServiceException, IOException {
		//String server = "http://code.skyebook.net:8080/geoserver/sf/wms";
		//String server = "http://columbo.nrlssc.navy.mil/ogcwms/servlet/WMSServlet/NGA_CADRG_DATA_UNITED_STATES_AND_TERRITORIES.wms";
		//String server = "http://www2.demis.nl/wms/wms.asp";
		//String server = "http://wms.openstreetmap.de/wms";
		String server = "http://imsortho.cr.usgs.gov:80/wmsconnector/com.esri.wms.Esrimap/USGS_EDC_Ortho_NewYork";
		URL capabilitiesURL = new URL(server+"?VERSION=1.1.0&REQUEST=GetCapabilities");

		WebMapServer wms = new WebMapServer(capabilitiesURL);

		getMapRequests(wms);

	}

	private static void getMapRequests(WebMapServer wms) throws ServiceException, IOException{
		GetMapRequest request = wms.createGetMapRequest();

		request.setFormat("image/png");
		request.setDimensions("583", "420"); //sets the dimensions of the image to be returned from the server
		request.setTransparent(true);
		request.setSRS("EPSG:4326");
		request.setBBox("-74.01,40.7,-73.98,40.8");
		//Note: you can look at the layer metadata to determine a layer's bounding box for a SRS

		for ( Layer layer : WMSUtils.getNamedLayers(wms.getCapabilities()) ) {
			request.addLayer(layer);
		}

		GetMapResponse response = (GetMapResponse) wms.issueRequest(request);
		BufferedImage image = ImageIO.read(response.getInputStream());
		
		JFrame frame = new JFrame("Display image");
		ImagePanel panel = new ImagePanel(image);
		frame.getContentPane().add(panel);
		frame.setSize(500, 500);
		frame.setVisible(true);

	}

	private static void showCapabilities(WebMapServer wms){
		WMSCapabilities capabilities = wms.getCapabilities();

		String serverName = capabilities.getService().getName();
		String serverTitle = capabilities.getService().getTitle();
		System.out.println("Capabilities retrieved from server: " + serverName + " (" + serverTitle + ")");

		if (capabilities.getRequest().getGetFeatureInfo() != null) {
			System.out.println("This server supports GetFeatureInfo requests!");
			// We could make one if we wanted to.
		}

		List<String> formats = wms.getCapabilities().getRequest().getGetMap().getFormats();

		for(String format : formats){
			System.out.println("FORMAT: "+format);
		}


		//gets the top most layer, which will contain all the others
		Layer rootLayer = capabilities.getLayer();

		//gets all the layers in a flat list, in the order they appear in
		//the capabilities document (so the rootLayer is at index 0)
		List layers = capabilities.getLayerList();
	}

	public static class ImagePanel extends JPanel {
		
		private BufferedImage image;
		
		public ImagePanel(BufferedImage image){
			this.image=image;
		}

		public void paint(Graphics g) {
			g.drawImage( image, 0, 0, null);
		}

	}

}
