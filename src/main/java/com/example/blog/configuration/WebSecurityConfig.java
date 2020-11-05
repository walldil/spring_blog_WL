package com.example.blog.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                .antMatchers("/addPost").hasAnyAuthority("ROLE_USER","ROLE_ADMIN")
                .antMatchers("/posts*").hasAnyAuthority("ROLE_USER","ROLE_ADMIN")
                .antMatchers(HttpMethod.POST,"/deletePost").hasAnyAuthority("ROLE_USER","ROLE_ADMIN")
                .antMatchers("/updatePost*").hasAnyAuthority("ROLE_USER")
                .anyRequest().permitAll()
                    .and()
                .csrf().disable()
                .formLogin().loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .loginProcessingUrl("/login_process")
                .failureUrl("/login&error")
                .defaultSuccessUrl("/")
                    .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/");
    }

    @Autowired
    DataSource dataSource;

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .usersByUsernameQuery(
                        "SELECT u.email, u.password, u.status FROM user u WHERE u.email = ?")
                .authoritiesByUsernameQuery(
                        "SELECT u.email, r.role_name FROM user u " +
                                "join user_has_role ur on (u.user_id = ur.user_id) " +
                                "join role r on (ur.role_id = r.role_id) " +
                        "WHERE u.email = ?")
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder());
    }
}
