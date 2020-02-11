package weatherCondition;


import java.util.HashMap;
import java.util.Map;

public class WeatherCondition {
    public final static Map<String, String> weatherCondition = new HashMap<>();

    static {
        weatherCondition.put("Clear", "\u2600");
        weatherCondition.put("Rain", "\u2614");
        weatherCondition.put("Snow", "\u2744");
        weatherCondition.put("Clouds", "\u26C5");
    }
}
