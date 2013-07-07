/**
 * 
 */
package ch.thn.gedcom.store;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

/**
 * This class contains some static methods which help with formatting data to be used 
 * in gedcom files
 * 
 * @author thomas
 *
 */
public class GedcomFormatter {
	
	
	
	
	private static final SimpleDateFormat gedcomDate = new SimpleDateFormat("dd MMM yyyy");
	private static final SimpleDateFormat gedcomTime = new SimpleDateFormat("HH:mm:ss");
	
	private static final Calendar calendar = new GregorianCalendar();
	
	static {
		gedcomDate.setCalendar(calendar);
		gedcomTime.setCalendar(calendar);
	}
	
	
	/**
	 * Returns todays date in the format needed for gedcom files.
	 * 
	 * @return
	 */
	public static String getDateNow() {
		calendar.setTime(new Date());
		return gedcomDate.format(calendar.getTime()).toUpperCase();
	}
	
	/**
	 * Returns todays time in the format needed for gedcom files.
	 * 
	 * @return
	 */
	public static String getTimeNow() {
		calendar.setTime(new Date());
		return gedcomTime.format(calendar.getTime()).toUpperCase();
	}
	
	/**
	 * Returns the given date in the format needed for gedcom files.
	 * 
	 * @param d
	 * @return
	 */
	public static String getDate(Date d) {
		if (d == null) {
			return null;
		}
		
		calendar.setTime(d);
		return gedcomDate.format(calendar.getTime()).toUpperCase();
	}
	
	/**
	 * Returns the given time in the format needed for gedcom files.
	 * 
	 * @param d
	 * @return
	 */
	public static String getTime(Date d) {
		if (d == null) {
			return null;
		}
		
		calendar.setTime(d);
		return gedcomTime.format(calendar.getTime()).toUpperCase();
	}
	
		
	
	public static String getDate(String dateString, String... dateFormatPatterns) {
		return getDate(extractDate(dateString, dateFormatPatterns));
	}
	
	public static String getTime(String dateString, String... dateFormatPatterns) {
		return getTime(extractDate(dateString, dateFormatPatterns));
	}
	
	public static Date getDate(String dateString) {
		if (dateString == null) {
			return null;
		}
		
		return extractDate(dateString, "dd MMM yyyy", "MMM yyyy", "yyyy");
		
	}
	
	public static Date getTime(String timeString) {
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
	 * 
	 * @param dateString
	 * @param dateFormatPatterns One or more date formats as used in {@link SimpleDateFormat}
	 * @return
	 */
	public static Date extractDate(String dateString, String... dateFormatPatterns) {
		if (dateString == null) {
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
	
	
	public static String printIfNotNull(String prefix, String valueToPrintIfNotNull, String postfix) {
		if (valueToPrintIfNotNull == null) {
			return "";
		}
		
		return prefix + valueToPrintIfNotNull + postfix;
	}
	
	
	/**
	 * Creates the necessary insets to show a structured form of this block
	 * 
	 * @param inset
	 * @return
	 */
	protected static StringBuffer makeInset(int inset) {
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < inset; i++) {
			sb.append("  ");
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
	protected static StringBuffer makeRightAlign(int spaceFromLeft, int preStringLength) {
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
	protected static StringBuffer makeOrList(LinkedList<String> list, String itemPrefix, String itemSuffix) {
		StringBuffer sb = new StringBuffer();
		
		if (list.size() > 1) {
			sb.append("[");
		}
		
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) {
				sb.append("|");
			}
			
			String item = list.get(i);
			
			if (item.equals("NULL")) {
				sb.append("<");
			} else {
				sb.append(itemPrefix);
			}
			
			sb.append(list.get(i));
			
			if (item.equals("NULL")) {
				sb.append(">");
			} else {
				sb.append(itemSuffix);
			}
		}
		
		if (list.size() > 1) {
			sb.append("]");
		}
		
		return sb;
	}
	
	

}
