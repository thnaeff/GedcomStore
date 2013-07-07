/**
 * 
 */
package ch.thn.gedcom.store;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;

/**
 * Just a helper class which holds various static methods and fields
 * 
 * @author thomas
 *
 */
public class GedcomHelper {	
	
	public static final int MAX_AGE = 110;
	
	/** Matches multiple tags ([ABC|DEF|GHI|JKL]) */
	public static final Pattern multipleTags = Pattern.compile("([\\[\\|]?[A-Z]+\\|?)+\\]");
	/**  */
	public static final Pattern multipleTagsReplace = Pattern.compile("[\\[\\] ]");
	/** Matches [&lt;EVENT_DESCRIPTOR&gt;|&lt;NULL&gt;] etc, matches the WHOLE string */
	public static final Pattern multipleValuesWhole = Pattern.compile("^([\\[\\|]<[A-Z_]+>\\|?)+\\]$");
	/** Matches [&lt;EVENT_DESCRIPTOR&gt;|&lt;NULL&gt;] etc */
	public static final Pattern multipleValues = Pattern.compile("([\\[\\|]<[A-Z_]+>\\|?)+\\]");
	/**  */
	public static final Pattern multipleValuesReplace = Pattern.compile("[\\[\\] <>]");
	/** */
	public static final Pattern multipleXRefsWhole = Pattern.compile("^([\\[\\|]@?<[A-Z_:]+>@?\\|?)+\\]$");
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
	public static final Pattern xrefPatternWhole = Pattern.compile("^@<.*>@$");
	/**  */
	public static final Pattern xrefTagReplace = Pattern.compile("[@<|>@]");
	/**  */
	public static final Pattern minMaxPatternWhole = Pattern.compile("^\\{\\d:[\\d|M]\\*?\\}\\*?$");
	/**  */
	public static final Pattern minMaxReplace = Pattern.compile("[{|}|*]");
	/**  */
	public static final Pattern structurePatternWhole = Pattern.compile("^<<.*>>$");
	/**  */
	public static final Pattern structurePattern2 = Pattern.compile("<<.*>>");
	/**  */
	public static final Pattern structureReplace = Pattern.compile("[<<|>>]");
	/**  */
	public static final Pattern valuePatternWhole = Pattern.compile("^<.*>$");
	/**  */
	public static final Pattern valueReplace = Pattern.compile("[<|>]");
	/**  */
	public static final Pattern tagPatternWhole = Pattern.compile("^[A-Z]+[1-9]*$");	
	/**  */
	public static final Pattern commentPattern = Pattern.compile("/\\*.*\\*/");
	/**  */
	public static final Pattern spacesPattern = Pattern.compile("[ \t]+");
	/**  */
	public static final Pattern leadingTrailingPatternWhole = Pattern.compile("^[ \t\r\n]+|[ \t\r\n]+$");
	/**  */
	public static final Pattern structureNamePatternWhole = Pattern.compile("^[A-Z_]+:[ ]*=$");
	/**  */
	public static final Pattern idPattern = Pattern.compile("[A-Z]+([_:]*[A-Z])+");
	/**  */
	public static final Pattern levelPattern = Pattern.compile("^(n|\\+[0-9]) ");
	/**  */
	public static final Pattern subBlockDivider = Pattern.compile("^[\\[\\|\\]]");
	
	
	/** Checks if there is at least one space between the different line items */
	public static final Pattern errorCheckSpacingBefore = Pattern.compile("[^ <>@\\[\\|]+[<@\\[\\{]");
	/** Checks if there is at least one space between the different line items */
	public static final Pattern errorCheckSpacingAfter = Pattern.compile("[>@\\]][^ <>@\\]\\|]+");
	/** Looks for the line index "n" or "+NUMBER", followed by a space */
	public static final Pattern errorCheckIndexFormat = Pattern.compile("^n|(\\+[0-9]{1,2})[ ]");
	/** Looks for the min/max pattern */
	public static final Pattern errorCheckMinMax = Pattern.compile("\\{\\d:[\\dM]\\}");
	
	
	public static String makeId(String tag, String xrefId, String valueId, String structureId) {
		return tag + "#" + xrefId + "#" + valueId + "#" + structureId;
	}
	
	
	
	/**
	 * A helper-method for Regex pattern matching
	 * 
	 * @param pattern
	 * @param input
	 * @return
	 */
	public static boolean matches(Pattern pattern, String input) {
		Matcher m = pattern.matcher(input);
		return m.find();
	}
	
	/**
	 * A helper-method for Regex pattern matching
	 * 
	 * @param pattern
	 * @param input
	 * @return
	 */
	public static String removeAll(Pattern pattern, String input) {
		return replaceAll(pattern, input, "");
	}
	
	/**
	 * A helper-method for Regex pattern matching
	 * 
	 * @param pattern
	 * @param input
	 * @param replaceWith
	 * @return
	 */
	public static String replaceAll(Pattern pattern, String input, String replaceWith) {
		Matcher m = pattern.matcher(input);
		if (m.find()) {
			return m.replaceAll(replaceWith);
		} else {
			return input;
		}
	}
	
	/**
	 * A helper-method for Regex pattern matching
	 * 
	 * @param pattern
	 * @param input
	 * @return
	 */
	public static String getPattern(Pattern pattern, String input) {
		Matcher m = pattern.matcher(input);
		if (m.find()) {
			return m.group();
		} else {
			return input;
		}
	}
	
	/**
	 * A helper-method for Regex pattern matching
	 * 
	 * @param pattern
	 * @param input
	 * @param findInPattern
	 * @param replaceWith
	 * @return
	 */
	public static String replaceAllInPattern(Pattern pattern, String input, Pattern findInPattern, String replaceWith) {
		String inPattern = getPattern(pattern, input);
		return replaceAll(findInPattern, inPattern, replaceWith);
	}
	
	
	public static int getAge(Date birthDate) {
		if (birthDate == null) {
			return 0;
		}
		
		DateMidnight bd = new DateMidnight(birthDate);
		DateTime now = new DateTime();
		Years age = Years.yearsBetween(bd, now);
		return age.getYears();
	}
	
	
}
