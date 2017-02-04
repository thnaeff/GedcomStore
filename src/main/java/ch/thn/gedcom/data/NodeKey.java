/**
 *    Copyright 2014 Thomas Naeff (github.com/thnaeff)
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
package ch.thn.gedcom.data;

import ch.thn.gedcom.store.GedcomStoreLine;
import ch.thn.numberutil.NumberUtil;

/**
 * The node key as object, to allow ordering of the keys
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class NodeKey {

  private String key = null;
  private String orderingString = null;

  private int ordering = 0;
  private int originalOrdering = 0;

  private boolean simpleTree = false;

  /**
   *
   *
   * @param key
   * @param storeLine
   */
  public NodeKey(String key, GedcomStoreLine storeLine) {
    this(key, storeLine == null ? 0 : storeLine.getPos());
  }

  /**
   *
   *
   * @param key
   * @param ordering
   */
  public NodeKey(String key, int ordering) {
    this.key = key;
    this.ordering = ordering;
    originalOrdering = ordering;

    updateOrderingString();

  }

  /**
   *
   *
   */
  private void updateOrderingString() {
    orderingString = NumberUtil.formatNumber(ordering, 2, 0, true, false) + key;
  }

  /**
   * When creating the simple tree (without any invisible structures), the ordering
   * has to be different since now the lower child nodes have to be ordered
   * next to each other. This method sets the ordering of the structure line
   * above the tag line as the ordering of the tag line.
   *
   * @param node
   */
  public void setAsSimpleTreeKey(GedcomNode node) {
    simpleTree = true;

    if (node.isRootNode()) {
      ordering = 0;
    }

    while (node != null && node.getParentNode().getNodeValue() != null &&node.getParentNode().getNodeValue().isStructureLine()) {
      node = node.getParentNode();
    }

    ordering = node == null ? 0 : node.getStoreLine().getPos();

    updateOrderingString();
  }

  /**
   * Returns <code>true</code> if this key has been adjusted as simple tree
   *
   * @return
   */
  public boolean asSimpleTree() {
    return simpleTree;
  }

  /**
   * Returns the original ordering of this key, before it has been adjusted
   * as simple tree
   *
   * @return
   */
  public int getOriginalOrdering() {
    return originalOrdering;
  }

  /**
   *
   *
   * @return
   */
  public String getKey() {
    return key;
  }

  /**
   *
   *
   * @return
   */
  public int getOrdering() {
    return ordering;
  }

  /**
   *
   *
   * @return
   */
  public String getOrderingString() {
    return orderingString;
  }

  @Override
  public String toString() {
    return key;
  }

}
