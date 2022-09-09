package com.doubledice.databuilder.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Objects;


/**
 * @author ponomarev 03.08.2022
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    public static final String DEFAULT_ADMIN_NAME = "spring.security.oauth2.client.registration.vk-app.defaultAdminName";
    public static final String DEFAULT_ADMIN_PASS = "spring.security.oauth2.client.registration.vk-app.defaultAdminPass";
    public static final String DEFAULT_USER_NAME = "spring.security.oauth2.client.registration.vk-app.defaultUserName";
    public static final String DEFAULT_USER_PASS = "spring.security.oauth2.client.registration.vk-app.defaultUserPass";
    @Autowired
    private Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(
                req -> req.anyRequest().authenticated()
        ).formLogin().and().logout().logoutSuccessUrl("/");
    }

    //todo move default users into DB
    //in memory
    @Bean
    public UserDetailsService users() {
        UserDetails admin = User.builder()
                .username(Objects.requireNonNull(env.getProperty(DEFAULT_ADMIN_NAME)))
                .password(Objects.requireNonNull(env.getProperty(DEFAULT_ADMIN_PASS)))
                .roles("ADMIN", "USER")
                .build(); //qwerty123
        UserDetails user = User.builder()
                .username(Objects.requireNonNull(env.getProperty(DEFAULT_USER_NAME)))
                .password(Objects.requireNonNull(env.getProperty(DEFAULT_USER_PASS)))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
//    @Bean
//    public JdbcUserDetailsManager users(DataSource dataSource) {
//        return new JdbcUserDetailsManager(dataSource);
//    }
}
