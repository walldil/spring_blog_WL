package com.example.blog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Entity
//@NoArgsConstructor
//@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postId;

    @NotBlank(message = "title must be not empty")
    private String title;

    @Column(columnDefinition = "text")
    @NotBlank(message = "content must be not empty")
    private String content;

    private Category category;

    @CreationTimestamp
    private LocalDateTime dateAdded;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User author;

    public Post(String title, String content, Category category, User author) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.author = author;
    }

    public Post() {

    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public User getAuthor() {
        return author;
    }

    public long getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Category getCategory() {
        return category;
    }
    public void setPostId(long postId) {
        this.postId = postId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
