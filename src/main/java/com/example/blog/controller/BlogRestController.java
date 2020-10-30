package com.example.blog.controller;

import com.example.blog.model.Category;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.service.BlogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController   // kontroler generujący wyniki w postaci REST API
@RequestMapping("/rest")
//@Controller     // kontrolek komunikujący się z warstwą front-end
public class BlogRestController {
    private BlogServiceImpl blogService;
    @Autowired
    public BlogRestController(BlogServiceImpl blogService) {
        this.blogService = blogService;
    }
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
    @GetMapping("/hello/{name}")        // {var} - zmienna osadzona w ścieżce
    public String helloMe(@PathVariable("name") String name){
        return "hello " + name.toUpperCase();
    }
    // żądanie dodania nowego użytkownika do tabeli user
    @PostMapping("/addUser")
    public boolean addUser(
            @RequestParam("name") String name, @RequestParam("lastName") String lastName,
            @RequestParam("email") String email, @RequestParam("password") String password
    ){
        return blogService.addUser(new User(name, lastName, email, password));
    }
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return blogService.getAllUsersOrderByregistrationDateDesc();
    }
    @DeleteMapping("/deleteUser")
    public boolean deleteUser(@RequestParam("userId") Long userId){
        return blogService.deleteUser(userId);
    }
    @GetMapping("/user/{userId}")
    public String getUserById(@PathVariable("userId") Long userId){
        Optional<User> userOpt = blogService.getUserById(userId);
        if(userOpt.isPresent()){
            return userOpt.get().toString();
        }
        return "brak użytkownika o id: " + userId;
    }
    @PutMapping("/updateUserPassword")
    public boolean updateUserPassword(@RequestParam("userId") Long userId,
                                      @RequestParam("newPassword") String newPassword){
        return blogService.updatePassword(userId,newPassword);
    }
    @PostMapping("/addPost")
    public Post addPostByUser(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category")Category category,
            @RequestParam("user_id") long userId
            ){
        return blogService.addPostByUser(userId, title, content, category);
    }
    @GetMapping("/posts")
    public List<Post> getAllPosts(){
        return blogService.getAllPosts();
    }
}
