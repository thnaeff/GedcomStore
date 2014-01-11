/**
 * 
 */
package ch.thn.gedcom.store;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.GedcomHelper;
import ch.thn.gedcom.printer.GedcomStorePrinter;
import ch.thn.util.StringUtil;

/**
 * A store line contains all the parsed information of one single line. A line 
 * could be a tag line or a structure line. A tag line has a tag, a tag ID (in 
 * brackets < and >) and min/max values, whereas a structure line has a structure 
 * ID (in double brackets << and >>) and min/max values. A tag line can also 
 * have a value (enclosed in < and >) or xref (enclosed in @< and >@) field and 
 * it can have multiple tag possibilities (enclosed in [ and ] and separated by |).<br>
 * <br>
 * The class {@link GedcomStoreStructure} has more information about the hierarchy 
 * of structures, blocks and lines.
 * 
 * 
 * @author thomas
 *
 */
public class GedcomStoreLine {
	
	private int min = 0;
	private int max = 0;
	
	private LinkedHashSet<String> xrefNames = null;
	private LinkedHashSet<String> valueNames = null;
	private LinkedHashSet<String> tagNames1 = null;
	private LinkedHashSet<String> tagNames2 = null;
	private LinkedHashSet<String> valuePossibilities = null;
	
	private String structureName = null;
	private String originalGedcomDefinitionLine = null;
	
	
	private GedcomStoreBlock parentBlock = null;
	private GedcomStoreBlock childBlock = null;
	
	
	/**
	 * Creates a new gedcom store line in the block given as parentBlock
	 * 
	 * @param parentBlock
	 */
	public GedcomStoreLine(GedcomStoreBlock parentBlock) {
		this.parentBlock = parentBlock;
		
		
		xrefNames = new LinkedHashSet<String>();
		valueNames = new LinkedHashSet<String>();
		tagNames1 = new LinkedHashSet<String>();
		tagNames2 = new LinkedHashSet<String>();
		valuePossibilities = new LinkedHashSet<String>();
	}
	
	
	/**
	 * Sets a child block for this gedcom store line
	 * 
	 * @param childBlock
	 */
	protected void setChildBlock(GedcomStoreBlock childBlock) {
		this.childBlock = childBlock;
	}
	
