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
package ch.thn.gedcom;

import java.util.Date;
import java.util.regex.Pattern;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;

/**
 * Just a helper class which holds various static methods and fields
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomHelper {	
	
	public static final int MAX_AGE = 110;
	
	public static final int BEFORE = -1;
	public static final int SAME = 0;
	public static final int AFTER = 1;
	
	
	/** Matches multiple tags ([ABC|DEF|GHI|JKL]) */
	public static final Pattern multipleTags = Pattern.compile("([\\[\\|]?[A-Z]+\\|?)+\\]");
	/**  */
	public static final Pattern multipleTagsReplace = Pattern.compile("[\\[\\] ]");
	/** Matches [&lt;EVENT_DESCRIPTOR&gt;|&lt;NULL&gt;] etc */
	public static final Pattern multipleValues = Pattern.compile("([\\[\\|]<[A-Z_]+>\\|?)+\\]");
	/**  */
	public static final Pattern multipleValuesReplace = Pattern.compile("[\\[\\] <>]");
	/** */
	public static final Pattern multipleXRefs = Pattern.compile("([\\[\\|]@?<[A-Z_:]+>@?\\|?)+\\]");
	/**  */
	public static final Pattern multipleXRefsReplace = Pattern.compile("[\\[\\] <>@]");
	/** Matches any or-item (the | sign), with or without leading and trailing spaces */
	public static final Pattern orPattern = Pattern.compile("[ ]*\\|[ ]*");
	/** Matches the [ bracket, with or without trailing spaces */
	public static final Pattern bracketOpen = Pattern.compile("\\[[ ]*");
	/** Matches the ] bracket, with or without leading spaces */
	public static final Pattern bracketClose = Pattern.compile("[ ]*\\]");
	/**  */
	public static final Pattern xrefPattern = Pattern.compile("@<.*>@");
	/**  */
	public static final Pattern xrefTagReplace = Pattern.compile("[@<|>@]");
	/**  */
	public static final Pattern minMaxPattern = Pattern.compile("\\{\\d:[\\d|M]\\*?\\}\\*?");
	/**  */
	public static final Pattern minMaxReplace = Pattern.compile("[{|}|*]");
	/**  */
	public static final Pattern structurePattern = Pattern.compile("<<.*>>");
	/**  */
	public static final Pattern structureReplace = Pattern.compile("[<<|>>]");
	/**  */
	public static final Pattern valuePattern = Pattern.compile("<.*>");
	/**  */
	public static final Pattern valueReplace = Pattern.compile("[<|>]");
	/**  */
	public static final Pattern tagPattern = Pattern.compile("[A-Z]+[1-9]*");	
	/**  */
	public static final Pattern commentPattern = Pattern.compile("/\\*.*\\*/");
	/**  */
	public static final Pattern spacesPattern = Pattern.compile("[ \t]+");
	/**  */
	public static final Pattern leadingTrailingPatternWhole = Pattern.compile("^[ \t\r\n]+|[ \t\r\n]+$");
	/**  */
	public static final Pattern structureNamePattern = Pattern.compile("[A-Z_]+:=");
	/**  */
	public static final Pattern idPattern = Pattern.compile("[A-Z]+([_:]*[A-Z])+");
	/**  */
	public static final Pattern levelPattern = Pattern.compile("^(n|\\+[1-9]|\\+[1-9][0-9]) ");
	/**  */
	public static final Pattern subBlockDivider = Pattern.compile("^[\\[\\|\\]]");
	/** */
	public static final Pattern multipleValuePossibilities = Pattern.compile("([\\[\\|]<?[A-Z_]+>?\\|?)+\\]");
	
	
	/** Checks if there is at least one space between the different line items */
	public static final Pattern errorCheckSpacingBefore = Pattern.compile("[^ <>@\\[\\|]+[<@\\[\\{]");
	/** Checks if there is at least one space between the different line items */
	public static final Pattern errorCheckSpacingAfter = Pattern.compile("[>@\\]][^ <>@\\]\\|]+");
	/** Looks for the line index "n" or "+NUMBER", followed by a space */
	public static final Pattern errorCheckIndexFormat = Pattern.compile("(^n|(\\+[0-9]{1,2}))[ ]");
	/** Looks for the min/max pattern */
	public static final Pattern errorCheckMinMax = Pattern.compile("\\{\\d:[\\dM]\\}");
	
	
//	protected static String makeId(String tag, String xrefId, String valueId, String structureId) {
//		return tag + "#" + xrefId + "#" + valueId + "#" + structureId;
//	}
	
	
//	/**
//	 * A helper-method for Regex pattern matching
//	 * 
//	 * @param pattern
//	 * @param input
//	 * @param findInPattern
//	 * @param replaceWith
//	 * @return
//	 */
//	public static String replaceAllInPattern(Pattern pattern, String input, Pattern findInPattern, String replaceWith) {
//		String inPattern = StringUtil.getMatchingFirst(pattern, input);
//		return StringUtil.replaceAll(findInPattern, inPattern, replaceWith);
//	}
	
	
	/**
	 * Calculates the current age (until the date of the execution of this method) 
	 * of the person with the given birthDate
	 * 
	 * @param birthDate
	 * @return
	 */
	public static int getAge(Date birthDate) {
		return getAge(birthDate, new Date());
	}
	
	/**
	 * Calculates the age of the person between the given birthDate and the toDate
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public static int getAge(Date fromDate, Date toDate) {
		if (fromDate == null || toDate == null) {
			return 0;
		}
		
		DateMidnight bd = new DateMidnight(fromDate);
		DateTime now = new DateTime(toDate);
		Years age = Years.yearsBetween(bd, now);
		return age.getYears();
	}
	
	
	/**
	 * Checks it the date (and time) given with <code>isBeforeOrAfter</code> is before or 
	 * after the date (and time) given with <code>date</code>.
	 * 
	 * @param date
	 * @param isBeforeOrAfter
	 * @return {@link #BEFORE}, {@link #AFTER} or {@link #SAME}
	 */
	public static int isBeforeOrAfter(Date date, Date isBeforeOrAfter) {
		DateTime dtDate = new DateTime(date);
		DateTime dtIsBeforeOrAfter = new DateTime(isBeforeOrAfter);
		
		if (dtIsBeforeOrAfter.isBefore(dtDate)) {
			return BEFORE;
		} else if (dtIsBeforeOrAfter.isAfter(dtDate)) {
			return AFTER;
		} else {
			return SAME;
		}
	}
	
	
}
