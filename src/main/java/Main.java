import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {
        String botToken = "7691939249:AAH1pBWFgUiHGPlnuCdnRRkDfsF4c7jIKxs";
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new BotConsumer(botToken));
            System.out.println("Automated House bot");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}