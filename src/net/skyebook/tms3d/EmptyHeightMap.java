/**
 * 
 */
package net.skyebook.tms3d;

import com.jme3.terrain.heightmap.AbstractHeightMap;

/**
 * Creates a flat heightmap of a desired size.
 * @author Skye Book
 *
 */
public class EmptyHeightMap extends AbstractHeightMap {
	
	private int size;

	/**
	 * 
	 */
	public EmptyHeightMap(int size) {
		this.size = size;
	}

	/* (non-Javadoc)
	 * @see com.jme3.terrain.heightmap.HeightMap#load()
	 */
	@Override
	public boolean load() {
		heightData = new float[size*size];
		
		for(int i=0; i<(size*size); i++){
			heightData[i]=0f;
		}
		
		return true;
	}

}
