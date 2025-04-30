package com.sivalabs.ft.features;

import com.sivalabs.ft.features.integration.EventPublisher;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventPublisherTestConfiguration {

    @Bean
    @ConditionalOnProperty(value = "ft.events.publisher", havingValue = "NONE")
    public EventPublisher eventPublisher() {
        return Mockito.mock(EventPublisher.class);
    }
}
