package botWeather;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import parse.Call5dayWeather;
import parse.CurrentWeather;

import java.util.ArrayList;


public class BotWeather extends TelegramLongPollingBot {

    private static final String BOT_TOKEN = "1070861891:AAExQ9pGNC343T7MPDxiGysHW3J7un24Fyc";
    private static final String BOT_NAME = "TestWeatherBot";

    private static CurrentWeather weatherParserCurrent = new CurrentWeather();
    private static Call5dayWeather weatherParser5day = new Call5dayWeather();


    private long chat_id;
    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    String lastMessage;


    @Override
    public void onUpdateReceived(Update update) {
        update.getUpdateId();

        SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId());
        chat_id = update.getMessage().getChatId();

        String text = update.getMessage().getText();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            sendMessage.setText(getMessage(text));
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    public String getMessage(String message) {
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        if (message.equalsIgnoreCase("Привет") || message.equalsIgnoreCase("Меню")) {
            keyboard.clear();
            keyboardFirstRow.add("Погода сейчас");
            keyboardFirstRow.add("Прогноз на 5 дней");
            keyboard.add(keyboardFirstRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "Привет! Что хочешь узнать?:)";
        }

        if (message.equals("Погода сейчас") || message.equals("Прогноз на 5 дней")) {
            lastMessage = message;
            return "Ваш город?";
        }

        if(lastMessage != null && lastMessage.equals("Погода сейчас")){
            return weatherParserCurrent.getForecast(message);
        }

        if(lastMessage != null && lastMessage.equals("Прогноз на 5 дней")){
            return weatherParser5day.getForecast(message);
        }
        return message;
    }
}
