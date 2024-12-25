package com.microservice.twitter_to_kafka_service.exception;

public class TwitterToKafkaServiceException extends RuntimeException {
  public TwitterToKafkaServiceException(String message) {
    super(message);
  }

  public TwitterToKafkaServiceException(Throwable cause) {
    super(cause);
  }

  public TwitterToKafkaServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
