package com.gsrk.graphql.mongodb.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.gsrk.graphql.mongodb.model.Book;

public class BookTask implements Callable<List<Book>>{
	@Autowired
	RestTemplate restTemplate;
	
	List<String> keys;
	
	@Override
	public List<Book> call() throws Exception {
		// TODO Auto-generated method stub
		
		List<Book> books = new ArrayList<Book>();
		for(String key:keys) {
			System.out.println("fetchBookAsync Book Id:"+key+" by Thread Name:"+Thread.currentThread().getName());
			ResponseEntity<Book> resultBookEntity =
			        restTemplate.getForEntity("http://localhost:8080/book/"+key, Book.class);
			Book book = resultBookEntity.getBody();
			books.add(book);
		}
		return books;
	}

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

}
