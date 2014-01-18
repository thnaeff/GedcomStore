/**
 * 
 */
package ch.thn.gedcom.printer;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.data.GedcomLine;
import ch.thn.util.tree.PrintableTreeNode;
import ch.thn.util.tree.printer.GenericTreePrinter;
import ch.thn.util.tree.printer.TreePrinter;

/**
 * A printer which prints the gedcom structure in text format. The output 
 * of this printer can be saved in a gedcom textfile to import the gedcom data 
 * into a software which supports the gedcom standard.
 * 
 * @author thomas
 *
 */
public class GedcomStructureTextPrinter extends GenericTreePrinter<String, GedcomLine> {

	
	public GedcomStructureTextPrinter() {
		super(
				"", 	//Head
				"", 	//Start
				"", 	//End
				"", 	//Intermediate
				"", 	//Through
				"", 	//After end
				"",		//Additional line through
				"",		//Additional line after end
				"", 	//Start of line
				TreePrinter.LINE_SEPARATOR, 	//End of line
				true, true);
		
	}
	
	@Override
	public StringBuilder getNodeValue(PrintableTreeNode<String, GedcomLine> node) {
		StringBuilder sb = new StringBuilder();
		int level = node.getAsTreeNode().getNodeLevel(true);
		sb.append(GedcomFormatter.makeInset(level));
		sb.append(level);
		sb.append(" ");
		sb.append(node.print());
		return sb;
	}
	
	
}
