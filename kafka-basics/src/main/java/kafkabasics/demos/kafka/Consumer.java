package kafkabasics.demos.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {

  private static final Logger log = LoggerFactory.getLogger(Consumer.class.getSimpleName());

  public static void main(String[] args) {
    log.info("starting create consumer");

    String topic = "demo_java";

    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "my-second-application");
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    // consumer
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

    // subscribe on topic(s)
    consumer.subscribe(Collections.singletonList(topic));

    // poll messages
    while (true) {
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

      for (ConsumerRecord<String, String> consumerRecord : records) {
        log.info("key: {}, value: {}, partition: {}", consumerRecord.key(), consumerRecord.value(), consumerRecord.partition());
      }

    }
 
  }
}
