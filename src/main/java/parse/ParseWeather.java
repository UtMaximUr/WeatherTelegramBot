package parse;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ParseWeather {
    private final static String CALL_5_DAY = "https://api.openweathermap.org/data/2.5/forecast?q=";
    private final static String API_CURRENT = "https://api.openweathermap.org/data/2.5/weather?q=";
    private final static String API_KEYS = "&units=metric&APPID=24dee87f4aa1d19ed5f6e41b617144d0";
    private static String userAgent = "Chrome/4.0.249.0 Safari/532.5";
    private static DateTimeFormatter inDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter outDateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM HH:mm", Locale.US);

    public static String getApiCurrent() {
        return API_CURRENT;
    }

    public static String getCall5Day() {
        return CALL_5_DAY;
    }

    public static String getApiKeys() {
        return API_KEYS;
    }

    public static String getUserAgent() {
        return userAgent;
    }

    public static DateTimeFormatter getInDateTimeFormatter() {
        return inDateTimeFormatter;
    }

    public static DateTimeFormatter getOutDateTimeFormatter() {
        return outDateTimeFormatter;
    }
}
