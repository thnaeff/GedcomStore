/**
 * 
 */
package ch.thn.gedcom.store;

import java.util.LinkedList;

/**
 * @author thomas
 *
 */
public class GedcomStoreLine {
	
	private int min = 0;
	private int max = 0;
	private int pos = 0;
	
	private LinkedList<String> xrefNames = null;
	private LinkedList<String> valueNames = null;
	private LinkedList<String> tagNames1 = null;
	private LinkedList<String> tagNames2 = null;
	
	private String structureName = null;
	private String originalGedcomDefinitionLine = null;
	
	
	private GedcomStoreBlock parentBlock = null;
	private GedcomStoreBlock childBlock = null;
	
	
	/**
	 * 
	 */
	public GedcomStoreLine(GedcomStoreBlock parentBlock) {
		this.parentBlock = parentBlock;
		
		
		xrefNames = new LinkedList<String>();
		valueNames = new LinkedList<String>();
		tagNames1 = new LinkedList<String>();
		tagNames2 = new LinkedList<String>();
		
	}
	
	
	
	protected void setChildBlock(GedcomStoreBlock childBlock) {
		this.childBlock = childBlock;
	}
	
	
	protected boolean parse(String gedcomDefinitionLine) {
		originalGedcomDefinitionLine = gedcomDefinitionLine;
		
		//Clean the line from all unnecessary stuff
		gedcomDefinitionLine = GedcomHelper.removeAll(GedcomHelper.commentPattern, gedcomDefinitionLine);
		gedcomDefinitionLine = GedcomHelper.replaceAll(GedcomHelper.levelPattern, gedcomDefinitionLine, "");
		gedcomDefinitionLine = GedcomHelper.replaceAll(GedcomHelper.spacesPattern, gedcomDefinitionLine, " ");
		gedcomDefinitionLine = gedcomDefinitionLine.trim();
		
		//Split
		String[] splitLine = gedcomDefinitionLine.split(" ");
		
		//Check if line could be valid
		if (splitLine.length < 1 || splitLine.length > 4) {
			System.out.println("[ERROR] Failed to parse line '" + gedcomDefinitionLine + "'. Number of items not in the range.");
			return false;
		}
		
		
		for (int i = 0; i < splitLine.length; i++) {
			
			if (GedcomHelper.matches(GedcomHelper.xrefPatternWhole, splitLine[i])) {
				//@<XREF:TAG>
				xrefNames.add(GedcomHelper.removeAll(GedcomHelper.xrefTagReplace, splitLine[i]));
				
			} else if (splitLine[i].contains("@") && GedcomHelper.matches(GedcomHelper.multipleXRefsWhole, splitLine[i])) {
				//Multiple XREF ([@<XREF>@|@<XREF>@|<NULL>...])
				//At least one @ has to be present
				
				String[] values = GedcomHelper.replaceAllInPattern(GedcomHelper.multipleXRefs, splitLine[i], GedcomHelper.multipleXRefsReplace, "").split("\\|");
				
				for (int j = 0; j < values.length; j++) {
					xrefNames.add(values[j]);
				}
				
			} else if (GedcomHelper.matches(GedcomHelper.minMaxPatternWhole, splitLine[i])) {
				//{MIN:MAX}
				//{MIN:MAX*}
				//{MIN:MAX}*
				String[] minmax = GedcomHelper.removeAll(GedcomHelper.minMaxReplace, splitLine[i]).split(":");
				min = Integer.parseInt(minmax[0]);
				
				if (!minmax[1].equals("M")) {
					max = Integer.parseInt(minmax[1]);
				}
				
			} else if (GedcomHelper.matches(GedcomHelper.structurePatternWhole, splitLine[i])) {
				//<<STRUCTURE>>
				structureName = GedcomHelper.removeAll(GedcomHelper.structureReplace, splitLine[i]);
				
			} else if (GedcomHelper.matches(GedcomHelper.valuePatternWhole, splitLine[i])) {
				//<VALUE>
				valueNames.add(GedcomHelper.removeAll(GedcomHelper.valueReplace, splitLine[i]));
				
			} else if (GedcomHelper.matches(GedcomHelper.multipleValuesWhole, splitLine[i])) {
				//Multiple VALUE ([<ABC>|<DEF>|<GHI>...])
				
				String[] values = GedcomHelper.replaceAllInPattern(GedcomHelper.multipleValues, splitLine[i], GedcomHelper.multipleValuesReplace, "").split("\\|");
				
				for (int j = 0; j < values.length; j++) {
					valueNames.add(values[j]);
				}
				
			}  else if (GedcomHelper.matches(GedcomHelper.tagPatternWhole, splitLine[i])) {
				//TAG
				if (xrefNames.size() == 0) {
					tagNames1.add(splitLine[i]);
				} else {
					tagNames2.add(splitLine[1]);
				}
				
			} else if (GedcomHelper.matches(GedcomHelper.multipleTags, splitLine[i])) {
				//Multiple TAG ([ABC|DEF|GHI...])

				String[] tags = GedcomHelper.replaceAllInPattern(GedcomHelper.multipleTags, splitLine[i], GedcomHelper.multipleTagsReplace, "").split("\\|");
				
				for (int j = 0; j < tags.length; j++) {
					if (xrefNames.size() == 0) {
						tagNames1.add(tags[j]);
					} else {
						tagNames2.add(tags[j]);
					}
				}
				
			} else {
				System.out.println("[INFO] Did not process " + splitLine[i] + " in " + getId() + " under " + getParentBlock().getStoreStructure().getStructureName());
			}
			
		}
		
		if (parentBlock.getStoreStructure().getStore().showParsingOutput()) {
			System.out.println("  parsed: " + GedcomPrinter.preparePrint(this));
		}
		
		return true;
		
	}
	
