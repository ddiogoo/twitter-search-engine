package com.microservice.twitter_to_kafka_service.runner.impl;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.microservice.twitter_to_kafka_service.config.TwitterToKafkaServiceConfigData;
import com.microservice.twitter_to_kafka_service.listener.TwitterKafkaStatusListener;
import com.microservice.twitter_to_kafka_service.runner.StreamRunner;

import jakarta.annotation.PreDestroy;
import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "false", matchIfMissing = true)
public class TwitterKafkaStreamRunner implements StreamRunner {
  private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStreamRunner.class);
  private TwitterStream twitterStream;

  private final TwitterKafkaStatusListener twitterKafkaStatusListener;
  private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

  public TwitterKafkaStreamRunner(
      TwitterKafkaStatusListener twitterKafkaStatusListener,
      TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData) {
    this.twitterKafkaStatusListener = twitterKafkaStatusListener;
    this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
  }

  @PreDestroy
  public void shutdown() {
    if (twitterStream != null) {
      LOG.info("Closing twitter stream");
      twitterStream.shutdown();
    }
  }

  @Override
  public void start() throws TwitterException {
    twitterStream = new TwitterStreamFactory().getInstance();
    twitterStream.addListener(twitterKafkaStatusListener);
    addFilter();
  }

  /**
   * Add filter to twitter stream
   */
  private void addFilter() {
    String[] keyWords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
    FilterQuery filterQuery = new FilterQuery(keyWords);
    twitterStream.filter(filterQuery);
    LOG.info("Started filtering twitter stream for keywords {}", Arrays.toString(keyWords));
  }
}
