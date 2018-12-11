package edu.mcw.rgd.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {
	static public HashMap<String, String> MONTH_TABLE = null;

	static public SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat(
			"MM/dd/yyyy");

	static public SimpleDateFormat MY_SQL_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");

	public static void initMonths() {
		if (MONTH_TABLE == null) {
			MONTH_TABLE = new HashMap<String, String>();
			MONTH_TABLE.put("1", "01");
			MONTH_TABLE.put("2", "02");
			MONTH_TABLE.put("3", "03");
			MONTH_TABLE.put("4", "04");
			MONTH_TABLE.put("5", "05");
			MONTH_TABLE.put("6", "06");
			MONTH_TABLE.put("7", "07");
			MONTH_TABLE.put("8", "08");
			MONTH_TABLE.put("9", "09");
			MONTH_TABLE.put("10", "10");
			MONTH_TABLE.put("11", "11");
			MONTH_TABLE.put("12", "12");

			MONTH_TABLE.put("01", "01");
			MONTH_TABLE.put("02", "02");
			MONTH_TABLE.put("03", "03");
			MONTH_TABLE.put("04", "04");
			MONTH_TABLE.put("05", "05");
			MONTH_TABLE.put("06", "06");
			MONTH_TABLE.put("07", "07");
			MONTH_TABLE.put("08", "08");
			MONTH_TABLE.put("09", "09");
			MONTH_TABLE.put("10", "10");
			MONTH_TABLE.put("11", "11");
			MONTH_TABLE.put("12", "12");

			MONTH_TABLE.put(("Jan").toLowerCase(), "01");
			MONTH_TABLE.put(("Feb").toLowerCase(), "02");
			MONTH_TABLE.put(("Mar").toLowerCase(), "03");
			MONTH_TABLE.put(("Apr").toLowerCase(), "04");
			MONTH_TABLE.put(("May").toLowerCase(), "05");
			MONTH_TABLE.put(("Jun").toLowerCase(), "06");
			MONTH_TABLE.put(("Jul").toLowerCase(), "07");
			MONTH_TABLE.put(("Aug").toLowerCase(), "08");
			MONTH_TABLE.put(("Sep").toLowerCase(), "09");
			MONTH_TABLE.put(("Oct").toLowerCase(), "10");
			MONTH_TABLE.put(("Nov").toLowerCase(), "11");
			MONTH_TABLE.put(("Dec").toLowerCase(), "12");

			MONTH_TABLE.put(("January").toLowerCase(), "01");
			MONTH_TABLE.put(("February").toLowerCase(), "02");
			MONTH_TABLE.put(("March").toLowerCase(), "03");
			MONTH_TABLE.put(("April").toLowerCase(), "04");
			MONTH_TABLE.put(("May").toLowerCase(), "05");
			MONTH_TABLE.put(("June").toLowerCase(), "06");
			MONTH_TABLE.put(("July").toLowerCase(), "07");
			MONTH_TABLE.put(("August").toLowerCase(), "08");
			MONTH_TABLE.put(("September").toLowerCase(), "09");
			MONTH_TABLE.put(("October").toLowerCase(), "10");
			MONTH_TABLE.put(("November").toLowerCase(), "11");
			MONTH_TABLE.put(("December").toLowerCase(), "12");

			MONTH_TABLE.put(("Spring").toLowerCase(), "03");
			MONTH_TABLE.put(("Summer").toLowerCase(), "06");
			MONTH_TABLE.put(("Fall").toLowerCase(), "09");
			MONTH_TABLE.put(("Autumn").toLowerCase(), "09");
			MONTH_TABLE.put(("Winter").toLowerCase(), "12");
		}
	}
	
	public static String getEndData(String dataRange) {
		initMonths();
		String year, month, day;
		Pattern p = Pattern.compile("(\\d{4}) (.+)-(.+)");
		Matcher m = p.matcher(dataRange.toLowerCase());
		if (m.find()) {
			year = m.group(1).trim();
			month = m.group(3).trim();
			if (month.contains(" ")) {
				String mon = month.substring(0, month.indexOf(' ')).trim();
				day = month.substring(month.lastIndexOf(' ') + 1, month.length()).trim();
				if (MONTH_TABLE.get(mon) == null) {
					if (MONTH_TABLE.get(day) == null) {
						year = month.substring(month.indexOf(' '), month.lastIndexOf(' ')).trim();
						return mon + "/" + MONTH_TABLE.get(year)
								+ "/" + day;   // returns if date is: yyyy mm dd-yyyy mm dd
					} else return mon + "/" + MONTH_TABLE.get(day)
							+ "/01";   // returns if date is: yyyy mm-yyyy mm
				} else {
					if (day.length()>2) {
						return day + "/" + MONTH_TABLE.get(mon)
								+ "/01";   // returns if date is: yyyy mm dd-mm yyyy
					} else
						return year.trim() + "/" + MONTH_TABLE.get(mon.trim())
							+ "/" + day.trim();   // returns if date is: yyyy mm dd-mm dd
				}
			} else if (MONTH_TABLE.get(month.trim()) == null) {
				day = month;
				month = m.group(2).trim();
				String mon = month.substring(0, month.indexOf(' ')).trim();
				return year + "/" + MONTH_TABLE.get(mon)
						+ "/" + day; // returns if date is: yyyy mm dd-dd
			} else {
				return year + "/" + MONTH_TABLE.get(month)
						+ "/01"; // returns if date is: yyyy mm-mm
			}
		}
		return null;
	}
	
	public static String getDateString(String year, String month, String day, String season) {
		initMonths();
		if (season != null) {
			return year + "/01/01";
		} else if (month != null && day != null) {
					return year + "/" + MONTH_TABLE.get(month.toLowerCase()) + "/" + day;
		} else	if (month != null && day == null) {
			return year + "/" + MONTH_TABLE.get(month.toLowerCase()) + "/01";
		} else {
				return year + "/01/01";
		}
	}
	
	public static String getJournalDate(String year, String month, String day, String season) {
		initMonths();
		if (season != null) {
			return year + " " + season;
		} else if (month != null && day != null) {
			return year + " " + month + " " + day;
		} else	if (month != null && day == null) {
			return year + " " + month;
		} else {
			return year;
		}
	}
	
	public static String getMySQLDate(String date) {
		try {
			return MY_SQL_DATE_FORMAT.format(LOG_DATE_FORMAT.parse(date)).toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
