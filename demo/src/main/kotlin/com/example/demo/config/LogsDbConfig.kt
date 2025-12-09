package com.example.demo.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(
    basePackages = ["com.example.demo.repository.logs"],
    mongoTemplateRef = "logsMongoTemplate"
)
class LogsDbConfig(
    @Value("\${spring.data.mongodb.logs.uri}")
    private val logsUri: String
) {

    @Bean(name = ["logsFactory"])
    @Primary
    fun logsFactory() = SimpleMongoClientDatabaseFactory(logsUri)

    @Bean(name = ["logsMongoTemplate"])
    fun logsMongoTemplate(
        @Qualifier("logsFactory") factory: SimpleMongoClientDatabaseFactory
    ) = MongoTemplate(factory)
}
