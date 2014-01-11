/**
 * 
 */
package ch.thn.gedcom.printer;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.data.GedcomLine;
import ch.thn.util.tree.TreeNode;
import ch.thn.util.tree.printer.GenericTreePrinter;
import ch.thn.util.tree.printer.TreePrinter;

/**
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
				true);
		
	}

	
	@Override
	public StringBuilder getNodeValue(TreeNode<String, GedcomLine> node) {
		StringBuilder sb = new StringBuilder();
		sb.append(GedcomFormatter.makeInset(node.getNodeLevel(true)));
		sb.append(node.print());
		return sb;
	}
	
	
}
