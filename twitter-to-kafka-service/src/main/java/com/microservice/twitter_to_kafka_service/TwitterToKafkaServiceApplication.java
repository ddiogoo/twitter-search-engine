package com.microservice.twitter_to_kafka_service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.microservice.twitter_to_kafka_service.config.TwitterToKafkaServiceConfigData;
import com.microservice.twitter_to_kafka_service.runner.StreamRunner;

@SpringBootApplication
@ComponentScan(basePackages = "com.microservice.twitter_to_kafka_service")
public class TwitterToKafkaServiceApplication implements CommandLineRunner {
	private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);
	private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
	private final StreamRunner streamRunner;

	public TwitterToKafkaServiceApplication(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData,
			StreamRunner streamRunner) {
		this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
		this.streamRunner = streamRunner;
	}

	public static void main(String[] args) {
		SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("App started at {} {}", LocalDate.now().toString(), LocalTime.now().toString());
		LOG.info(Arrays.toString(twitterToKafkaServiceConfigData.getTwitterKeywords().toArray()));
		LOG.info(twitterToKafkaServiceConfigData.getWelcomeMessage());
		streamRunner.start();
	}
}
