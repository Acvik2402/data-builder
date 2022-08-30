//package com.doubledice.databuilder.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.provisioning.JdbcUserDetailsManager;
//import org.springframework.security.web.authentication.HttpStatusEntryPoint;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//
//import javax.sql.DataSource;
//
///**
// * @author ponomarev 27.07.2022
// */
//
//@EnableWebSecurity(debug = true)
////@EnableOAuth2Sso
//public class SpringBootSecurityConfiguration extends WebSecurityConfigurerAdapter {
//
//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("usr").password("{noop}password").roles("ADMIN");
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        // @formatter:off
//        http
//                .authorizeRequests()
//                .antMatchers("/", "/error", "/webjars/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//                .and()
//                .logout().logoutSuccessUrl("/").permitAll()
//                .and()
//                .csrf()
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                .and()
////                .formLogin();
//                .oauth2Login();
//        // @formatter:on
//    }
//
//    //in memory
////    @Bean
////    public UserDetailsService users() {
////        UserDetails admin = User.builder()
////                .username("admin")
////                .password("{bcrypt}$2a$12$l4wDAy.tiDs.8JU0e2VkL.vwo2XHN7LCNV/atABf/7FBkziMybFse")
////                .roles("ADMIN", "USER")
////                .build(); //qwerty123
////        UserDetails user = User.builder()
////                .username("user")
////                .password("{bcrypt}$2a$12$l4wDAy.tiDs.8JU0e2VkL.vwo2XHN7LCNV/atABf/7FBkziMybFse")
////                .roles("USER")
////                .build();
////        return new InMemoryUserDetailsManager(user, admin);
////    }
//
//    //jdbc authentication
//    @Bean
//    public JdbcUserDetailsManager users(DataSource dataSource) {
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password("{bcrypt}$2a$12$l4wDAy.tiDs.8JU0e2VkL.vwo2XHN7LCNV/atABf/7FBkziMybFse")
//                .roles("ADMIN", "USER")
//                .build(); //qwerty123
//        UserDetails user = User.builder()
//                .username("user")
//                .password("{bcrypt}$2a$12$l4wDAy.tiDs.8JU0e2VkL.vwo2XHN7LCNV/atABf/7FBkziMybFse")
//                .roles("USER")
//                .build();
//        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
//        if (jdbcUserDetailsManager.userExists(user.getUsername())) {
//            jdbcUserDetailsManager.deleteUser(user.getUsername());
//        }
//        if (jdbcUserDetailsManager.userExists(admin.getUsername())) {
//            jdbcUserDetailsManager.deleteUser(admin.getUsername());
//        }
//        jdbcUserDetailsManager.createUser(user);
//        jdbcUserDetailsManager.createUser(admin);
//        return jdbcUserDetailsManager;
//    }
//
//    /*
//    @Bean
//    public JdbcUserDetailsManager users(DataSource dataSource) {
//        return new JdbcUserDetailsManager(dataSource);
//    }
//     */
//
//}
