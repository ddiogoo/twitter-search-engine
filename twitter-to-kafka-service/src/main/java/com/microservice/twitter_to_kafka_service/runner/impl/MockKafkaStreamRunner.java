package com.microservice.twitter_to_kafka_service.runner.impl;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.microservice.twitter_to_kafka_service.config.TwitterToKafkaServiceConfigData;
import com.microservice.twitter_to_kafka_service.exception.TwitterToKafkaServiceException;
import com.microservice.twitter_to_kafka_service.listener.TwitterKafkaStatusListener;
import com.microservice.twitter_to_kafka_service.runner.StreamRunner;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {
  private static final Logger LOG = LoggerFactory.getLogger(MockKafkaStreamRunner.class);

  private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
  private final TwitterKafkaStatusListener twitterKafkaStatusListener;

  private static final Random RANDOM = new Random();
  private static final String[] WORDS = { "Lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing",
      "elit", "Curabitur", "vel", "hendrerit", "libero", "eleifend", "blandit", "nunc", "ornare", "odio", "ut",
      "orci", "gravida", "imperdiet", "nullam", "purus", "lacinia", "Aenean", "placerat", "porttitor", "at", "diam",
      "mauris", "pellentesque", "Praesent", "congue", "laoreet", "Aenean", "nunc", "et", "viverra", "dignissim",
      "libero", "eleifend", "blandit", "nunc", "ornare", "odio", "ut", "orci", "gravida", "imperdiet", "nullam",
      "purus", "lacinia", "Aenean", "placerat", "porttitor", "at", "diam", "mauris", "pellentesque", "Praesent",
      "congue", "laoreet", "Aenean", "nunc", "et", "viverra", "dignissim" };
  private static final String tweetAsRawJson = "{" +
      "\"created_at\":\"{0}\"," +
      "\"id\":{1}," +
      "\"text\":\"{2}\"," +
      "\"user\":{\"id\":{3}}" +
      "}";
  private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";

  public MockKafkaStreamRunner(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData,
      TwitterKafkaStatusListener twitterKafkaStatusListener) {
    this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
    this.twitterKafkaStatusListener = twitterKafkaStatusListener;
  }

  @Override
  public void start() throws TwitterException {
    long sleepTimeMs = twitterToKafkaServiceConfigData.getMockSleepMs();
    int minTweetLength = twitterToKafkaServiceConfigData.getMockMinTweetLength();
    int maxTweetLength = twitterToKafkaServiceConfigData.getMockMaxTweetLength();
    String[] keyWords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
    LOG.info("Starting mock filtering twitter stream for keywords {}", Arrays.toString(keyWords));
    simulateTwitterStream(sleepTimeMs, minTweetLength, maxTweetLength, keyWords);
  }

  /**
   * Simulate twitter stream by generating random tweets with given sleep time
   * 
   * @param sleepTimeMs    sleep time in milliseconds
   * @param minTweetLength minimum tweet length
   * @param maxTweetLength maximum tweet length
   * @param keyWords       keywords to be included in the tweet
   */
  private void simulateTwitterStream(long sleepTimeMs, int minTweetLength, int maxTweetLength, String[] keyWords) {
    Executors.newSingleThreadExecutor().submit(() -> {
      try {
        while (true) {
          String formattedTweetAsRawJson = getFormattedTweetAsRawJson(keyWords, minTweetLength, maxTweetLength);
          Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
          twitterKafkaStatusListener.onStatus(status);
          sleep(sleepTimeMs);
        }
      } catch (TwitterException e) {
        LOG.info("Error creating twitter status!", e);
      }
    });
  }

  /**
   * Sleep for given time in milliseconds
   * 
   * @param sleepTimeMs time in milliseconds
   */
  private void sleep(long sleepTimeMs) {
    try {
      Thread.sleep(sleepTimeMs);
    } catch (InterruptedException e) {
      throw new TwitterToKafkaServiceException("Error while sleeping for waiting new status to create!", e);
    }
  }

  /**
   * Get formatted tweet as raw json
   * 
   * @param keywords       keywords to be included in the tweet
   * @param minTweetLength minimum tweet length
   * @param maxTweetLength maximum tweet length
   * @return formatted tweet as raw json
   */
  private String getFormattedTweetAsRawJson(String[] keywords, int minTweetLength, int maxTweetLength) {
    String[] params = new String[] {
        ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
        String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
        getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
        String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
    };
    return formatTweetAsJsonWithParams(params);
  }

  /**
   * Format tweet as raw json with given parameters
   * 
   * @param params parameters to be included in the tweet
   * @return formatted tweet as raw json
   */
  private String formatTweetAsJsonWithParams(String[] params) {
    String tweet = tweetAsRawJson;
    for (int i = 0; i < params.length; i++) {
      tweet = tweet.replace("{" + i + "}", params[i]);
    }
    return tweet;
  }

  /**
   * Generate random tweet content with random length
   * 
   * @param keywords       keywords to be included in the tweet
   * @param minTweetLength minimum tweet length
   * @param maxTweetLength maximum tweet length
   * @return random tweet content
   */
  private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength) {
    StringBuilder tweetContent = new StringBuilder();
    int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength) + minTweetLength;
    return constructRandomTweet(keywords, tweetContent, tweetLength);
  }

  /**
   * Construct random tweet with given keywords and tweet length
   * 
   * @param keywords     keywords to be included in the tweet
   * @param tweetContent tweet content
   * @param tweetLength  tweet length
   * @return random tweet
   */
  private String constructRandomTweet(String[] keywords, StringBuilder tweetContent, int tweetLength) {
    for (int i = 0; i < tweetLength; i++) {
      tweetContent.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
      if (i == tweetLength / 2) {
        tweetContent.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
      }
    }
    return tweetContent.toString();
  }
}
