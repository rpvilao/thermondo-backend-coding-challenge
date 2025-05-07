package com.example.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@EntityScan("com.example.persistence.entity")
@EnableJpaRepositories("com.example.persistence.repository")
@EnableTransactionManagement
@Configuration
open class DatabaseConfig {
}