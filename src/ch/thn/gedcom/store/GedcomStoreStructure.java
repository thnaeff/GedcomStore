/**
 * 
 */
package ch.thn.gedcom.store;

import java.util.LinkedList;

import ch.thn.gedcom.printer.GedcomStorePrinter;


/**
 * A store structure has a structure name (like FAMILY_EVENT_STRUCTURE), a link 
 * to the store where it has been parsed and a {@link GedcomStoreBlock} which 
 * contains all the first level lines of the structure.<br>
 * <br>
 * Hierarchy:
 * <pre>
 * |---GedcomStoreStructure----------------------------|
 * |                                                   |
 * | A structure contains one block                    |
 * |                                                   |
 * | |---GedcomStoreBlock---------------------------|  |
 * | |                                              |  |
 * | |  A block contains multiple lines             |  |
 * | |                                              |  |
 * | |  |---GedcomStoreLine---------------------|   |  |
 * | |  |                                       |   |  |
 * | |  | A line may contain one sub block      |   |  |
 * | |  |                                       |   |  |
 * | |  | |---GedcomStoreBlock---------------|  |   |  |
 * | |  | | If a line has sub-lines (on a    |  |   |  |
 * | |  | | higher level), those lines are   |  |   |  |
 * | |  | | wrapped in a block.              |  |   |  |
 * | |  | |----------------------------------|  |   |  |
 * | |  |---------------------------------------|   |  |
 * | |                                              |  |
 * | |  |---GedcomStoreLine---------------------|   |  |
 * | |  | ...                                   |   |  |
 * | |  |---------------------------------------|   |  |
 * | |                                              |  |
 * | |----------------------------------------------|  |
 * |---------------------------------------------------|
 * </pre>
 * 
 * The class {@link GedcomObject} has additional information about how 
 * the parsed lineage-linked grammar is split up in lines and blocks
 * 
 * 
 * @author thomas
 *
 */
public class GedcomStoreStructure {
	
	/** The name of this structure, like FAMILY_EVENT_STRUCTURE etc. **/
	private String structureName = null;
	
	/** A link to the gedcom store in which this structure is stored. **/
	private GedcomStore store = null;
	
	/** The starting block in the structure **/
	private GedcomStoreBlock storeBlock = null;
	
	
	/**
	 * Creates a new gedcom structure with the given name and with a link to 
	 * the store.
	 * 
	 * @param store
	 * @param structureName
	 */
	public GedcomStoreStructure(GedcomStore store, String structureName) {
		this.store = store;
		this.structureName = structureName;
	}
	
	/**
	 * Creates the starting store block in this store structure and parses the 
	 * given block.
	 * 
	 * @param block
	 * @return
	 * @throws GedcomParseException
	 */
	protected boolean parse(LinkedList<String> block) throws GedcomParseException {
		//A new store block without a parent line
		storeBlock = new GedcomStoreBlock(this, null);
		return storeBlock.parse(block);		
	}
	
	/**
	 * Returns the starting store block of this structure
	 * 
	 * @return
	 */
	public GedcomStoreBlock getStoreBlock() {
		return storeBlock;
	}
	
	/**
	 * Returns the {@link GedcomStore} in which this structure is in.
	 * 
	 * @return
	 */
	public GedcomStore getStore() {
		return store;
	}
	
	
	/**
	 * Returns the name of this structure, like FAMILY_EVENT_STRUCTURE
	 * 
	 * @return
	 */
	public String getStructureName() {
		return structureName;
	}
	
	/**
	 * Returns <code>true</code> if this structure has multiple variations 
	 * (the FAMILY_EVENT_STRUCTURE for example has the variations 
	 * [ANUL|CENS|DIV|DIVF], [ENGA|MARB|MARC], [MARR] etc.)
	 * 
	 * @return
	 */
	public boolean hasVariations() {
		return (store.getVariations(structureName).size() > 1);
		
	}
	
	
	
	@Override
	public String toString() {
		return GedcomStorePrinter.preparePrint(storeBlock, 1, false).toString();
	}

}
