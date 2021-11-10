package com.iteixeira.pocspringcloud

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

val logger: Logger = LoggerFactory.getLogger("PocSpring_Cloud")

@SpringBootApplication
class PocSpringCloudApplication

fun main(args: Array<String>) {
    runApplication<PocSpringCloudApplication>(*args)
}

data class CustomerApi(
    @JsonProperty("name") val name: String,
    @JsonProperty("vat") val vat: String)

@EnableSqs
@Configuration
class SQSConfig(
    @Value("\${cloud.aws.region.static}") private val region:String,
    @Value("\${cloud.aws.credentials.access-key}") private val key:String,
    @Value("\${cloud.aws.credentials.secret-key}") private val secret:String
){

    @Bean
    @Primary
    fun awsSqsAsyns(): AmazonSQSAsync =
        AmazonSQSAsyncClientBuilder.standard().withRegion(region)
            .withCredentials(AWSStaticCredentialsProvider(
                BasicAWSCredentials(key,secret)
            )).build()

    @Bean
    fun sqsMessagaetemplate(): QueueMessagingTemplate =
        QueueMessagingTemplate(awsSqsAsyns())

}

@Component
class CustomerApiConsumer {

    @SqsListener(value = ["customer-register"], deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    fun listenerNewCustomer(customer: CustomerApi) {
        logger.info("Received SQS: {}", customer)
    }
}

@RestController
@RequestMapping(value = ["/v1/customers"])
class CustomerController {

    @GetMapping
    @RequestMapping(value = ["/hi/{text}"])
    fun hello(@PathVariable("text") t: String) = "${t}, Kotlin Customer!"

}


