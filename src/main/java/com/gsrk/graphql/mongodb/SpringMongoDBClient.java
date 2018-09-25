package com.gsrk.graphql.mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Hello world!
 *
 */

@SpringBootApplication
public class SpringMongoDBClient 
{
    public static void main( String[] args )
    {
       SpringApplication.run(SpringMongoDBClient.class, args);
    }
    
    @Bean
   RestTemplate restTemplate() {
    	return new RestTemplate();
   }
}
