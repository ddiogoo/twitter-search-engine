package com.microservices.demo.twitter.to.kafka.service.listener;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.kafka.model.avro.TwitterAvroModel;
import com.microservices.demo.kafka.producer.config.services.IKafkaProducer;
import com.microservices.demo.twitter.to.kafka.service.transformer.TwitterStatusToAvroTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Component
public class TwitterToKafkaStatusListener extends StatusAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaStatusListener.class);

    private final KafkaConfigData kafkaConfigData;
    private final IKafkaProducer<Long, TwitterAvroModel> kafkaProducer;
    private final TwitterStatusToAvroTransformer twitterStatusToAvroTransformer;
    public TwitterToKafkaStatusListener(KafkaConfigData kafkaConfigData,
                                        IKafkaProducer<Long, TwitterAvroModel> kafkaProducer,
                                        TwitterStatusToAvroTransformer twitterStatusToAvroTransformer)
    {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaProducer = kafkaProducer;
        this.twitterStatusToAvroTransformer = twitterStatusToAvroTransformer;
    }

    @Override
    public void onStatus(Status status) {
        LOG.info("Received status text {} sending to kafka topic {}", status.getText(), kafkaConfigData.getTopicName());
        TwitterAvroModel twitterAvroModel = twitterStatusToAvroTransformer.getTwitterAvroModelFromStatus(status);
        kafkaProducer.send(kafkaConfigData.getTopicName(), twitterAvroModel.getUserId(), twitterAvroModel);
    }
}
