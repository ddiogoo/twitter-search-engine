package com.microservices.demo.twitter.to.kafka.service.runner;

import com.microservices.demo.config.TwitterToKafkaConfigData;
import com.microservices.demo.twitter.to.kafka.service.exception.TwitterToKafkaServiceException;
import com.microservices.demo.twitter.to.kafka.service.listener.TwitterToKafkaStatusListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockTwitterToKafkaStreamRunner implements ITwitterToKafkaStreamRunner {
    private static final Logger LOG = LoggerFactory.getLogger(MockTwitterToKafkaStreamRunner.class);
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

    private final TwitterToKafkaConfigData twitterToKafkaConfigData;
    private final TwitterToKafkaStatusListener twitterToKafkaStatusListener;
    public MockTwitterToKafkaStreamRunner(TwitterToKafkaConfigData twitterToKafkaConfigData,
                                          TwitterToKafkaStatusListener twitterToKafkaStatusListener
    ) {
        this.twitterToKafkaConfigData = twitterToKafkaConfigData;
        this.twitterToKafkaStatusListener = twitterToKafkaStatusListener;
    }

    @Override
    public void start() throws TwitterException {
        long sleepTimeMs = twitterToKafkaConfigData.getMockSleepMs();
        int minTweetLength = twitterToKafkaConfigData.getMockMinTweetLength();
        int maxTweetLength = twitterToKafkaConfigData.getMockMaxTweetLength();
        String[] keyWords = twitterToKafkaConfigData.getTwitterKeywords().toArray(new String[0]);
        LOG.info("Starting mock filtering twitter stream for keywords {}", Arrays.toString(keyWords));
        simulateTwitterStream(sleepTimeMs, minTweetLength, maxTweetLength, keyWords);
    }

    private void simulateTwitterStream(long sleepTimeMs, int minTweetLength, int maxTweetLength, String[] keyWords) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while (true) {
                    String formattedTweetAsRawJson = getFormattedTweetAsRawJson(keyWords, minTweetLength, maxTweetLength);
                    Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
                    twitterToKafkaStatusListener.onStatus(status);
                    sleep(sleepTimeMs);
                }
            } catch (TwitterException e) {
                LOG.info("Error creating twitter status!", e);
            }
        });
    }

    private void sleep(long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new TwitterToKafkaServiceException("Error while sleeping for waiting new status to create!", e);
        }
    }

    private String getFormattedTweetAsRawJson(String[] keywords, int minTweetLength, int maxTweetLength) {
        String[] params = new String[] {
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
                getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
        };
        return formatTweetAsJsonWithParams(params);
    }

    private String formatTweetAsJsonWithParams(String[] params) {
        String tweet = tweetAsRawJson;
        for (int i = 0; i < params.length; i++) {
            tweet = tweet.replace("{" + i + "}", params[i]);
        }
        return tweet;
    }

    private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength) {
        StringBuilder tweetContent = new StringBuilder();
        int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength) + minTweetLength;
        return constructRandomTweet(keywords, tweetContent, tweetLength);
    }

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
