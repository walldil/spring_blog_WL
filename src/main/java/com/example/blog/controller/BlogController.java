package com.example.blog.controller;

import com.example.blog.model.Category;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.service.BlogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller                  // klasa mapująca żądania protokołu http
public class BlogController {
    private BlogServiceImpl blogService;    // obiekt klasy BlogServiceImpl jest wstrzykiwany do BlogController
    @Autowired
    public BlogController(BlogServiceImpl blogService) {
        this.blogService = blogService;
    }
    @GetMapping("/")        // na adresie URL localhost:8080/
    public String home(Model model, Authentication auth){   // wywoływana jest metoda zwracająca String
        model.addAttribute("auth", blogService.getLoginStatus(auth));
        model.addAttribute("isAdmin", blogService.isAdmin(auth));
        model.addAttribute("author", blogService.getLoggedUser(auth));
        List<Post> posts = blogService.getAllPosts();   // pobranie postów z DB
        model.addAttribute("header_title", "BLOG IT");
        model.addAttribute("header_author", "walldi");
        model.addAttribute("posts", posts); // przekazanie listy postów do front-end
        model.addAttribute("cats", Category.values());
        model.addAttribute("newPost", new Post());
        return "blog";      // wartością zwracaną jest nazwa szablony Thymeleaf
                            // -> domyślna lokalizacja to resources/templates
                            // -> nie dopisujemy rozszerzenia .html
    }
    @PostMapping("/addPost")
    public String addPost(@Validated @ModelAttribute("newPost") Post newPost,
                          BindingResult bindingResult,
                          Model model, Authentication auth){
        if(bindingResult.hasErrors()){
            // gdy pos nie został dodany widok blog powinien się wyświetlić na znaczniku formularza
            List<Post> posts = blogService.getAllPosts();   // pobranie postów z DB
            model.addAttribute("header_title", "BLOG IT");
            model.addAttribute("header_author", "walldi");
            model.addAttribute("posts", posts); // przekazanie listy postów do front-end
            model.addAttribute("cats", Category.values());
//            return "redirect:/#simpleAnchor";
            return "blog";
        }
        blogService.addPostByUser(
                blogService.getLoggedUser(auth).getUserId(), newPost.getTitle(), newPost.getContent(), newPost.getCategory());
        return "redirect:/";    // przekierowanie na adres localhost:8080/
    }
    @GetMapping("/posts&{postId}")
    public String getPost(@PathVariable("postId") Long postId, Model model, Authentication auth){
        model.addAttribute("auth", blogService.getLoginStatus(auth));
        Optional<Post> postOpt = blogService.getPostById(postId);
        if(postOpt.isPresent()){
            model.addAttribute("post", postOpt.get());
            return "post";
        }
        model.addAttribute("post", null);
        return "post";
    }
    @GetMapping("/addUser")
    public String addUser(Model model, Authentication auth){
        model.addAttribute("auth", blogService.getLoginStatus(auth));
        model.addAttribute("user", new User());
        model.addAttribute("header_title", "REGISTRATION FORM");
        return "registration";
    }
    @PostMapping("/addUser")
    public String addUser(@Valid @ModelAttribute("user") User user,
                          BindingResult bindingResult,
                          Model model){
        if(bindingResult.hasErrors()) {
            return "registration";
        }
        boolean isRegistered = blogService.addUser(new User(
                user.getName(), user.getLastName(),
                user.getEmail(), user.getPassword()));
        if(isRegistered){
            return "redirect:/";
        }
        model.addAttribute("isRegistered",
                "Email: " + user.getEmail() + " is registered in our service");
        return "registration";
    }
    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("header_title", "LOGIN PAGE");
        return "login";
    }
    @GetMapping("/login&error")
    public String loginError(Model model){
        model.addAttribute("header_title", "LOGIN PAGE");
        model.addAttribute("error", "Bad credentials");
        return "login";
    }
    @PostMapping("/deletePost")
    public String deletePost(
            @RequestParam("post_id") long postId){
        blogService.deletePostById(postId);
        return "redirect:/";
    }
    @GetMapping("/updatePost")
    public String updatePost(
            @RequestParam("post_id") long postId,
            Model model,
            Authentication auth
    ){
        model.addAttribute("auth", blogService.getLoginStatus(auth));
        model.addAttribute("header_title", "UPDATE POST FORM");
        // wyświetlanie formularza do edycji z aktualnymi wartościami w polach title, content, category
        model.addAttribute("postToUpdate", blogService.getPostById(postId));
        model.addAttribute("cats", Category.values());
        return "updatePost";
    }
    @PostMapping("/updatePost")
    public String updatePost(
            @Valid @ModelAttribute("postToUpdate") Post postToUpdate,
            BindingResult bindingResult,
            Model model,
            Authentication auth
    ) {
        if (bindingResult.hasErrors()){ // gdy są błędy walidacji fomularza
            model.addAttribute("auth", blogService.getLoginStatus(auth));
            model.addAttribute("header_title", "UPDATE POST FORM");
            model.addAttribute("cats", Category.values());
            return "updatePost";
        }
        // edycja posta -> post_id, title, content, category
        blogService.updatePost(postToUpdate);
        return "redirect:/";
    }
    @GetMapping("/activateUser&{hexHash}")
    public String activateUser(
            @PathVariable("hexHash") String hexHash, Authentication auth, Model model
    ){
        boolean decision = blogService.activateUser(hexHash);
        if(decision){
            return "redirect:/login";
        }
        model.addAttribute("auth", blogService.getLoginStatus(auth));
        model.addAttribute("header_title", "YOU ARE NOT ACTIVATED");
        return "errorPage";
    }
}
