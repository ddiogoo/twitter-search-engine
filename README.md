# Twitter Search Engine

This project is for academic purposes only. The project is a search engine that uses the Twitter API to search for tweets based on a keyword. The search engine will return the tweets that contain the keyword and display them in a user-friendly way. The search engine will also display the number of tweets that contain the keyword and the number of tweets that do not contain the keyword.

## Configuration

### Twitter API

The Twitter API is not available for free, so we are using a mock API for this project. The mock API is a simple JSON file that contains a list of tweets. The tweets are in the following format:

```json
{
  "created_at": "value",
  "id": 1,
  "text": "This is a tweet",
  "user": { "id": 1 }
}
```

The mock API is generate automatically by the project.

### twitter-to-kafka-service microservice

The `twitter-to-kafka-service` microservice is responsible for fetching tweets from the Twitter API and sending them to a Kafka topic. The `twitter-to-kafka-service` microservice is a mock service that generates tweets and sends them to a Kafka topic.

Follow the environment variables to configure the `twitter-to-kafka-service` microservice:

- The `application.yaml` file is located in the `twitter-to-kafka-service/src/main/resources` directory. The `application.yaml` file contains the following configuration:

  ```application.yaml
  twitter-to-kafka-service:
    twitter-keywords: Java, Microservices, Kafka, Spring Boot, Elasticsearch
    welcome-message: Hello Microservices!
    enable-mock-tweets: true
    mock-min-tweet-length: 5
    mock-max-tweet-length: 10
    mock-sleep-ms: 10000
  ```

- The `logback.xml` file is located in the `twitter-to-kafka-service/src/main/resources` directory. The `logback.xml` file contains the following configuration:

  ```logback.xml
  <?xml version="1.0" encoding="UTF-8"?>
  <configuration>
      <property name="DEV_HOME" value="./logs"/>
      <property name="APP_NAME" value="twitter-to-kafka-service"/>

      <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
          <layout class="ch.qos.logback.classic.PatternLayout">
              <Pattern>
                  %d{yyyy-MM-dd HH:mm:ss} %-5level [%thread] %logger{36} - %msg%n
              </Pattern>
          </layout>
      </appender>

      <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
          <file>${DEV_HOME}/${APP_NAME}.log</file>
          <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
              <Pattern>
                  %d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
              </Pattern>
          </encoder>
          <!-- This rolling policy doesn't work -->
          <!-- <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
              <fileNamePattern>${DEV_HOME}/archived/${APP_NAME}-log.%d{yyyy-MM-dd}.%i.log
              </fileNamePattern>
              <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                  <maxFileSize>10MB</maxFileSize>
              </timeBasedFileNamingAndTriggeringPolicy>
          </rollingPolicy> -->

          <!-- This rolling policy works -->
          <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
              <!-- rollover daily -->
              <fileNamePattern>${DEV_HOME}/archived/${APP_NAME}-log.%d{yyyy-MM-dd}.%i.log
              </fileNamePattern>
              <maxHistory>30</maxHistory>
              <maxFileSize>10MB</maxFileSize>
          </rollingPolicy>
      </appender>


      <logger name="com.microservice.twitter_to_kafka_service" level="info" additivity="false">
          <appender-ref ref="FILE"/>
          <appender-ref ref="STDOUT"/>
      </logger>

      <root level="info">
          <appender-ref ref="FILE"/>
          <appender-ref ref="STDOUT"/>
      </root>

  </configuration>
  ```

- If you have a paid version of the Twitter API, the `twitter4j.properties` settings file must be created in the `twitter-to-kafka-service/src/main/resources` directory. The `twitter4j.properties` file must contain the following configuration:

  ```twitter4j.properties
  debug=true
  oauth.consumerKey=**********
  oauth.consumerSecret==**********
  oauth.accessToken==**********
  oauth.accessTokenSecret==**********
  ```

  The `enable-mock-tweets` on application.yaml must be set to `false`.

## Notes

This README will be updated as the project progresses.
