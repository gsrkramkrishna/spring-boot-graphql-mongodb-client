package com.gsrk.graphql.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.gsrk.graphql.mongodb.model.Book;

@RestController
public class FetchBookResource {
	
	@Autowired
	RestTemplate restTemplate;
	
	@RequestMapping(value="/all",method=RequestMethod.GET,produces= {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<Book>> fetchBooks(){
		ResponseEntity<List<Book>> booksListResp =
		        restTemplate.exchange("http://localhost:8080/book/all",
		                    HttpMethod.GET, null, new ParameterizedTypeReference<List<Book>>() {
		            });
		return booksListResp;
	}
	
	@RequestMapping(value="/async/all/v1",method=RequestMethod.GET,produces= {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<Book>> fetchBooksByAsync(){
		ResponseEntity<List<Book>> booksListResp =
		        restTemplate.exchange("http://localhost:8080/book/all",
		                    HttpMethod.GET, null, new ParameterizedTypeReference<List<Book>>() {
		            });
		
		List<Book> books = booksListResp.getBody();
		List<Book> resultBooks = new ArrayList<Book>();
		for(Book book:books) {
			CompletableFuture<Book> cfBook = fetchData("http://localhost:8080/book/", book);
			try {
				resultBooks.add(cfBook.get());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ResponseEntity.ok(resultBooks);
	}
	
	public CompletableFuture<Book> fetchData(final String url,final Book book) {
		ResponseEntity<Book> resultBookEntity =
		        restTemplate.getForEntity(url+book.getId(), Book.class);
		return CompletableFuture.completedFuture(resultBookEntity.getBody());
	}
	
	@RequestMapping(value="/async/all/v2",method=RequestMethod.GET,produces= {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<Book>> fetchBooksByNewAsync(){
		ResponseEntity<List<Book>> booksListResp =
		        restTemplate.exchange("http://localhost:8080/book/all",
		                    HttpMethod.GET, null, new ParameterizedTypeReference<List<Book>>() {
		            });
		
		List<Book> books = booksListResp.getBody();
		
		List<CompletableFuture<Book>> cfBooks = books.stream().map(book -> fetchBookAsync(book)).collect(Collectors.toList());
		
		List<Book> resultBooks = cfBooks.stream().map(CompletableFuture::join).collect(Collectors.toList());
		return ResponseEntity.ok(resultBooks);
	}
	
	public CompletableFuture<Book> fetchBookAsync(Book book) {
		
		CompletableFuture<Book> cfBook = CompletableFuture.supplyAsync(new Supplier<Book>() {

			@Override
			public Book get() {
				// TODO Auto-generated method stub
				ResponseEntity<Book> resultBookEntity =
				        restTemplate.getForEntity("http://localhost:8080/book/"+book.getId(), Book.class);
				return resultBookEntity.getBody();
			}
		});
		
		return cfBook;
	}
	
	@RequestMapping(value="/async/all/v3",method=RequestMethod.GET,produces= {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<Book>> fetchBooksByDataLoader(){
		ResponseEntity<List<Book>> booksListResp =
		        restTemplate.exchange("http://localhost:8080/book/all",
		                    HttpMethod.GET, null, new ParameterizedTypeReference<List<Book>>() {
		            });
		
		List<Book> books = booksListResp.getBody();
		
		List<String> keys = new ArrayList<String>();
		for(Book book:books) {
			keys.add(book.getId());
		}
		List<CompletableFuture<Book>> cfBooks = fetchBooksByDataLoader(keys);
		
		List<Book> resultBooks = cfBooks.stream().map(CompletableFuture::join).collect(Collectors.toList());
		return ResponseEntity.ok(resultBooks);
	}
	
	public List<CompletableFuture<Book>> fetchBooksByDataLoader(List<String> keys) {
		DataLoaderOptions dlo = new DataLoaderOptions();
		dlo.setBatchingEnabled(false);
		dlo.setCachingEnabled(false);
		DataLoader<String, Book> booksDataLoader = new DataLoader<String,Book>(fetchBatchLoder(keys),dlo);
		List<CompletableFuture<Book>> listOfCFBook = new ArrayList<CompletableFuture<Book>>();
		for(String key:keys) {
			CompletableFuture<Book> cfBook = booksDataLoader.load(key);
			listOfCFBook.add(cfBook);
		}
		
		return listOfCFBook;
	}
	
	public BatchLoader<String, Book> fetchBatchLoder(List<String> keys){
		
		BatchLoader<String, Book> batchLoader = new BatchLoader<String, Book>() {
			
			@Override
			public CompletionStage<List<Book>> load(List<String> keys) {
				// TODO Auto-generated method stub
				return loadBooks(keys);
			}
		};
		
		return batchLoader;
	}
	
	public CompletionStage<List<Book>> loadBooks(List<String> keys){
		List<Book> books = new ArrayList<Book>();
		for(String key:keys) {
			ResponseEntity<Book> resultBookEntity =
			        restTemplate.getForEntity("http://localhost:8080/book/"+key, Book.class);
			Book book = resultBookEntity.getBody();
			books.add(book);
		}
		return CompletableFuture.supplyAsync(() -> {
					return books;
		});
		
		/*
		 * 
		 * 
		 return CompletableFuture.supplyAsync(() -> {
		
					return books;
		}).thenApply(responses -> {
			responses.forEach(i -> {
				books.add(i);
			});
			return books;
		});
		 */
	}


}
