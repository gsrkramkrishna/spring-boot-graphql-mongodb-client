package com.gsrk.graphql.mongodb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Author {
    private String authorId;
    
	private String firstName;

    private String lastName;
    
    

    public Author() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Author(String authorId) {
    	this.authorId = authorId;
    }

    
	public Author(String firstName, String lastName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}


	public String getId() {
		return authorId;
	}


	public void setId(String authorId) {
		this.authorId = authorId;
	}


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Author author = (Author) o;

        return authorId.equals(author.authorId);
    }

    @Override
    public int hashCode() {
        return authorId.hashCode();
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + authorId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}