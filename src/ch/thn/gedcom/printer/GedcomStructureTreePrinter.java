/**
 * 
 */
package ch.thn.gedcom.printer;

import ch.thn.gedcom.data.GedcomLine;
import ch.thn.util.tree.PrintableTreeNode;
import ch.thn.util.tree.printer.GenericTreePrinter;

/**
 * A printer which prints the whole tree structure of the gedcom data and also 
 * shows the tree lines connecting the tree elements. In addition, it has a flag 
 * to turn the invisible/hidden nodes on or off. Setting the invisible nodes to on is very 
 * useful when creating a gedcom structure since it shows all the added lines 
 * and not only the elements which would be printed. This is great for debugging 
 * the tree building process.<br>
 * <br>
 * This printer adds some information in front of each line:<br>
 * - The actual level of the line (a) and the level the line would have if invisible 
 * lines would not be printed (b) in the format "a-b"<br>
 * - Some tags which show if the line is set to invisible [i], hidden [h] or set 
 * to hidden child lines [c].
 * 
 * @author thomas
 *
 */
public class GedcomStructureTreePrinter extends GenericTreePrinter<String, GedcomLine> {

	/**
	 * 
	 * 
	 * @param showAllNodes
	 */
	public GedcomStructureTreePrinter(boolean showAllNodes) {
		super(!showAllNodes, !showAllNodes);
		
	}
	
	@Override
	public StringBuilder getNodeValue(PrintableTreeNode<String, GedcomLine> node) {
		StringBuilder sb = new StringBuilder();
		sb.append(node.getAsTreeNode().getNodeLevel(false));
		sb.append("-" + node.getAsTreeNode().getNodeLevel(true) + " ");
		
		if (node.isInvisibleNode()) {
			sb.append("[i] ");
		} else if (!node.printNode()) {
			sb.append("[h] ");
		} else if (!node.printChildNodes()) {
			sb.append("[c] ");
		}
		
		sb.append(node.print());
		
		return sb;
	}
	
	
}
