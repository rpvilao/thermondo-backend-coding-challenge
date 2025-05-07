package com.example.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan("com.example")
@Import(DatabaseConfig::class)
open class PopcornTimeConfig {
    @Bean
    fun dispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}