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

import ch.thn.datatree.onoff.OnOffTreeUtil;
import ch.thn.datatree.printer.TreeNodeHTMLPrinter;
import ch.thn.gedcom.data.GedcomNode;
/**
 * This gedcom data printer prints the HTML code to view the gedcom structure
 * as HTML file, for example in a web browser.
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomStructureHTMLPrinter extends TreeNodeHTMLPrinter<GedcomNode> {


  private static final String HTMLSPACE = "&nbsp;";

  /**
   *
   *
   * @param useColors
   * @param showLines
   */
  public GedcomStructureHTMLPrinter(boolean useColors, boolean showLines) {
    super(false, useColors);

    if (!showLines) {
      HEAD = null;
      LEFT_SPACE = null;
      FIRST_CHILD = HTMLSPACE + HTMLSPACE + HTMLSPACE;
      LAST_NODE = HTMLSPACE + HTMLSPACE + HTMLSPACE;
      THROUGH = HTMLSPACE + HTMLSPACE + HTMLSPACE;
      AFTEREND = "";
      ADDITIONALLINE_THROUGH = HTMLSPACE + HTMLSPACE + HTMLSPACE;
      ADDITIONALLINE_AFTEREND = null;
      ADDITIONALLINE_CONNECTFIRST = null;
      ADDITIONALLINE_CONNECTFIRSTNOCHILD = null;
    }

  }


  @Override
  protected Collection<String> getNodeValues(GedcomNode node) {
    List<String> values = new ArrayList<>();

    if (node.getNodeValue() != null) {
      values.add(node.getNodeDepth() + HTMLSPACE + HTMLSPACE + node.getNodeValue().toString());
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