	/**
	 * Parses the given lineage linked grammar line
	 * 
	 * @param gedcomDefinitionLine
	 * @return
	 */
	protected boolean parse(String gedcomDefinitionLine) {
		originalGedcomDefinitionLine = gedcomDefinitionLine;
		
		//Clean the line from all unnecessary stuff
		gedcomDefinitionLine = StringUtil.removeAll(GedcomHelper.commentPattern, gedcomDefinitionLine);
		gedcomDefinitionLine = StringUtil.replaceAll(GedcomHelper.levelPattern, gedcomDefinitionLine, "");
		gedcomDefinitionLine = StringUtil.replaceAll(GedcomHelper.spacesPattern, gedcomDefinitionLine, " ");
		gedcomDefinitionLine = gedcomDefinitionLine.trim();
		
		//Split for each space
		String[] splitLine = gedcomDefinitionLine.split(" ");
		
		//Check if line could be valid
		if (splitLine.length < 1 || splitLine.length > 4) {
			System.out.println("[ERROR] Failed to parse line '" + gedcomDefinitionLine + "'. Number of items not in the range.");
			return false;
		}
		
		int tagIndex = -1;
		
		for (int i = 0; i < splitLine.length; i++) {
			
			if (StringUtil.matches(GedcomHelper.xrefPattern, splitLine[i])) {
				//@<XREF:TAG>
				xrefNames.add(StringUtil.removeAll(GedcomHelper.xrefTagReplace, splitLine[i]));
				
			} else if (splitLine[i].contains("@") && StringUtil.matches(GedcomHelper.multipleXRefs, splitLine[i])) {
				//Multiple XREF ([@<XREF>@|@<XREF>@|<NULL>...])
				//At least one @ has to be present
				
				String[] values = StringUtil.replaceAll(GedcomHelper.multipleXRefsReplace, splitLine[i], "").split("\\|");
				
				for (int j = 0; j < values.length; j++) {
					xrefNames.add(values[j]);
				}
				
			} else if (StringUtil.matches(GedcomHelper.minMaxPattern, splitLine[i])) {
				//{MIN:MAX}
				//{MIN:MAX*}
				//{MIN:MAX}*
				String[] minmax = StringUtil.removeAll(GedcomHelper.minMaxReplace, splitLine[i]).split(":");
				min = Integer.parseInt(minmax[0]);
				
				if (!minmax[1].equals("M")) {
					max = Integer.parseInt(minmax[1]);
				}
				
			} else if (StringUtil.matches(GedcomHelper.structurePattern, splitLine[i])) {
				//<<STRUCTURE>>
				structureName = StringUtil.removeAll(GedcomHelper.structureReplace, splitLine[i]);
				
			} else if (StringUtil.matches(GedcomHelper.valuePattern, splitLine[i])) {
				//<VALUE>
				valueNames.add(StringUtil.removeAll(GedcomHelper.valueReplace, splitLine[i]));
				
			} else if (StringUtil.matches(GedcomHelper.multipleValues, splitLine[i])) {
				//Multiple VALUE ([<ABC>|<DEF>|<GHI>...])
				
				String[] values = StringUtil.replaceAll(GedcomHelper.multipleValuesReplace, splitLine[i], "").split("\\|");
				
				for (int j = 0; j < values.length; j++) {
					valueNames.add(values[j]);
				}
				
			}  else if (StringUtil.matches(GedcomHelper.tagPattern, splitLine[i])) {
				//TAG
				if (xrefNames.size() == 0) {
					//Tag before xref
					tagNames1.add(splitLine[i]);
				} else {
					//Tag after xref
					tagNames2.add(splitLine[1]);
				}
				
				tagIndex = i;
			} else if (StringUtil.contains(GedcomHelper.multipleTags, splitLine[i])) {
				//Multiple TAG ([ABC|DEF|GHI...])

				String[] tags = StringUtil.replaceAll(GedcomHelper.multipleTagsReplace, splitLine[i], "").split("\\|");
				
				for (int j = 0; j < tags.length; j++) {
					if (xrefNames.size() == 0) {
						//Tag before xref
						tagNames1.add(tags[j]);
					} else {
						//Tag after xref
						tagNames2.add(tags[j]);
					}
				}
			
				tagIndex = i;
			} else if (tagIndex != -1 && i == tagIndex + 1 
					&& StringUtil.contains(GedcomHelper.multipleValuePossibilities, splitLine[i])) {
				//Value possibilities. They can only appear right after the tag
				
				String[] possibilities = StringUtil.replaceAll(GedcomHelper.multipleValuesReplace, splitLine[i], "").split("\\|");
				
				for (int j = 0; j < possibilities.length; j++) {
					if (possibilities[j].toUpperCase().equals("NULL")) {
						this.valuePossibilities.add(null);
					} else {
						this.valuePossibilities.add(possibilities[j]);
					}
				}
			} else {
				System.out.println("[INFO] Did not process " + splitLine[i] + " in " + getId() + " under " + getParentBlock().getStoreStructure().getStructureName());
			}
			
		}
		
		if (parentBlock.getStoreStructure().getStore().showParsingOutput()) {
			System.out.println("  parsed: " + GedcomStorePrinter.preparePrint(this));
		}
		
		return true;
		
	}
	
	/**
	 * Returns the position of this store line in the block
	 * 
	 * @return
	 */
	public int getPos() {
		return parentBlock.getStoreLines().indexOf(this);
	}
	
	/**
	 * Returns the child block of this store line
	 * 
	 * @return
	 */
	public GedcomStoreBlock getChildBlock() {
		return childBlock;
	}
	
	/**
	 * Returns the parent block of this store line
	 * 
	 * @return
	 */
	public GedcomStoreBlock getParentBlock() {
		return parentBlock;
	}
	
