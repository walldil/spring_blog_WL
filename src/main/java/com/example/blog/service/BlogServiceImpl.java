package com.example.blog.service;

import com.example.blog.model.Category;
import com.example.blog.model.Post;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.RoleRepository;
import com.example.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService {
    private UserRepository userRepository;
    private PostRepository postRepository;
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder bCrypt;

    @Autowired
    public BlogServiceImpl(UserRepository userRepository, PostRepository postRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.roleRepository = roleRepository;
    }
    @Override
    public boolean addUser(User user) {
        if(userRepository.findFirstByEmail(user.getEmail()) == null) {
            // przypisanie roli
            addRoleToUser(user, "ROLE_USER");
            // szyfrowanie hasła algorytmem bCrypt
            user.setPassword(bCrypt.encode(user.getPassword()));
            // zmiana - domyślnie user jestnieaktywny!
            user.setStatus(false);
            try {
                System.out.println("LINK AKTYWACYJNY:");
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] activateHashBytes = md.digest(("userEmail="+user.getEmail()).getBytes());
                String activateHash = "";
                for (byte b : activateHashBytes){
                    activateHash += String.format("%02x", b);
                }
                System.out.println("localhost:8080/activateUser&"+activateHash);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            userRepository.save(user);   // INSERT INTO user VALUES (?,?,?,?,?);
            return true;
        }
        return false;
    }
    @Override
    public User addRoleToUser(User user, String roleName) {
        Role role = roleRepository.findFirstByRoleName(roleName);
        user.getRoles().add(role);
        return user;
    }
    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "dateAdded"));
    }
    @Override
    public Optional<Post> getPostById(long postId){
        return postRepository.findById(postId);
    }

    @Override
    public Post addPostByUser(long userId, String title, String content, Category category) {
        if (userRepository.existsById(userId)){
            User user = userRepository.findById(userId).get();
            return postRepository.save(new Post(title, content, category, user));
        }
        return null;
    }

    @Override
    public boolean deleteUser(long userId) {
        boolean isDeleted = userRepository.existsById(userId);
        userRepository.deleteById(userId);
        return isDeleted;
    }
    @Override
    public Optional<User> getUserById(long userId) {
        return userRepository.findById(userId);
    }


    @Override
    public boolean updatePassword(long userId, String newPassword) {
        if(userRepository.findById(userId).isPresent()){
            User userToUpdate = userRepository.findById(userId).get();  // pobranie użytkownika po id
            userToUpdate.setPassword(newPassword);                      // aktualizacja pola password
            userRepository.save(userToUpdate);                          // zapis/update istniejącego obiektu
            return true;
        }
        return false;
    }
    @Override
    public List<User> getAllUsersOrderByregistrationDateDesc() {
        return userRepository.findAll();
    }

    public String getLoginStatus(Authentication auth){
        if(auth != null) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String email = userDetails.getUsername();
            Set<GrantedAuthority> roles = (Set<GrantedAuthority>) userDetails.getAuthorities();
            return email + " (" + roles.stream()
                                    .map(grantedAuthority -> grantedAuthority.toString()
                                            .replace("ROLE_",""))
                                    .collect(Collectors.joining(",")) + ")";
        }
        return null;
    }
    public void deletePostById(long postId) {
        postRepository.deleteById(postId);
    }
    public boolean isAdmin(Authentication auth) {
        if (auth != null) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            return userDetails.getAuthorities()
                    .stream()
                    .anyMatch(o -> ((GrantedAuthority) o).getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }
    public User getLoggedUser(Authentication auth){
        if (auth != null) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            return userRepository.findFirstByEmail(userDetails.getUsername());
        }
        return null;
    }
    public void updatePost(Post postToUpdate){
        long postId = postToUpdate.getPostId();
        Post postFromDB = postRepository.getOne(postId);
        // aktualizacja pól posta na podstawie postToUpdate
        postFromDB.setTitle(postToUpdate.getTitle());
        postFromDB.setContent(postToUpdate.getContent());
        postFromDB.setCategory(postToUpdate.getCategory());
        postRepository.save(postFromDB);
        // gdy obiekt posta jest już w bazie danych to post jest aktualizowany
    }
    public boolean activateUser(String hash) {
        List<User> users = getAllUsersOrderByregistrationDateDesc();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        for (User u : users) {
            String activateHash = "";
            byte[] activateHashBytes = md.digest(("userEmail=" + u.getEmail()).getBytes());
            for (byte b : activateHashBytes) {
                activateHash += String.format("%02x", b);
            }
            if (activateHash.equals(hash)) {
                u.setStatus(true);
                userRepository.save(u);
                return true;
            }
        }
        return false;
    }
}
