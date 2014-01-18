/**
 * 
 */
package ch.thn.gedcom.data;

import ch.thn.gedcom.store.GedcomStoreStructure;

/**
 * @author thomas
 *
 */
public class GedcomTree extends GedcomNode {
	
	private String structureName = null;
	
	
	/**
	 * 
	 * 
	 * @param storeStructure
	 * @param tag
	 */
	public GedcomTree(GedcomStoreStructure storeStructure, String tag) {
		super(storeStructure);
		
		this.structureName = storeStructure.getStructureName();
		
		setInvisibleNode(true);
	}
	

	public String getStructureName() {
		return structureName;
	}
	
	
	@Override
	public String print() {
		return structureName;
	}

}
