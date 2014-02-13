/**
 *    Copyright 2013 Thomas Naeff (github.com/thnaeff)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.thn.gedcom.test;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import ch.thn.gedcom.data.GedcomNode;
import ch.thn.gedcom.data.GedcomTree;
import ch.thn.gedcom.printer.GedcomStructureHTMLPrinter;
import ch.thn.gedcom.printer.GedcomStorePrinter;
import ch.thn.gedcom.printer.GedcomStructureTextPrinter;
import ch.thn.gedcom.printer.GedcomStructureTreePrinter;
import ch.thn.gedcom.store.GedcomParseException;
import ch.thn.gedcom.store.GedcomStore;

/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomStoreTest {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GedcomStore store = new GedcomStore();
		
		store.showParsingOutput(false);
		store.setValidator(new GedcomDataValidatorTest());
		
		try {
			store.parse("/home/thomas/Projects/java/GedcomStore/gedcomobjects_5.5.1_test.gedg");
		} catch (GedcomParseException e) {
			e.printStackTrace();
		}
		
	
		System.out.println("\n\n--------------------------------------\n");
				
		GedcomStorePrinter.showLevelLineIndex(true);
		
		GedcomTree header = store.getGedcomTree("HEADER");
		
		GedcomNode header1 = header.addChildLine("HEAD");
		header1.addAllChildLines(true);
//		header1.addMandatoryChildLines(true);
//		header1.newLine();
//		
//		GedcomNode header11 = header1.addChildLine("SOUR");
//		GedcomNode header12 = header1.addChildLine("GEDC");
//		
//		GedcomNode header111 = header11.addChildLine("VERS");
//		header11.addChildLine("NAME");
//		header11.addChildLine("CORP");
//		
//		header111.setTagLineValue("version");
		
		GedcomStructureTextPrinter textPrinter = new GedcomStructureTextPrinter();
		GedcomStructureTreePrinter structureTreePrinter = new GedcomStructureTreePrinter(true);
		GedcomStructureHTMLPrinter htmlPrinter = new GedcomStructureHTMLPrinter(true);
		
		System.out.println(textPrinter.print(header));
		System.out.println(structureTreePrinter.print(header));
		
		
		
		GedcomTree indi = store.getGedcomTree("INDIVIDUAL_RECORD");
		
		GedcomNode indi1 = indi.addChildLine("INDI");
//		indi1.addAllChildLines();
//		indi1.addMandatoryChildLines();
		
		indi1.setTagLineXRef("I987");
		
		GedcomNode indi12 = indi1.addChildLine("INDIVIDUAL_EVENT_STRUCTURE", "BIRT");
		GedcomNode indi121 = indi12.addChildLine("BIRT");
		indi121.setTagLineValue("Y");
		indi121.addChildLine("INDIVIDUAL_EVENT_DETAIL").addChildLine("EVENT_DETAIL").addChildLine("DATE").setTagLineValue("birth date");
		
		
		
		indi1.addChildLine("INDIVIDUAL_EVENT_STRUCTURE", "DEAT").addChildLine("DEAT").addChildLine("INDIVIDUAL_EVENT_DETAIL").addChildLine("EVENT_DETAIL").addChildLine("DATE").setTagLineValue("death date");
		GedcomNode indi13 = indi1.addChildLine("SPOUSE_TO_FAMILY_LINK");
		GedcomNode indi14 = indi1.addChildLine("CHANGE_DATE");
		
		GedcomNode indi11 = indi1.addChildLine("SEX").setTagLineValue("M");
		indi11.newLine();
		
		GedcomNode indi131 = indi13.addChildLine("FAMS");
		indi131.setTagLineXRef("famslink1");
		
		GedcomNode indi141 = indi14.addChildLine("CHAN");
		
		GedcomNode indi1411 = indi141.addChildLine("DATE");
		indi1411.setTagLineValue("date");
		

		
		
		indi1.addChildLine("CHILD_TO_FAMILY_LINK").addChildLine("FAMC").setTagLineXRef("famclink");
		
		
		indi13.newLine().addChildLine("FAMS").setTagLineXRef("famslink2");
		
		System.out.println("Number of INDIVIDUAL_EVENT_STRUCTURE: " + indi1.getNumberOfChildLines("INDIVIDUAL_EVENT_STRUCTURE"));
		System.out.println("Number of INDIVIDUAL_EVENT_STRUCTURE DEAT: " + indi1.getNumberOfChildLines("INDIVIDUAL_EVENT_STRUCTURE", "DEAT"));

		
		System.out.println(indi1.followPath("CHILD_TO_FAMILY_LINK", "FAMC"));
		
		System.out.println(indi1.followPath("INDIVIDUAL_EVENT_STRUCTURE;DEAT", "DEAT", "INDIVIDUAL_EVENT_DETAIL", "EVENT_DETAIL", "DATE"));
		
		System.out.println(textPrinter.print(indi));
		System.out.println(structureTreePrinter.print(indi));
		
		StringBuilder sb = new StringBuilder();
		
		htmlPrinter.appendSimpleHeader(sb, "GedcomStore Test Tree");
		sb.append(htmlPrinter.print(indi));
		htmlPrinter.appendSimpleFooter(sb);
		
		writeToFile("/home/thomas/Desktop/familienfest/gedcomtest.html", sb);
		
	}
	
	
	/**
	 * 
	 * 
	 * @param filename
	 * @param string
	 */
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
