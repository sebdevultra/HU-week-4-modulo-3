package com.riwi.creditapplication.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter appErrorCounter(MeterRegistry registry) {
        return Counter.builder("app.credit.errors.count")
                .description("Total number of application errors")
                .register(registry);
    }

    @Bean
    public Counter authFailureCounter(MeterRegistry registry) {
        return Counter.builder("security.auth.failures")
                .description("Total number of authentication failures")
                .register(registry);
    }
}
