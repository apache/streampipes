package org.streampipes.pe.sinks.standalone.samples.rabbitmq;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by riemer on 29.03.2017.
 */
public enum RabbitMqPublisher {

  INSTANCE;

  private Map<String, Channel> queueMap;
  private boolean errorMode;

  private ConnectionFactory factory;
  private Connection connection;

  private Gson gson;

  private static final String EXCHANGE_NAME = "Axoom.IoT";

  RabbitMqPublisher() {
    try {
      this.queueMap = new HashMap<>();
      this.gson = new Gson();
      setupConnection();
      this.errorMode = false;
    } catch (IOException e) {
      System.out.println("Error (IOException) while connecting to RabbitMQ..entering error mode");
      this.errorMode = true;
    } catch (TimeoutException e) {
      System.out.println("Error (Timeout) while connecting to RabbitMQ..entering error mode");
      this.errorMode = true;
    }
  }

  private void setupConnection() throws IOException, TimeoutException {
    this.factory = new ConnectionFactory();
    this.factory.setPort(AdapterConfiguration.INSTANCE.getRabbitMqPort());
    this.factory.setHost(AdapterConfiguration.INSTANCE.getRabbitMqHost());
    this.factory.setUsername(AdapterConfiguration.INSTANCE.getRabbitMqUser());
    this.factory.setPassword(AdapterConfiguration.INSTANCE.getRabbitMqPassword());
    this.connection = factory.newConnection();

  }

  public void fire(String event, String topic) {
    if (!channelActive(topic)) {
      setupChannel(topic);
    }
    System.out.println(topic);
    try {
      queueMap.get(topic).basicPublish(EXCHANGE_NAME, topic, null, event.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void setupChannel(String topic) {
    try {
      Channel channel = connection.createChannel();
      channel.exchangeDeclare(EXCHANGE_NAME, "topic", true, false, null);

      queueMap.put(topic, channel);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean channelActive(String topic) {
    return queueMap.containsKey(topic);
  }

  public void cleanup() {
    queueMap
            .keySet()
            .stream()
            .map(key -> queueMap.get(key))
            .forEach(channel -> {
              try {
                channel.close();
              } catch (IOException e) {
                e.printStackTrace();
              } catch (TimeoutException e) {
                e.printStackTrace();
              }
            });
    try {
      connection.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
