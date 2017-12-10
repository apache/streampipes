package org.streampipes.pe.sinks.standalone.samples.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by riemer on 29.03.2017.
 */
public class RabbitMqPublisher {

  private Map<String, Channel> queueMap;
  private boolean errorMode;

  private ConnectionFactory factory;
  private Connection connection;

  private RabbitMqParameters params;

  private static final String EXCHANGE_NAME = "Axoom.IoT";
  private static final Logger LOG = LoggerFactory.getLogger(RabbitMqPublisher.class);

  public RabbitMqPublisher(RabbitMqParameters params) {
    try {
      this.queueMap = new HashMap<>();
      this.params = params;
      setupConnection();
      this.errorMode = false;
    } catch (IOException e) {
      LOG.error("Error (IOException) while connecting to RabbitMQ..entering error mode");
      this.errorMode = true;
    } catch (TimeoutException e) {
      LOG.error("Error (Timeout) while connecting to RabbitMQ..entering error mode");
      this.errorMode = true;
    }
  }

  private void setupConnection() throws IOException, TimeoutException {
    this.factory = new ConnectionFactory();
    this.factory.setPort(params.getRabbitMqPort());
    this.factory.setHost(params.getRabbitMqHost());
    this.factory.setUsername(params.getRabbitMqUser());
    this.factory.setPassword(params.getRabbitMqPassword());
    this.connection = factory.newConnection();

  }

  public void fire(byte[] event, String topic) {
    if (!channelActive(topic)) {
      setupChannel(topic);
    }
    try {
      queueMap.get(topic).basicPublish(EXCHANGE_NAME, topic, null, event);
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
