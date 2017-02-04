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
package ch.thn.gedcom.store;

/**
 * Shows an error that occurred when parsing the grammar linked file. 
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomParseException extends Exception {
	private static final long serialVersionUID = -9166944783238433522L;

	/**
	 * 
	 * 
	 * @param message
	 */
	public GedcomParseException(String message) {
		super(message);
	}
	
}
