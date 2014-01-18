/**
 * 
 */
package ch.thn.gedcom.test;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import ch.thn.gedcom.data.GedcomLine;
import ch.thn.gedcom.data.GedcomNode;
import ch.thn.gedcom.data.GedcomTree;
import ch.thn.gedcom.printer.GedcomStructureHTMLPrinter;
import ch.thn.gedcom.printer.GedcomStorePrinter;
import ch.thn.gedcom.printer.GedcomStructureTextPrinter;
import ch.thn.gedcom.printer.GedcomStructureTreePrinter;
import ch.thn.gedcom.store.GedcomParseException;
import ch.thn.gedcom.store.GedcomStore;
import ch.thn.util.tree.printer.SimpleTreePrinter;

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
				
		GedcomStorePrinter.showLevelLineIndex(true);
		
		GedcomTree header = store.getGedcomTree("HEADER");
		
		GedcomNode header1 = header.addChildLine("HEAD");
		header1.newLine();
		
		GedcomNode header11 = header1.addChildLine("SOUR");
		GedcomNode header12 = header1.addChildLine("GEDC");
		
		GedcomNode header111 = header11.addChildLine("VERS");
		header11.addChildLine("NAME");
		header11.addChildLine("CORP");
		
		header111.setTagLineValue("version");
				
		System.out.println(header.print(new GedcomStructureTextPrinter()));
		
		
		
		GedcomTree indi = store.getGedcomTree("INDIVIDUAL_RECORD");
		
		GedcomNode indi1 = indi.addChildLine("INDI");
		indi1.setTagLineXRef("I987");
		
		GedcomNode indi11 = indi1.addChildLine("SEX").setTagLineValue("M");
		GedcomNode indi12 = indi1.addChildLine("INDIVIDUAL_EVENT_STRUCTURE", "BIRT");
		GedcomNode indi13 = indi1.addChildLine("SPOUSE_TO_FAMILY_LINK");
		GedcomNode indi14 = indi1.addChildLine("CHANGE_DATE");
		
		indi11.newLine();
		
		GedcomNode indi131 = indi13.addChildLine("FAMS");
		indi131.setTagLineXRef("famslink1");
		
		GedcomNode indi141 = indi14.addChildLine("CHAN");
		
		GedcomNode indi1411 = indi141.addChildLine("DATE");
		indi1411.setTagLineValue("date");
		
		GedcomNode indi121 = indi12.addChildLine("BIRT");
		indi121.setTagLineValue("Y");
		indi121.addChildLine("INDIVIDUAL_EVENT_DETAIL").addChildLine("EVENT_DETAIL").addChildLine("DATE"); //.setTagLineValue("birth date");
		
		indi13.newLine().addChildLine("FAMS").setTagLineXRef("famslink2");
		
		indi1.addChildLine("CHILD_TO_FAMILY_LINK").addChildLine("FAMC").setTagLineXRef("famclink");
		
		
		
		System.out.println(indi.print(new GedcomStructureTextPrinter()));
		System.out.println(indi.print(new GedcomStructureTreePrinter(true)));
		
		writeToFile("/home/thomas/Desktop/familienfest/gedcomtest.html", indi.print(new GedcomStructureHTMLPrinter("Test Tree")));
		
	}
	
	
	
	private static void writeToFile(String filename, StringBuilder string) {
		Writer output = null;
		
		try {
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
		} catch (IOException e) {
			System.out.println("failed to open writer");
			return;
		}
		
		try {
			output.write(string.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			output.close();
		} catch (IOException e) {
			System.out.println("failed to close file stream");
		}
	}

}
