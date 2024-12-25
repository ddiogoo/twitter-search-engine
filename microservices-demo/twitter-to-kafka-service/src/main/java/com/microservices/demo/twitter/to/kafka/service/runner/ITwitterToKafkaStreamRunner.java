package com.microservices.demo.twitter.to.kafka.service.runner;

import twitter4j.TwitterException;

public interface ITwitterToKafkaStreamRunner {
    void start() throws TwitterException;
}