	protected void setPos(int pos) {
		this.pos = pos;
	}
	
	protected int getPos() {
		return pos;
	}
	
	protected GedcomStoreBlock getChildBlock() {
		return childBlock;
	}
	
	protected GedcomStoreBlock getParentBlock() {
		return parentBlock;
	}
	
	protected GedcomStoreStructure getStoreStructure() {
		if (structureName == null) {
			return null;
		}
		
		LinkedList<GedcomStoreStructure> storeStructures = parentBlock.getStoreStructure().getStore().getVariations(structureName);
		
		if (storeStructures == null || storeStructures.size() > 1) {
			return null;
		}
		
		return storeStructures.get(0);
	}
	
	protected String getOriginalGedcomDefinitionLine() {
		return originalGedcomDefinitionLine;
	}
	
	
	
	
	/**
	 * This method can be used if only one tag name for this line exists. If there 
	 * are multiple tag names, null is returned.
	 * 
	 * @param parentBlock
	 * @return
	 */
	protected GedcomLine getLineInstance(GedcomBlock parentBlock, int copyMode) {
		
		if (structureName != null) {
			return new GedcomStructureLine(this, parentBlock, copyMode);
		} else {
			LinkedList<String> tagNames = getTagNames();
			
			if (tagNames.size() != 1) {
				return null;
			}
			
			return new GedcomTagLine(this, parentBlock, getTagNames().get(0), copyMode);
		}
	}
	
	/**
	 * Returns a {@link GedcomStructureLine} if a structure name is set, or a 
	 * {@link GedcomTagLine} if no structure name is set and the given tag is 
	 * valid.
	 * 
	 * @param parentBlock
	 * @param tag
	 * @return
	 */
	protected GedcomLine getLineInstance(GedcomBlock parentBlock, String tag, int copyMode) {
		
		if (structureName != null) {
			return new GedcomStructureLine(this, parentBlock, tag, copyMode);
		} else {
			if (!hasTag(tag)) {
				return null;
			}
			
			return new GedcomTagLine(this, parentBlock, tag, copyMode);
		}
	}
	
	/**
	 * 
	 * @param parentBlock
	 * @param tag
	 * @param withXRef
	 * @param withValue
	 * @return
	 */
	protected GedcomLine getLineInstance(GedcomBlock parentBlock, String tag, 
			boolean withXRef, boolean withValue, int copyMode) {
		
		if (structureName != null) {
			return new GedcomStructureLine(this, parentBlock, tag, withXRef, withValue, copyMode);
		} else {
			if (!hasTag(tag)) {
				return null;
			}
			
			return new GedcomTagLine(this, parentBlock, tag, copyMode);
		}
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	public boolean isMandatory() {
		return (min >= 1);
	}
	
	public String getStructureName() {
		return structureName;
	}
	
	public String getId() {
		if (structureName != null) {
			return structureName;
		} else {
			return GedcomFormatter.makeOrList(getTagNames(), "", "").toString();
		}
	}
	
	public LinkedList<String> getTagNames() {
		if (tagNames1.size() > 0) {
			return tagNames1;
		}
		
		return tagNames2;
	}
	
	public LinkedList<String> getXRefNames() {
		return xrefNames;
	}
	
	public LinkedList<String> getValueNames() {
		return valueNames;
	}
	
	public boolean hasTags() {
		return (getTagNames().size() > 0);
	}
	
	public boolean hasTagBeforeXRef() {
		return (tagNames1.size() > 0);
	}
	
	public boolean hasTagAfterXRef() {
		return (tagNames2.size() > 0);
	}
	
	public boolean hasXRefNames() {
		return (xrefNames.size() > 0);
	}
	
	public boolean hasValueNames() {
		return (valueNames.size() > 0);
	}
	
	public boolean hasMultipleTagNames() {
		return (getTagNames().size() > 1);
	}
	
	public boolean hasStructureName() {
		return (structureName != null);
	}
	
	public int getLevel() {
		return parentBlock.getLevel();
	}
	
	public boolean hasChildBlock() {
		return (childBlock != null);
	}
	
	public boolean hasTag(String tag) {
		LinkedList<String> tags = getTagNames();
		
		for (String t : tags) {
			if (t.equals(tag)) {
				return true;
			}
		}
		
		return false;
	}

	
	
	@Override
	public String toString() {
		return GedcomPrinter.preparePrint(this).toString();
	}
}
