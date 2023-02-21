package io.demos.kafka.wikimedia;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

public class WikimediaChangesProducer {

  public static void main(String[] args) throws InterruptedException {

    String bootstrapServer = "127.0.0.1:9092";
    String topic = "wikimedia.recentchange";

    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    // kafka safe (kafka <= 2.8)
    properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
    properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
    properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));

    // kafka high throughput producer config
    properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
    properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
    properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));


    // kafka producer
    KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

    EventHandler eventHandler = new WikimediaEventChangeHandler(producer, topic);

    String url = "https://stream.wikimedia.org/v2/stream/recentchange";
    EventSource eventSource = new EventSource.Builder(eventHandler, URI.create(url)).build();

    eventSource.start();

    TimeUnit.MINUTES.sleep(10);


  }
}
