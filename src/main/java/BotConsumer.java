import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class BotConsumer implements LongPollingSingleThreadUpdateConsumer {
    private static final String MQTT_BROKER = "tcp://localhost:1883";
    private static final String TELEGRAM_CHAT_ID = "-1002473801990";
    private final TelegramClient telegramClient;
    private MqttHandler mqttHandler;

    public BotConsumer(String TELEGRAM_TOKEN) throws MqttException {
        telegramClient = new OkHttpTelegramClient(TELEGRAM_TOKEN);
        mqttHandler = new MqttHandler(MQTT_BROKER, MqttClient.generateClientId());
        mqttHandler.subscribe("homer/#", this::handleMessage); // Suscribirse a todos los topics de la casa
    }

    private void handleMessage(String topic, MqttMessage message) throws TelegramApiException {
        String messageContent = new String(message.getPayload());
        sendNotification(messageContent);
    }

    @Override
    public void consume(Update update) {
        // Verifica si la actualización tiene un mensaje y el mensaje tiene texto
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            System.out.println("Comando recibido: " + messageText);
            MqttMessage message = new MqttMessage();
            message.setPayload(messageText.getBytes());
            int indexSlash = messageText.indexOf('/');

            // Obtener el índice del '@'
            int indexAt = messageText.indexOf('@');
            String topic;

            String res = messageText.substring(indexSlash + 1, indexAt);

            topic = "home/entrance";

            switch (res) {
                case "status":
                    topic = "home";
                    break;
                case "lamp1on":
                case "lamp1off":
                    topic = "home/room1/lamp";
                    break;
                case "lamp2on":
                case "lamp2off":
                    topic = "home/room2/lamp";
                    break;
                case "kitchenlampon":
                case "kitchenlampoff":
                    topic = "home/kitchen/lamp";
                    break;
                case "livnroomlampon":
                case "livnroomlampoff":
                    topic = "home/livingroom/lamp";
                    break;
                case "foyerlampon":
                case "foyerlampoff":
                    topic = "home/foyer/lamp";
                    break;
                case "bathlampon":
                case "bathlampoff":
                    topic = "home/bathroom/lamp";
                    break;
                case "bathhumiditysensor":
                    topic = "home/bathroom/humidity";
                    break;
                case "livtempsensor":
                    topic = "home/livingroom/temperature";
                    break;
                case "kitsmokesensor":
                    topic = "home/kitchen/smoke";
                    break;
                case "foyerentrance":
                    topic = "home/foyer/entrance";
                    break;
                default:
                    System.out.println("No contemplada");
            }

            try {
                mqttHandler.publish(topic, message);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void sendNotification(String messageContent) {
        try {
            SendMessage sendMessage = new SendMessage(TELEGRAM_CHAT_ID, messageContent);
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
