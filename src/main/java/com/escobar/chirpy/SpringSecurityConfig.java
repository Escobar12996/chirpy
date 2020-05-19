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

            http.authorizeRequests().antMatchers(
            		"/resendemail",
            		"/confirmaccount",
            		"/profileimages/{id}",
            		"/profile/{id}",
            		"/profilefollower/{id}",
            		"/profilefollowers/{id}",
            		"/explorer/{hashtag}",
            		"/register", "/css/**",
            		"/js/**",
            		"/",
            		"/explorer",
            		"/image/{tipo}/{id}",
            		"/viewpublication/{id}"
            		).permitAll()
            
                .antMatchers(
            		"/home",
            		"/deleteimage/{id}",
            		"/home/response",
            		"/quotes",
            		"/follow/{id}",
            		"/unfollow/{id}",
            		"/editprofile",
            		"/getfollows",
            		"/deletepost/{id}",
            		"/editprofile/{id}",
            		"/editpass",
            		"/imagesu",
            		"/editimageprofile",
            		"/report/{id}"
            		).hasAuthority("user")
                
                .antMatchers(
            		"/administration",
            		"/administration/users",
            		"/administration/userimages/{id}",
            		"/administration/userpost/{id}",
            		"/administration/deleteimage",
            		"/administration/deletepost",
            		"/administration/posts",
            		"/administration/images",
            		"/administration/edituser/{id}",
            		"/activatedisableuser",
            		"/blockunblockuser",
            		"/administration/editImageprofile",
            		"/administration/imagesu",
            		"/administration/edituser",
            		"/administration/editpass",
            		"/administration/emoticons"
        		).hasAuthority("admin")
                
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
