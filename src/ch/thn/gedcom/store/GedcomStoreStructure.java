/**
 * 
 */
package ch.thn.gedcom.store;

import java.util.LinkedList;

/**
 * @author thomas
 *
 */
public class GedcomStoreStructure {
	
	private String structureName = null;
	
	private GedcomStore store = null;
	
	private GedcomStoreBlock storeBlock = null;
	
	
	/**
	 * 
	 */
	public GedcomStoreStructure(GedcomStore store, String structureName) {
		this.store = store;
		this.structureName = structureName;
		
		
	}
	
	
	protected boolean parse(LinkedList<String> block, String structureName) {
		this.structureName = structureName;

		storeBlock = new GedcomStoreBlock(this);
		storeBlock.parse(block);
				
		return true;
	}
	
	
	protected GedcomStoreBlock getStoreBlock() {
		return storeBlock;
	}
	
	
	protected GedcomStore getStore() {
		return store;
	}
	
	
	
	
	
	public String getStructureName() {
		return structureName;
	}
	
	public boolean hasVariations() {
		return (store.getVariations(structureName).size() > 1);
		
	}
	
	
	
	@Override
	public String toString() {
		return GedcomPrinter.preparePrint(storeBlock, 1, false).toString();
	}

}
