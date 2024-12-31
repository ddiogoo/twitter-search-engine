package com.microservices.demo.twitter.to.kafka.service;

import com.microservices.demo.twitter.to.kafka.service.init.IStreamInitializer;
import com.microservices.demo.twitter.to.kafka.service.runner.ITwitterToKafkaStreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.demo")
public class TwitterToKafkaServiceApplication implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);

    private final ITwitterToKafkaStreamRunner twitterToKafkaStreamRunner;
    private final IStreamInitializer streamInitializer;
    public TwitterToKafkaServiceApplication(ITwitterToKafkaStreamRunner twitterToKafkaStreamRunner, IStreamInitializer streamInitializer) {
        this.twitterToKafkaStreamRunner = twitterToKafkaStreamRunner;
        this.streamInitializer = streamInitializer;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("App started successfully!");
        streamInitializer.init();
        twitterToKafkaStreamRunner.start();
    }
}
