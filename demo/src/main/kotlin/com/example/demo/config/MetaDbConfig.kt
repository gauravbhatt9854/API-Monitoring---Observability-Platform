package com.example.demo.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(
    basePackages = ["com.example.demo.repository.meta"],
    mongoTemplateRef = "metaMongoTemplate"
)
class MetaDbConfig(
    @Value("\${spring.data.mongodb.meta.uri}")
    private val metaUri: String
) {

    @Bean(name = ["metaFactory"])
    fun metaFactory() = SimpleMongoClientDatabaseFactory(metaUri)

    @Bean(name = ["metaMongoTemplate"])
    fun metaMongoTemplate(
        @Qualifier("metaFactory") factory: SimpleMongoClientDatabaseFactory
    ) = MongoTemplate(factory)
}
