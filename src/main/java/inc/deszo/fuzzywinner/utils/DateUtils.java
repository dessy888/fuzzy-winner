package inc.deszo.fuzzywinner.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by deszo on 08/06/2017.
 */
public class DateUtils {

    public static String STANDARD_FORMAT = "dd/MM/yyyy";

    public static String getTodayDate(String format) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        return now.format(formatter);
    }
}
