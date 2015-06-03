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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.thn.gedcom.data.GedcomNode;
import ch.thn.util.tree.onoff.OnOffTreeUtil;
import ch.thn.util.tree.printer.TreeNodePlainTextPrinter;

/**
 * A printer which prints the gedcom structure in text format. The output
 * of this printer can be saved in a gedcom textfile to import the gedcom data
 * into a software which supports the gedcom standard.
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomStructureTextPrinter extends TreeNodePlainTextPrinter<GedcomNode> {

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
			HEAD = null;
			LEFT_SPACE = null;
			FIRST_CHILD = null;
			LAST_NODE = null;
			THROUGH = null;
			AFTEREND = null;
			ADDITIONALLINE_THROUGH = null;
			ADDITIONALLINE_AFTEREND = null;
			ADDITIONALLINE_CONNECTFIRST = null;
			ADDITIONALLINE_CONNECTFIRSTNOCHILD = null;
		} else {
			HEAD = null;
			LEFT_SPACE = null;
			FIRST_CHILD = "  ";
			LAST_NODE = "  ";
			THROUGH = "  ";
			AFTEREND = "  ";
			ADDITIONALLINE_THROUGH = "  ";
			ADDITIONALLINE_AFTEREND = "  ";
			ADDITIONALLINE_CONNECTFIRST = " ";
			ADDITIONALLINE_CONNECTFIRSTNOCHILD = " ";
		}



	}


	@Override
	protected Collection<String> getNodeValues(GedcomNode node) {
		List<String> values = new ArrayList<>();

		if (node.getNodeValue() != null) {
			values.add(node.getNodeDepth() + " " + node.getNodeValue().toString());
		}


		return values;
	}

	@Override
	public StringBuilder print(GedcomNode printNode) {
		LinkedList<GedcomNode> trees = OnOffTreeUtil.convertToSimpleTree(printNode, true, true);
		//There is only one tree since only the structure name is ignored and it
		//continues with the first tag line which is not ignored
		return super.print(trees.get(0));
	}


}