	/**
	 * Returns the store structure if there is one. <code>NULL</code> is returned 
	 * if there is no store structure or if multiple variations are available.
	 * 
	 * @return
	 */
	public GedcomStoreStructure getStoreStructure() {
		if (structureName == null) {
			return null;
		}
		
		LinkedList<GedcomStoreStructure> storeStructures = parentBlock.getStoreStructure().getStore().getVariations(structureName);
		
		if (storeStructures == null || storeStructures.size() > 1 || storeStructures.size() == 0) {
			//No variations or multiple variations available
			return null;
		}
		
		//Only one variation available
		return storeStructures.get(0);
	}
	
	protected String getOriginalGedcomDefinitionLine() {
		return originalGedcomDefinitionLine;
	}
	
	
	
	
//	/**
//	 * Creates a new instance of a {@link GedcomLine}<br>
//	 * The returned line can be a {@link GedcomStructureLine} if a structure name 
//	 * is set, or a {@link GedcomTagLine} if no structure name is set and the 
//	 * given tag is valid.<br>
//	 * <br>
//	 * This method can be used if only one tag name for this line exists. If there 
//	 * are multiple tag names, null is returned.
//	 * 
//	 * @param parentLine The line which should be the parent of the returned 
//	 * line
//	 * @return
//	 */
//	public GedcomLine getLineInstance(GedcomLine parentLine, int copyMode) {
//		
//		if (structureName != null) {
//			return new GedcomStructureLine(this, parentLine, copyMode);
//		} else {
//			LinkedHashSet<String> tagNames = getTagNames();
//			
//			if (tagNames.size() != 1) {
//				return null;
//			}
//			
//			return new GedcomTagLine(this, parentLine, 
//					tagNames.toArray(new String[tagNames.size()])[0], copyMode);
//		}
//	}
//	
//	/**
//	 * Creates a new instance of a {@link GedcomLine}<br>
//	 * The returned line can be a {@link GedcomStructureLine} if a structure name 
//	 * is set, or a {@link GedcomTagLine} if no structure name is set and the 
//	 * given tag is valid.<br>
//	 * <br>
//	 * This method has to be used if multiple variations for this line exists.
//	 * 
//	 * @param parentLine The line which should be the parent of the returned 
//	 * line
//	 * @param tag
//	 * @return
//	 */
//	public GedcomLine getLineInstance(GedcomLine parentLine, String tag, int copyMode) {
//		
//		if (structureName != null) {
//			return new GedcomStructureLine(this, parentLine, tag, copyMode);
//		} else {
//			if (!hasTag(tag)) {
//				return null;
//			}
//			
//			return new GedcomTagLine(this, parentLine, tag, copyMode);
//		}
//	}
//	
//	/**
//	 * Creates a new instance of a {@link GedcomLine}<br>
//	 * The returned line can be a {@link GedcomStructureLine} if a structure name 
//	 * is set, or a {@link GedcomTagLine} if no structure name is set and the 
//	 * given tag is valid.<br>
//	 * <br>
//	 * This method has to be used if multiple variations for this line exists.
//	 * 
//	 * @param parentLine The line which should be the parent of the returned 
//	 * line
//	 * @param tag
//	 * @param withXRef
//	 * @param withValue
//	 * @return
//	 */
//	public GedcomLine getLineInstance(GedcomLine parentLine, String tag, 
//			boolean withXRef, boolean withValue, int copyMode) {
//		
//		if (structureName != null) {
//			return new GedcomStructureLine(this, parentLine, tag, withXRef, withValue, copyMode);
//		} else {
//			if (!hasTag(tag)) {
//				return null;
//			}
//			
//			return new GedcomTagLine(this, parentLine, tag, copyMode);
//		}
//	}
	
	/**
	 * Returns the minimum number of lines which are required in one block
	 * 
	 * @return
	 */
	public int getMin() {
		return min;
	}
	
	/**
	 * Returns the maximum number of lines which are allowed in one block
	 * 
	 * @return
	 */
	public int getMax() {
		return max;
	}
	
