import org.eclipse.paho.client.mqttv3.*;

public class MqttHandler {
    private MqttClient mqttClient;

    public MqttHandler(String brokerUrl, String clientId) throws MqttException {
        mqttClient = new MqttClient(brokerUrl, clientId);
        mqttClient.connect();
    }

    public void subscribe(String topic, IMqttMessageListener messageListener) throws MqttException {
        mqttClient.subscribe(topic, (t, msg) -> messageListener.messageArrived(t, msg));
    }

    public void publish(String topic, MqttMessage message) throws MqttException {
        mqttClient.publish(topic, message);
    }

    public void disconnect() throws MqttException {
        mqttClient.disconnect();
    }
}
