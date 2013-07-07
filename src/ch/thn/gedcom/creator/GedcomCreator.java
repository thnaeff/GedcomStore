/**
 * 
 */
package ch.thn.gedcom.creator;

import java.util.LinkedHashMap;

import ch.thn.gedcom.store.GedcomBlock;
import ch.thn.gedcom.store.GedcomObject;
import ch.thn.gedcom.store.GedcomPrinter;
import ch.thn.gedcom.store.GedcomStore;
import ch.thn.gedcom.store.GedcomTagLine;

/**
 * @author thomas
 *
 */
public class GedcomCreator {
	
	/** &lt;ID, {@link GedcomBlock}&gt;*/
	private LinkedHashMap<Object, GedcomBlock> structures = null;
	
//	private HashMap<Object, HashMap<String, GedcomTagLine>> cache = null;
	
	private GedcomStore store = null;
	
	private String structureName = null;
	
	private boolean showDebugOutput = false;
//	private boolean useCaching = false;
	
	
	public GedcomCreator(GedcomStore store, String structureName, String... initialStructureIds) {
		this.store = store;
		this.structureName = structureName;
//		this.useCaching = useCaching;
		
		structures = new LinkedHashMap<Object, GedcomBlock>();
		
//		if (useCaching) {
//			cache = new HashMap<Object, HashMap<String,GedcomTagLine>>();
//		}
		
		if (initialStructureIds != null) {
			for (int i = 0; i < initialStructureIds.length; i++) {
				addNewStructure(initialStructureIds[i]);
			}
		}
		
	}
	
	public GedcomCreator(GedcomStore store, String structureName) {
		this(store, structureName, new String[0]);
	}
	
	
	
	public boolean addNewStructure(Object id) {
		
		GedcomBlock newStructure = store.getGedcomBlock(structureName, GedcomBlock.COPY_MODE_MANDATORY);
		
		if (newStructure == null) {
			if (showDebugOutput) {
				System.out.println("Failed to add structure " + structureName + " with ID " + id);
			}
			return false;
		}
		
		structures.put(id, newStructure);
		
		if (showDebugOutput) {
			System.out.println("New structure " + structureName + " number " + structures.size() + " with ID " + id + " added");
		}
		
		return true;
	}
	
	public boolean removeStructure(Object id) {
		if (!structures.containsKey(id)) {
			return false;
		}
		
		structures.remove(id);
		return true;
	}
	
	public GedcomTagLine getLine(Object id, String... pathToLine) {
//		String pathString = null;
//		
//		if (useCaching) {
//			//Some idea for caching... It seems like there is not much benefit though...
//			pathString = Arrays.toString(pathToLine);
//			
//			if (cache.containsKey(id) && cache.get(id).containsKey(pathString)) {
//				return cache.get(id).get(pathString);
//			}
//		}
		
		GedcomObject o = structures.get(id).followPath(pathToLine);
		
//		if (useCaching) {
//			if (!cache.containsKey(id)) {
//				cache.put(id, new HashMap<String, GedcomTagLine>());
//			}
//			cache.get(id).put(pathString, o.getTagLine());
//		}
		
		return o.getTagLine();
		
	}
	
	public GedcomBlock getBlock(Object id, String... pathToLine) {
		return getLine(id, pathToLine).getBlock();
	}
	
	
	public boolean hasStructure(Object structureId) {
		return structures.containsKey(structureId);
	}
	
	public StringBuffer getStructure(boolean printEmptyLines) {
		StringBuffer sb = new StringBuffer();
		
		for (GedcomBlock block : structures.values()) {
			sb.append(GedcomPrinter.preparePrint(block, 0, true, printEmptyLines));
		}
		
		return sb;
	}
	
	public int getNumberOfStructures() {
		return structureName.length();
	}
	

}
