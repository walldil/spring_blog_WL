package com.example.blog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@AllArgsConstructor
//@NoArgsConstructor
@Data
@Entity     // tworzy encję i mapuje ją na tabele
public class User {

    @Id                                                     // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // auto-inkrementacja
    private long userId;

    @NotBlank(message = "field name is mandatory")
    private String name;

    @NotBlank(message = "field last name is mandatory")
    private String lastName;

    @Email(message = "email is not valid")
    @NotBlank(message = "field email is mandatory")
    private String email;

    @NotBlank(message = "field password is mandatory")
    @Size(min = 6, max = 255,
            message = "password requires at least {min} characters (not more than {max})")
    private String password;

    private LocalDateTime registrationDateTime;

    private boolean status;

    @ManyToMany
    @JoinTable(
            name = "user_has_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )

    private Set<Role> roles = new HashSet<>();

    public User(String name, String lastName, String email, String password) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.registrationDateTime = LocalDateTime.now();
        this.status = true;
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getRegistrationDateTime() {
        return registrationDateTime;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRegistrationDateTime(LocalDateTime registrationDateTime) {
        this.registrationDateTime = registrationDateTime;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isStatus() {
        return status;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public String getPassword() {
        return password;
    }


    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }
}
