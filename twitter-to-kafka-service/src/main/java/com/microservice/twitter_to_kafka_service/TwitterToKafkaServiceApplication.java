package com.microservice.twitter_to_kafka_service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.microservice.twitter_to_kafka_service.config.TwitterToKafkaServiceConfigData;

@SpringBootApplication
public class TwitterToKafkaServiceApplication implements CommandLineRunner {
	private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);
	private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

	public TwitterToKafkaServiceApplication(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData) {
		this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
	}

	public static void main(String[] args) {
		SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("App started at {} {}", LocalDate.now().toString(), LocalTime.now().toString());
		LOG.info(Arrays.toString(twitterToKafkaServiceConfigData.getTwitterKeywords().toArray()));
		LOG.info(twitterToKafkaServiceConfigData.getWelcomeMessage());
	}
}
