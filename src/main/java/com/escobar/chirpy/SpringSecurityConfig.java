package com.escobar.chirpy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.escobar.chirpy.models.services.JpaUserDetailsService;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter  {

    @Autowired
    private JpaUserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder paswordEncoder() {
            return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider impl = new DaoAuthenticationProvider();
        impl.setUserDetailsService(userDetailsService);
        impl.setPasswordEncoder(new BCryptPasswordEncoder());
        impl.setHideUserNotFoundExceptions(false) ;
        return impl;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

            http.authorizeRequests().antMatchers("/regitrationConfirm", "/userimages", "/userdetails", "/explorer/{hashtag}", "/register", "/css/**", "/js/**", "/", "/explorer", "/image/{tipo}/{id}", "/viewpublication/{id}").permitAll()
                .antMatchers("/home/response", "/quotes", "/home", "/follow/{id}", "/unfollow/{id}", "/editprofile", "/getfollows", "/deletepost/{id}", "/editprofile/{id}", "/editpass", "/imagesu", "/editImageprofile").hasAuthority("user")
                .and()
                .formLogin().loginPage("/login").permitAll()
                .and()
                .logout().permitAll();

    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());

    }
}
