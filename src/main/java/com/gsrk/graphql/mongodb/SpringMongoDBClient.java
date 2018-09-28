package com.gsrk.graphql.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.gsrk.graphql.mongodb.model.Book;

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
    
   @Bean
   public Map<String,DataLoader<String,Book>> dataLoaderMap(){
	   return new ConcurrentHashMap<>();
   }
    
	@Bean
	public DataLoader<String, Book> createBooksDataLoaderBean() {
		DataLoaderOptions dlo = new DataLoaderOptions();
		dlo.setBatchingEnabled(true);
		//dlo.setCachingEnabled(true);
		//dlo.setMaxBatchSize(50);
		DataLoader<String, Book> booksDataLoader = new DataLoader<String, Book>((keys) -> impl(keys), dlo);
		return booksDataLoader;
	}
	
	
	public CompletionStage<List<Book>> impl(List<String> keys) {
		System.out.println("calling loadBooks...");
		List<Book> books = new ArrayList<Book>();
		for(String key:keys) {
			ResponseEntity<Book> resultBookEntity =
			        restTemplate().getForEntity("http://localhost:8080/book/"+key, Book.class);
			Book book = resultBookEntity.getBody();
			books.add(book);
		}
		return CompletableFuture.supplyAsync(() -> {
					return books;
		});
	}
	
}
