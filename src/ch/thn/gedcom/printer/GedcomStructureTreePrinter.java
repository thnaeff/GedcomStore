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
package ch.thn.gedcom.printer;

import ch.thn.gedcom.data.GedcomLine;
import ch.thn.gedcom.data.GedcomNode;
import ch.thn.util.tree.printable.printer.TextTreePrinterLines;
import ch.thn.util.tree.printable.printer.TreePrinterNode;
import ch.thn.util.tree.printable.printer.vertical.GenericVerticalDebugTextTreePrinter;

/**
 * Simply extends {@link DebugVerticalTreePrinter} and is very useful for debugging 
 * a gedcom tree when building it.
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 * 
 * @see DebugVerticalTreePrinter
 */
public class GedcomStructureTreePrinter extends GenericVerticalDebugTextTreePrinter<String, GedcomLine, GedcomNode> {

	/**
	 * 
	 * 
	 * @param showAllNodes
	 */
	public GedcomStructureTreePrinter(boolean showAllNodes) {
		super(!showAllNodes, !showAllNodes, false, false);
	}

	@Override
	protected TextTreePrinterLines getNodeData(GedcomNode node) {
		TextTreePrinterLines lines = new TextTreePrinterLines();
		
		if (node.getNodeLine() != null) {
			lines.addNewLine(node.toString());
		}
		
		return lines;
	}

	@Override
	protected void preProcessingNode(
			TreePrinterNode<String, TextTreePrinterLines> printerNode,
			int currentNodeLevel, int currentNodeIndex, int currentNodeCount,
			boolean isHeadNode, boolean isFirstChildNode,
			boolean isLastChildNode, boolean hasChildNodes) {
	}

	@Override
	protected void postProcessingNode(
			TreePrinterNode<String, TextTreePrinterLines> printerNode,
			int currentNodeLevel, int currentNodeIndex, int currentNodeCount,
			boolean isHeadNode, boolean isFirstChildNode,
			boolean isLastChildNode, boolean hasChildNodes) {
	}
	
	
}
