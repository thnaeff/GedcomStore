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

import java.util.LinkedList;

import ch.thn.gedcom.data.GedcomLine;
import ch.thn.gedcom.data.GedcomNode;
import ch.thn.util.tree.onoff.OnOffTreeUtil;
import ch.thn.util.tree.printer.text.LeftRightTextTreePrinter;
import ch.thn.util.tree.printer.text.TextTreePrinterLines;

/**
 * A printer which prints the gedcom structure in text format. The output 
 * of this printer can be saved in a gedcom textfile to import the gedcom data 
 * into a software which supports the gedcom standard.
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomStructureTextPrinter extends LeftRightTextTreePrinter<GedcomLine, GedcomNode> {
	
	/**
	 * Prints the gedcom structure
	 * 
	 */
	public GedcomStructureTextPrinter() {
		this(true);
	}
	
	
	/**
	 * 
	 * 
	 * @param flatStructure If set to <code>true</code>, there is no indentation 
	 * for the output and all lines are printed all the way on the left.
	 */
	public GedcomStructureTextPrinter(boolean flatStructure) {
		super();
		
		if (flatStructure) {
			HEAD = "";
			FIRST_CHILD = "";
			START = "";
			END = "";
			INTERMEDIATE = "";
			THROUGH = "";
			AFTEREND = "";
			ADDITIONALLINETHROUGH = "";
			ADDITIONALLINEAFTEREND = "";
		} else {
			HEAD = "";
			FIRST_CHILD = "";
			START = "  ";
			END = "  ";
			INTERMEDIATE = "  ";
			THROUGH = "  ";
			AFTEREND = "  ";
			ADDITIONALLINETHROUGH = "  ";
			ADDITIONALLINEAFTEREND = "  ";
		}
		
	}
	

	@Override
	protected TextTreePrinterLines getNodeData(GedcomNode node) {
		TextTreePrinterLines lines = new TextTreePrinterLines();
		
		if (node.getNodeValue() != null) {
			int index = lines.addNewLine();
			lines.addValue(index, node.getNodeDepth() + " ");
			lines.addValue(index, node.getNodeValue().toString());
		}
		
		return lines;
	}
	
	@Override
	public StringBuilder print(GedcomNode printNode) {
		LinkedList<GedcomNode> trees = OnOffTreeUtil.convertToSimpleTree(printNode, true, true);
		//There is only one tree since only the structure name is ignored and it 
		//continues with the first tag line which is not ignored
		return super.print(trees.get(0));
	}
	
	
}
