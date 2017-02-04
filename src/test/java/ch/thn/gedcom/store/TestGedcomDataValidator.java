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
package ch.thn.gedcom.store;

import java.util.regex.Pattern;

import ch.thn.gedcom.data.GedcomDataValidator;
import ch.thn.gedcom.data.GedcomTagLine;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class TestGedcomDataValidator extends GedcomDataValidator {
	
	public static final Pattern pattern = Pattern.compile("^[A-Za-z0-9_ ]*$");

	
	

	@Override
	public boolean validateValue(GedcomTagLine tagLine, String valueName, String value) {
		
		//The total length of a GEDCOM line, including level number, cross-reference number, tag, value,
		//delimiters, and terminator, must not exceed 255 (wide) characters
		//Just about the length...
		if (value.length() > (255 - tagLine.getTag().length() - 3)) {
			System.err.println("Value line with '" + value + "' is too long");
			return false;
		}
		
		if (!pattern.matcher(value).find()) {
			System.err.println("Value '" + value + "' contans illegal characters");
			return false;
		}
		
		return true;
	}

	@Override
	public boolean validateXRef(GedcomTagLine tagLine, String xrefName, String xref) {
		
		//The total length of a GEDCOM line, including level number, cross-reference number, tag, value,
		//delimiters, and terminator, must not exceed 255 (wide) characters
		//Just about the length...
		if (xref.length() > (255 - tagLine.getTag().length() - 3)) {
			System.err.println("XRef line with '" + xref + "' is too long");
			return false;
		}
		
		if (!pattern.matcher(xref).find()) {
			System.err.println("XRef '" + xref + "' contans illegal characters");
			return false;
		}
		
		return true;
	}

}
