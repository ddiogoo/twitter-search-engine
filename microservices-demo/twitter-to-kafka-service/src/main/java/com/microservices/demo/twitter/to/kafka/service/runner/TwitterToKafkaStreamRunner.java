package com.microservices.demo.twitter.to.kafka.service.runner;

import com.microservices.demo.twitter.to.kafka.service.config.TwitterToKafkaConfigData;
import com.microservices.demo.twitter.to.kafka.service.listener.TwitterToKafkaStatusListener;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.util.Arrays;

@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "false", matchIfMissing = true)
public class TwitterToKafkaStreamRunner implements ITwitterToKafkaStreamRunner {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaStreamRunner.class);
    private TwitterStream twitterStream;

    private final TwitterToKafkaConfigData twitterToKafkaConfigData;
    private final TwitterToKafkaStatusListener twitterToKafkaStatusListener;
    public TwitterToKafkaStreamRunner(TwitterToKafkaConfigData twitterToKafkaConfigData,
                                      TwitterToKafkaStatusListener twitterToKafkaStatusListener
    ) {
        this.twitterToKafkaConfigData = twitterToKafkaConfigData;
        this.twitterToKafkaStatusListener = twitterToKafkaStatusListener;
    }

    @Override
    public void start() throws TwitterException {
        twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(twitterToKafkaStatusListener);
        addFilter();
    }

    @PreDestroy
    public void shutdown() {
        if(twitterStream != null) {
            LOG.info("Closing twitter stream");
            twitterStream.shutdown();
        }
    }

    private void addFilter() {
        String[] keywords = twitterToKafkaConfigData.getTwitterKeywords().toArray(new String[0]);
        FilterQuery filterQuery = new FilterQuery(keywords);
        twitterStream.filter(filterQuery);
        LOG.info("Started filtering twitter stream for keywords: {}", Arrays.toString(keywords));
    }
}
