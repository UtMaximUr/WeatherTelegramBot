package parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import weatherCondition.WeatherCondition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CurrentWeather extends ParseWeather{

    public String getForecast(String city) {

        String result;
        try {
            String json = Json(city);
            List<String> linesOfForecast = convertWeatherList(json);
            result = String.format("%s:%s%s", city, System.lineSeparator(), parserForecast(linesOfForecast));
        } catch (IllegalArgumentException e) {
            return String.format("Такой не найден:( \"%s\". Попробуем еще? Например: \"Москва\" или \"Moscow\"", city);
        } catch (Exception e) {
            e.printStackTrace();
            return "Упс:( Сервис не доступен. Попробуйте позже.";
        }
        return result;
    }

    private static List<String> convertWeatherList(String data) {
        List<String> weatherList = new ArrayList<>();
        weatherList.add(data);
        return weatherList;
    }


    private static String Json(String city) throws Exception {

        String url = getApiCurrent() + city + getApiKeys();
        URL urlObject = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", getUserAgent());

        int resultCode = connection.getResponseCode();
        if (resultCode == 404) {
            throw new IllegalArgumentException();
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inLine;
            StringBuffer result = new StringBuffer();
            while ((inLine = in.readLine()) != null) {
                result.append(inLine);
            }
            return result.toString();
        }
    }


    private static String parserForecast(List<String> weatherList) throws Exception {

        final StringBuffer stringBuffer = new StringBuffer();
        ObjectMapper objectMapper = new ObjectMapper();
        for (String line : weatherList) {
            JsonNode mainNode;
            JsonNode weatherArrNode;
            JsonNode wind;
            JsonNode pressure;
            try {
                mainNode = objectMapper.readTree(line).get("main");
                weatherArrNode = objectMapper.readTree(line).get("weather");
                wind = objectMapper.readTree(line).get("wind");
                pressure = objectMapper.readTree(line).get("main");

                for (final JsonNode objNode : weatherArrNode) {
                    stringBuffer.append(formattedForecast(objNode.get("main").toString(),
                            mainNode.get("temp").asDouble(),
                            wind.get("speed").asDouble(),
                            pressure.get("pressure").asDouble()));
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuffer.toString();
    }

    private static String formattedForecast(String description, double temperature, double wind, double pressure) throws Exception {

        String formattedTemperature;
        long roundedTemperature = Math.round(temperature);
        if (roundedTemperature > 0) {
            formattedTemperature = "+" + String.valueOf(Math.round(temperature));
        } else {
            formattedTemperature = String.valueOf(Math.round(temperature));
        }
        String formattedWind = String.valueOf(Math.round(wind));
        String formattedPressure = String.valueOf(Math.round(pressure * 0.750));
        String formattedDescription = description.replaceAll("\"", "");

        String weatherIcon = WeatherCondition.weatherCondition.get(formattedDescription);

        return String.format("%s\u00B0  %s  %s м/с  %s мм", formattedTemperature, weatherIcon, formattedWind, formattedPressure, System.lineSeparator());
    }
}
