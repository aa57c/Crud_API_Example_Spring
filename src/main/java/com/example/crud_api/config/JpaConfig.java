package com.example.crud_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // This enables automatic timestamp management
    // @CreatedDate and @LastModifiedDate will work automatically
}
