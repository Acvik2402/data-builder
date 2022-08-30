package com.doubledice.databuilder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;


/**
 * @author ponomarev 03.08.2022
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
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
                .username("admin")
                .password("{bcrypt}$2a$12$l4wDAy.tiDs.8JU0e2VkL.vwo2XHN7LCNV/atABf/7FBkziMybFse")
                .roles("ADMIN", "USER")
                .build(); //qwerty123
        UserDetails user = User.builder()
                .username("user")
                .password("{bcrypt}$2a$12$l4wDAy.tiDs.8JU0e2VkL.vwo2XHN7LCNV/atABf/7FBkziMybFse")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
//    @Bean
//    public JdbcUserDetailsManager users(DataSource dataSource) {
//        return new JdbcUserDetailsManager(dataSource);
//    }
}
