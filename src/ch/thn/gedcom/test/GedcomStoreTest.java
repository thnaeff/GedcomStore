/**
 * 
 */
package ch.thn.gedcom.test;


import ch.thn.gedcom.GedcomToString;
import ch.thn.gedcom.store.GedcomParseException;
import ch.thn.gedcom.store.GedcomStore;

/**
 * @author thomas
 *
 */
public class GedcomStoreTest {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GedcomStore store = new GedcomStore();
		
		store.showParsingOutput(false);
		
		try {
			store.parse("/home/thomas/Projects/java/GedcomStore/gedcomobjects_5.5.1_test.gedg");
		} catch (GedcomParseException e) {
			e.printStackTrace();
		}
		
	
		System.out.println("\n\n--------------------------------------\n");
		
		GedcomToString.showLevelLineIndex(true);
		
//		System.out.println(GedcomPrinter.preparePrint(store, 0, true));
		
//		System.out.println(">" + GedcomPrinter.preparePrint(store.getGedcomBlock("HEADER", GedcomBlock.COPY_MODE_ALL), 0, true, true));
//		System.out.println(">" + GedcomPrinter.preparePrint(store.getGedcomBlock("FAM_RECORD"), 0, false));
//		System.out.println(">" + GedcomPrinter.preparePrint(store.getGedcomBlock("MULTIMEDIA_RECORD"), 0, false));
//		System.out.println(">" + GedcomPrinter.preparePrint(store.getGedcomBlock("FAMILY_EVENT_STRUCTURE", "ENGA"), 0, false));
//		System.out.println(">" + GedcomPrinter.preparePrint(store.getGedcomBlock("LDS_INDIVIDUAL_ORDINANCE", "SLGC"), 0, false));
//		System.out.println(">" + GedcomPrinter.preparePrint(store.getGedcomBlock("MULTIMEDIA_LINK", "OBJE", false, false), 0, false));
//		System.out.println(">" + GedcomPrinter.preparePrint(store.getGedcomBlock("NOTE_STRUCTURE", "NOTE"), 0, false));
//		System.out.println(">" + GedcomPrinter.preparePrint(store.getGedcomBlock("INDIVIDUAL_EVENT_DETAIL"), 0, false));

		
//		GedcomBlock b1 = store.getGedcomBlock("HEADER", GedcomBlock.COPY_MODE_MANDATORY);
//		System.out.println(GedcomPrinter.preparePrint(b1, 0, true, true));
//		GedcomBlock b2 = b1.getChildLine("HEAD", 0).getBlock();
//		System.out.println(GedcomPrinter.preparePrint(b2, 1, true, true));
//		GedcomBlock b3 = b2.getChildLine("GEDC", 0).getBlock();
//		System.out.println(GedcomPrinter.preparePrint(b3, 1, true, true));
//		
//		GedcomLine l4 = b2.addTagLine("DATE");
//		System.out.println(GedcomPrinter.preparePrint(l4, 1, true, true));
//		
//		GedcomLine l5 = b2.addTagLine("NOTE");
//		System.out.println(GedcomPrinter.preparePrint(l5, 1, true, true));
//		
//		GedcomBlock b6 = b2.getChildLine("SOUR", 0).getBlock();
//		System.out.println(GedcomPrinter.preparePrint(b6, 1, true, true));
//		
//		GedcomLine l7 = b6.addTagLine("CORP");
//		System.out.println(GedcomPrinter.preparePrint(l7, 1, true, true));
//		
//		GedcomLine l8 = l7.getParentBlock().getChildLine("CORP", 0).getBlock().addStructureLine("ADDRESS_STRUCTURE");
//		System.out.println(GedcomPrinter.preparePrint(l8, 1, true, true));
//		
//		GedcomLine l9 = l8.getParentBlock().getChildLine("ADDRESS_STRUCTURE", 0).getBlock().addTagLine("PHON").getParentBlock().addTagLine("EMAIL").getParentBlock().addTagLine("EMAIL");
//		System.out.println(GedcomPrinter.preparePrint(l9, 1, true, true));
//		
//		System.out.println(">>" + GedcomPrinter.preparePrint(b1, 0, true, true));
//		
//		System.out.println("====");
//		
//		b1.followPath("HEAD", "SOUR", "CORP", "ADDRESS_STRUCTURE", "EMAIL;2").getTagLine().setValue("some.e@mail.ch");
//		b1.followPath("HEAD", "DATE", "TIME");
//		System.out.println(">>" + GedcomPrinter.preparePrint(b1, 0, true, true));	
		
		
//		GedcomBlock b1 = store.getGedcomBlock("INDIVIDUAL_RECORD", GedcomBlock.ADD_MANDATORY);
//		System.out.println(GedcomPrinter.preparePrint(b1, 0, true, true));
//		
//		GedcomLine line = b1.getChildLine("INDI").getBlock().addStructureLine("PERSONAL_NAME_STRUCTURE").getChildLine("NAME");
//		System.out.println("Line:\n" + GedcomPrinter.preparePrint(line));
//		System.out.println("Line:\n" + GedcomPrinter.preparePrint(line, 2, false, true));
//		
//		GedcomObject o1 = b1.followPath("INDI", "PERSONAL_NAME_STRUCTURE", "NAME");
//		System.out.println("Block:\n" + GedcomPrinter.preparePrint(o1.getParentBlock(), 0, true, true));
		
		System.out.println(GedcomToString.preparePrint(store, 0, true));
	}

}