	/**
	 * Returns <code>true</code> if this is a mandatory line (with a minimum 
	 * number of lines >= 1).
	 * 
	 * @return
	 */
	public boolean isMandatory() {
		return (min >= 1);
	}
	
	/**
	 * Returns the structure name if there is one. If there is a structure name 
	 * (the return value is != <code>NULL</code>), it means that this line is 
	 * a structure line.
	 * 
	 * @return
	 */
	public String getStructureName() {
		return structureName;
	}
	
	/**
	 * Returns the ID of this line. The ID is either the structure name (if 
	 * this is a structure line) or a list of the possible tag names (if it 
	 * is a tag line). This ID can be used to identify the store line.
	 * 
	 * @return
	 */
	public String getId() {
		if (structureName != null) {
			return structureName;
		} else {
			return GedcomFormatter.makeOrList(getTagNames(), "", "").toString();
		}
	}
	
	/**
	 * Returns all the possible tag names
	 * 
	 * @return
	 */
	public LinkedHashSet<String> getTagNames() {
		if (tagNames1.size() > 0) {
			return tagNames1;
		}
		
		return tagNames2;
	}
	
	/**
	 * Returns a list of all the xref names on this line
	 * 
	 * @return
	 */
	public LinkedHashSet<String> getXRefNames() {
		return xrefNames;
	}
	
	/**
	 * Returns a list of all the value names in this line
	 * 
	 * @return
	 */
	public LinkedHashSet<String> getValueNames() {
		return valueNames;
	}
	
	/**
	 * Returns all values which are possible for this line
	 * 
	 * @return
	 */
	public LinkedHashSet<String> getValuePossibilities() {
		return valuePossibilities;
	}
	
	/**
	 * Returns <code>true</code> if this line has at least one tag name
	 * 
	 * @return
	 */
	public boolean hasTags() {
		return (getTagNames().size() > 0);
	}
	
	/**
	 * Returns <code>true</code> if the tag appears before the xref value on this line
	 * 
	 * @return
	 */
	public boolean hasTagBeforeXRef() {
		return (tagNames1.size() > 0);
	}
	
	/**
	 * Returns <code>true</code> if the tag appears after the xref value on this line
	 * 
	 * @return
	 */
	public boolean hasTagAfterXRef() {
		return (tagNames2.size() > 0);
	}
	
	/**
	 * Returns <code>true</code> if this line has any xref names
	 * 
	 * @return
	 */
	public boolean hasXRefNames() {
		return (xrefNames.size() > 0);
	}
	
	/**
	 * Returns <code>true</code> if this line has any value names
	 * 
	 * @return
	 */
	public boolean hasValueNames() {
		return (valueNames.size() > 0 || valuePossibilities.size() > 0);
	}
	
	/**
	 * Returns <code>true</code> if this line has more than one tag name possibilities
	 * 
	 * @return
	 */
	public boolean hasMultipleTagNames() {
		return (getTagNames().size() > 1);
	}
	
	/**
	 * Returns <code>true</code> if this line has a structure name instead of 
	 * tag names. If this line has a structure name, it is a structure line and 
	 * does not have any value/xref fields
	 * 
	 * @return
	 */
	public boolean hasStructureName() {
		return (structureName != null);
	}
	
	/**
	 * Returns the level of this line
	 * 
	 * @return
	 */
	public int getLevel() {
		return parentBlock.getLevel();
	}
	
	/**
	 * Returns true if this line has sub-lines (with higher levels than this line) 
	 * and therefore has a child block which contains all the sub-lines.
	 * 
	 * @return
	 */
	public boolean hasChildBlock() {
		return (childBlock != null);
	}
	
	/**
	 * Returns <code>true</code> if the given tag name is a possible tag name 
	 * for this line
	 * 
	 * @param tag
	 * @return
	 */
	public boolean hasTag(String tag) {
		return getTagNames().contains(tag);
	}

	
	
	@Override
	public String toString() {
		return GedcomStorePrinter.preparePrint(this).toString();
	}
}
