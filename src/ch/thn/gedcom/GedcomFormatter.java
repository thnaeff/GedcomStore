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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This class contains some static methods which help with formatting data to be used 
 * in gedcom files
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomFormatter {
	
	public static final String INSET = "  ";
	
	private static final SimpleDateFormat gedcomDateAll = new SimpleDateFormat("dd MMM yyyy");
	private static final SimpleDateFormat gedcomDateYearMonth = new SimpleDateFormat("MMM yyyy");
	private static final SimpleDateFormat gedcomDateYear = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat gedcomDateTimeAll = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
	private static final SimpleDateFormat gedcomDateTimeYearMonth = new SimpleDateFormat("MMM yyyy HH:mm:ss");
	private static final SimpleDateFormat gedcomDateTimeYear = new SimpleDateFormat("yyyy HH:mm:ss");
	private static final SimpleDateFormat gedcomTime = new SimpleDateFormat("HH:mm:ss");
	
	private static final Calendar calendar = new GregorianCalendar();
	
	static {
		gedcomDateAll.setCalendar(calendar);
		gedcomDateYearMonth.setCalendar(calendar);
		gedcomDateYear.setCalendar(calendar);
		gedcomDateTimeAll.setCalendar(calendar);
		gedcomDateTimeYearMonth.setCalendar(calendar);
		gedcomDateTimeYear.setCalendar(calendar);
		gedcomTime.setCalendar(calendar);
	}
	
	
	/**
	 * Returns todays date in the format needed for gedcom files.
	 * 
	 * @return
	 */
	public static String getGedcomDateNow() {
		calendar.setTime(new Date());
		return gedcomDateAll.format(calendar.getTime()).toUpperCase();
	}
	
	/**
	 * Returns todays time in the format needed for gedcom files.
	 * 
	 * @return
	 */
	public static String getGedcomTimeNow() {
		calendar.setTime(new Date());
		return gedcomTime.format(calendar.getTime()).toUpperCase();
	}
	
	/**
	 * Returns the given date in the format needed for gedcom files. It has to 
	 * be specified if the day and the month should be included in the returned 
	 * date string.
	 * 
	 * @param d
	 * @param month Include the month?
	 * @param day Include the day (only possible if the month is included)?
	 * @return The date as string, or <code>null</code> if <code>d=null</code>
	 */
	public static String getGedcomDate(Date d, boolean month, boolean day) {
		if (d == null) {
			return null;
		}
		
		calendar.setTime(d);
		
		if (month) {
			if (!day) {
				//Year and month. With uppercase month
				return gedcomDateYearMonth.format(calendar.getTime()).toUpperCase();
			} else {
				//Year, month and day. With uppercase month
				return gedcomDateAll.format(calendar.getTime()).toUpperCase();
			}
		} else {
			//Year only
			//Year, month and day
			return gedcomDateYear.format(calendar.getTime());
		}
	}
	
	/**
	 * Returns the given date and time in the format needed for gedcom files. It has to 
	 * be specified if the day and the month should be included in the returned 
	 * date-time string.
	 * 
	 * @param d
	 * @param month Include the month?
	 * @param day Include the day (only possible if the month is included)?
	 * @return The date and time as string, or <code>null</code> if <code>d=null</code>
	 */
	public static String getGedcomDateTime(Date d, boolean month, boolean day) {
		if (d == null) {
			return null;
		}
		
		calendar.setTime(d);
		
		if (month) {
			if (!day) {
				//Year and month. With uppercase month
				return gedcomDateTimeYearMonth.format(calendar.getTime()).toUpperCase();
			} else {
				//Year, month and day. With uppercase month
				return gedcomDateTimeAll.format(calendar.getTime()).toUpperCase();
			}
		} else {
			//Year only
			//Year, month and day
			return gedcomDateTimeYear.format(calendar.getTime());
		}
	}
	
	/**
	 * Returns the given time in the format needed for gedcom files.
	 * 
	 * @param d
	 * @return The time as string, or <code>null</code> if <code>d=null</code>
	 */
	public static String getGedcomTime(Date d) {
		if (d == null) {
			return null;
		}
		
		calendar.setTime(d);
		return gedcomTime.format(calendar.getTime()).toUpperCase();
	}
	
		
	/**
	 * Converts the given date string to a date string needed for GEDCOM files. 
	 * The date patterns given with dateFormatPatterns are used in the given order 
	 * to parse the date string.
	 * 
	 * @param dateString
	 * @param month Include the month?
	 * @param day Include the day (only possible if the month is included)?
	 * @param dateFormatPatterns
	 * @return
	 */
	public static String getGedcomDate(String dateString, boolean month, boolean day, 
			String... dateFormatPatterns) {
		return getGedcomDate(extractDate(dateString, dateFormatPatterns), month, day);
	}
	
	/**
	 * Converts the given time string to a time string needed for GEDCOM files. 
	 * The date patterns given with dateFormatPatterns are used in the given order 
	 * to parse the time string.
	 * 
	 * @param timeString
	 * @param dateFormatPatterns
	 * @return
	 */
	public static String getGedcomTime(String timeString, String... dateFormatPatterns) {
		return getGedcomTime(extractDate(timeString, dateFormatPatterns));
	}
	
	/**
	 * Converts the given date and time string to a date and time string needed for GEDCOM files. 
	 * The date-time patterns given with dateTimeFormatPatterns are used in the given order 
	 * to parse the date-time string.
	 * 
	 * @param dateTimeString
	 * @param month Include the month?
	 * @param day Include the day (only possible if the month is included)?
	 * @param dateTimeFormatPatterns
	 * @return
	 */
	public static String getGedcomDateTime(String dateTimeString, boolean month, boolean day, 
			String... dateTimeFormatPatterns) {
		return getGedcomDateTime(extractDate(dateTimeString, dateTimeFormatPatterns), month, day);
	}
	
	/**
	 * Returns the Date object from the given gedcom dateString. Since gedcom dates could 
	 * be given with only the year or year and month, it tries the 
	 * following patterns in the given order:<br>
	 * 1. "dd MMM yyyy"<br>
	 * 2. "MMM yyyy"<br>
	 * 3. "yyyy"
	 * <br>
	 * Note: The missing pieces are set to 01 (first day and first month). Thus, the 
	 * input date string "FEB 1995" would produce a date object "01 02 1995". This 
	 * is just how {@link Date} works.
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date getDateFromGedcom(String dateString) {
		if (dateString == null) {
			return null;
		}
		
		return extractDate(dateString, "dd MMM yyyy", "MMM yyyy", "yyyy");
		
	}
	
	/**
	 * Converts the given gedcom time (HH:mm:ss) string to a date object
	 * 
	 * @param timeString
	 * @return
	 */
	public static Date getTimeFromGedcom(String timeString) {
		if (timeString == null) {
			return null;
		}
		
		try {
			return gedcomTime.parse(timeString);
		} catch (ParseException e) {
			return null;
		}
		
	}
	
	/**
	 * Returns the Date object from the given dateTimeString. Since gedcom dates could 
	 * be given with only the year or year and month, it tries the 
	 * following patterns in the given order for the date part:<br>
	 * 1. "dd MMM yyyy"<br>
	 * 2. "MMM yyyy"<br>
	 * 3. "yyyy"<br>
	 * The time part is parsed with the format "HH:mm:ss"<br>
	 * <br>
	 * Note: The missing pieces are set to 01 (first day and first month). Thus, the 
	 * input date string "1995 12:02:20" would produce a date object "01 01 1995 12:02:20". This 
	 * is just how {@link Date} works.
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date getDateTimeFromGedcom(String dateTimeString) {
		return extractDate(dateTimeString, "dd MMM yyyy HH:mm:ss", "MMM yyyy HH:mm:ss", "yyyy HH:mm:ss");
	}
	
	/**
	 * Tries to parse the given date string with the given dateFormatPatterns. The 
	 * order of trying is exactly the order of the patterns given with dateFormatPatterns. 
	 * The first pattern which works to parse the date string returns its {@link Date} object. 
	 * If none of the patterns work, null is returned. The date parsing starts 
	 * at the beginning of the given date string and parses until it fails. It might 
	 * not use the whole date string, thus, longer format patterns should appear 
	 * first.
	 * 
	 * @param dateString
	 * @param dateFormatPatterns One or more date formats as used in {@link SimpleDateFormat}
	 * @return
	 */
	public static Date extractDate(String dateString, String... dateFormatPatterns) {
		if (dateString == null || dateString.length() == 0) {
			return null;
		}
		
		SimpleDateFormat dateFormat = null;
		
		for (int i = 0; i < dateFormatPatterns.length; i++) {
			
			dateFormat = new SimpleDateFormat(dateFormatPatterns[i]);
			
			try {
				Date date = dateFormat.parse(dateString);
				
				//If parsing the date did not fail, return the date
				return date;
			} catch (ParseException e) {
				continue;
			}
		}
		
		//Parsing the date with any given pattern failed
		return null;
	}
	
	/**
	 * Prints the given value (with prefix and postfix if given), but only if the 
	 * given value is not null. If the given value is null, an empty string is returned.
	 * 
	 * @param prefix
	 * @param valueToPrintIfNotNull
	 * @param postfix
	 * @return
	 */
	public static String printIfNotNull(String prefix, String valueToPrintIfNotNull, String postfix) {
		if (valueToPrintIfNotNull == null) {
			return "";
		}
		
		return prefix + valueToPrintIfNotNull + postfix;
	}
	
	
	/**
	 * Creates a string with the number of spaces defined with the parameter insets
	 * 
	 * @param inset
	 * @return
	 */
	public static StringBuffer makeInset(int inset) {
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < inset; i++) {
			sb.append(INSET);
		}
		
		return sb;
	}
	
	/**
	 * Returns the required number of spaces to align text further to the right 
	 * 
	 * @param spaceFromLeft How many spaces from the left page border are needed?
	 * @param preStringLength The current length of the string on this line
	 * @return
	 */
	public static StringBuffer makeRightAlign(int spaceFromLeft, int preStringLength) {
		StringBuffer sb = new StringBuffer();
				
		int spaceNeeded = spaceFromLeft - preStringLength;
		
		if (spaceNeeded <= 0) {
			spaceNeeded = 3;
		}
		
		for (int i = spaceNeeded; i > 0; i--) {
			if (i % 2 == 0) {
				sb.append("°");
			} else {
				sb.append(" ");
			}
		}
		
		return sb;
	}
	
	
	/**
	 * Makes a or-list out of the given list, adding the given pre- and suffixes 
	 * to each or-item.<br>
	 * A generated list could look like:<br>
	 * [ITEM1|ITEM2|ITEM3]<br>
	 * or with prefix &lt; and suffix &gt;:<br>
	 * [&lt;ITEM1&gt;|&lt;ITEM2&gt;|&lt;ITEM3&gt;]
	 * 
	 * @param list
	 * @param itemPrefix 
	 * @param itemSuffix
	 * @return
	 */
	public static StringBuilder makeOrList(Collection<String> list, String itemPrefix, String itemSuffix) {
		return makeStringList(list, "|", itemPrefix, itemSuffix, true, null, true);
	}
	
	/**
	 * 
	 * 
	 * @param list
	 * @param separator
	 * @param itemPrefix 
	 * @param itemSuffix
	 * @param includeNullAndEmpty
	 * @param replaceNullWith
	 * @param addBrackets
	 * @return
	 */
	public static StringBuilder makeStringList(Collection<String> list, String separator, 
			String itemPrefix, String itemSuffix, boolean includeNullAndEmpty, 
			String replaceNullWith, boolean addBrackets) {
		StringBuilder sb = new StringBuilder();
		
		
		for (String item : list) {
			if (item == null) {
				item = replaceNullWith;
			}
			
			if (!includeNullAndEmpty && (item == null || item.length() == 0)) {
				continue;
			}
			
			if (sb.length() > 0 && separator != null) {
				sb.append(separator);
			}
			
			if (item == null || item.equals("NULL")) {
				sb.append("<");
			} else if (itemPrefix != null) {
				sb.append(itemPrefix);
			}
			
			sb.append(item);
			
			if (item == null || item.equals("NULL")) {
				sb.append(">");
			} else if (itemSuffix != null) {
				sb.append(itemSuffix);
			}
		}
		
		if (addBrackets && list.size() > 1) {
			sb.insert(0, "[");
		}
		
		if (addBrackets && list.size() > 1) {
			sb.append("]");
		}
		
		return sb;
	}

}
