package inc.deszo.fuzzywinner.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

public class DateUtils {

  public static String STANDARD_FORMAT = "dd/MM/yyyy";

  public static String getTodayDate(String format) {

    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

    return now.format(formatter);
  }

  public static String getDateFromISODate(String isoDate, String format) {

    DateTimeFormatter isoDateParser = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .optionalStart()
        .appendLiteral('T')
        .append(DateTimeFormatter.ISO_TIME)
        .toFormatter();

    LocalDateTime formatDateTime = LocalDateTime.parse(isoDate, isoDateParser);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

    return formatDateTime.format(formatter);
  }

  public static String addToDate(String date, String format, String tenor) {

    String calDate = "";

    int num = Integer.valueOf(tenor.substring(0, tenor.length() - 1));
    String units = tenor.substring(tenor.length() - 1, tenor.length());

    switch (units) {
      case "D":
        calDate = addDayToDate(date, format, num);
        break;
      case "W":
        calDate = addWeekToDate(date, format, num);
        break;
      case "M":
        calDate = addMonthToDate(date, format, num);
        break;
      case "Y":
        calDate = addYearToDate(date, format, num);
        break;
      default:
        break;
    }

    return calDate;
  }

  public static String addYearToDate(String date, String format, int year) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    LocalDate localDate = LocalDate.parse(date, formatter);

    return localDate.plusYears(year).format(formatter);
  }

  public static String addMonthToDate(String date, String format, int month) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    LocalDate localDate = LocalDate.parse(date, formatter);

    return localDate.plusMonths(month).format(formatter);
  }

  public static String addWeekToDate(String date, String format, int week) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    LocalDate localDate = LocalDate.parse(date, formatter);

    return localDate.plusWeeks(week).format(formatter);
  }

  public static String addDayToDate(String date, String format, int day) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    LocalDate localDate = LocalDate.parse(date, formatter);

    return localDate.plusDays(day).format(formatter);
  }

  public static String getEndDateForHistoricalPrices(String startDate, String format) {

    String todayDate = getTodayDate(format);
    String endDate = DateUtils.addYearToDate(startDate, format, 1);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

    LocalDate localTodayDate = LocalDate.parse(todayDate, formatter);
    LocalDate localEndDate = LocalDate.parse(endDate, formatter);

    if (localTodayDate.compareTo(localEndDate) >= 0) {
      return endDate;
    } else {
      return todayDate;
    }
  }

  public static boolean isLessThanOrEqualToDate(String firstDate, String secondDate, String format) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

    LocalDate localFirstDate = LocalDate.parse(firstDate, formatter);
    LocalDate localSecondDate = LocalDate.parse(secondDate, formatter);

    return localFirstDate.compareTo(localSecondDate) <= 0;
  }

  public static String getDatefromLongFormat(String longDate, String format) {

    DateTimeFormatter longFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, y");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

    LocalDate localDate = LocalDate.parse(longDate, longFormatter);

    return localDate.format(formatter);
  }

  public static String getDatefromFormat(String date, String oldFormat, String newFormat) {

    DateTimeFormatter oldFormatter = DateTimeFormatter.ofPattern(oldFormat);
    DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern(newFormat);

    LocalDate localDate = LocalDate.parse(date, oldFormatter);

    return localDate.format(newFormatter);
  }

  public static LocalDate getLocalDate(String date, String format) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

    return LocalDate.parse(date, formatter);
  }

  public static Date getDate(String date, String format) throws ParseException {

    SimpleDateFormat formatter = new SimpleDateFormat(format);

    return formatter.parse(date);
  }

  public static String getDate(LocalDate date, String format) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

    return date.format(formatter);
  }

  public static String getDate(Date date, String format) {

    SimpleDateFormat formatter = new SimpleDateFormat(format);

    return formatter.format(date);
  }
}
