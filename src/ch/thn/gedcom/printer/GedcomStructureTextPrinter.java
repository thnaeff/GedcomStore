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

import java.util.ArrayList;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.data.GedcomLine;
import ch.thn.util.tree.printable.PrintableTreeNode;
import ch.thn.util.tree.printable.printer.TextTreePrinterLines;
import ch.thn.util.tree.printable.printer.TreePrinter;
import ch.thn.util.tree.printable.printer.TreePrinterNode;
import ch.thn.util.tree.printable.printer.TreePrinterTree;
import ch.thn.util.tree.printable.printer.vertical.VerticalTextTreePrinter;

/**
 * A printer which prints the gedcom structure in text format. The output 
 * of this printer can be saved in a gedcom textfile to import the gedcom data 
 * into a software which supports the gedcom standard.
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomStructureTextPrinter extends VerticalTextTreePrinter<String, GedcomLine> {

	private boolean flatStructure = false;
	
	/**
	 * 
	 * 
	 * @param flatStructure If set to <code>true</code>, there is no indentation 
	 * for the output and all lines are printed all the way on the left.
	 */
	public GedcomStructureTextPrinter(boolean flatStructure) {
		super(true, true, false, false);
		this.flatStructure = flatStructure;
		
		HEAD = "";
		FIRST_CHILD = "";
		START = "";
		END = "";
		INTERMEDIATE = "";
		THROUGH = "";
		AFTEREND = "";
		ADDITIONALLINETHROUGH = "";
		ADDITIONALLINEAFTEREND = "";
		
	}
	

	@Override
	protected TextTreePrinterLines getNodeData(PrintableTreeNode<String, GedcomLine> node) {
		TextTreePrinterLines lines = new TextTreePrinterLines();
		
		int lineIndex = lines.addNewLine();
		lines.addValue(lineIndex, node.toString());
		
		return lines;
	}
	
	@Override
	protected StringBuilder createPrinterOutput(
			ArrayList<TreePrinterTree<String, TextTreePrinterLines>> preparedTrees) {
		
		StringBuilder sb = new StringBuilder();
		
		for (TreePrinterNode<String, TextTreePrinterLines> tree : preparedTrees) {
			TextTreePrinterLines lines = null;
			TreePrinterNode<String, TextTreePrinterLines> nextTreeLine = tree;
			
			while (nextTreeLine != null) {
				lines = nextTreeLine.getNodeValue();
				
				//All the lines
				for (int i = 0; i < lines.getLineCount(); i++) {
					//All the prefixes
					for (int j = 0; j < lines.getPrefixCount(i); j++) {
						sb.append(lines.getPrefix(i, j));
					}
					
					//All the values
					for (int j = 0; j < lines.getValueCount(i); j++) {
						if (i == 0 && j == 0) {
							//Node level in front of the first value and only on 
							//the first line of a node
							int level = nextTreeLine.getNodeLevel();
							if (!flatStructure) {
								sb.append(GedcomFormatter.makeInset(level));
							}
							
							sb.append(level);
							sb.append(" ");
						}
						//Value
						sb.append(lines.getValue(i, j));
					}
					
					sb.append(TreePrinter.LINE_SEPARATOR);
				}
				
				nextTreeLine = nextTreeLine.getNextPrinterNodeVertical();
			}
			
			//Space before a new tree starts
			sb.append(TreePrinter.LINE_SEPARATOR);
		}
		
		return sb;
		
	}
}
