package parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import weatherCondition.WeatherCondition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Call5dayWeather extends ParseWeather {

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


    private static String Json(String city) throws Exception {
        String url = getCall5Day() + city + getApiKeys();
        URL urlObject = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", getUserAgent());

        int responseCode = connection.getResponseCode();
        if (responseCode == 404) {
            throw new IllegalArgumentException();
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inLine;
            StringBuffer response = new StringBuffer();
            while ((inLine = in.readLine()) != null) {
                response.append(inLine);
            }
            return response.toString();
        }
    }

    private static List<String> convertWeatherList(String data) throws Exception {
        List<String> weatherList = new ArrayList<>();

        JsonNode arrayNode = new ObjectMapper().readTree(data).get("list");
        if (arrayNode.isArray()) {
            for (final JsonNode objNode : arrayNode) {
                String forecastTime = objNode.get("dt_txt").toString();
                if (forecastTime.contains("09:00") || forecastTime.contains("12:00")
                        || forecastTime.contains("15:00") || forecastTime.contains("18:00")) {
                    weatherList.add(objNode.toString());
                }
            }
        }
        return weatherList;
    }

    private static String parserForecast(List<String> weatherList) throws Exception {
        final StringBuffer stringBuffer = new StringBuffer();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String line : weatherList) {
            {
                String dateTime;
                JsonNode mainNode;
                JsonNode weatherArrNode;
                try {
                    mainNode = objectMapper.readTree(line).get("main");
                    weatherArrNode = objectMapper.readTree(line).get("weather");
                    for (final JsonNode objNode : weatherArrNode) {
                        dateTime = objectMapper.readTree(line).get("dt_txt").toString();
                        stringBuffer.append(formattedForecast(dateTime, objNode.get("main").toString(), mainNode.get("temp").asDouble()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuffer.toString();
    }

    private static String formattedForecast(String dateTime, String description, double temperature) throws Exception {
        LocalDateTime forecastDateTime = LocalDateTime.parse(dateTime.replaceAll("\"", ""), getInDateTimeFormatter());
        String formattedDateTime = forecastDateTime.format(getOutDateTimeFormatter());
        String formattedTemperature;
        long roundedTemperature = Math.round(temperature);
        if (roundedTemperature > 0) {
            formattedTemperature = "+" + String.valueOf(Math.round(temperature));
        } else {
            formattedTemperature = String.valueOf(Math.round(temperature));
        }
        String formattedDescription = description.replaceAll("\"", "");
        String weatherIcon = WeatherCondition.weatherCondition.get(formattedDescription);
        return String.format("%s   %s\u00B0  %s  %s", formattedDateTime, formattedTemperature, weatherIcon, System.lineSeparator());
    }
}
