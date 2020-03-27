package com.escobar.chirpy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Override
    protected void configure(HttpSecurity http) throws Exception {

            http.authorizeRequests().antMatchers("/register", "/css/**", "/js/**", "/explorer").permitAll()
                .antMatchers("/", "/home", "/mypublication", "/follow/{id}", "/unfollow/{id}", "/getfollows").hasAuthority("user")
                .and()
                .formLogin().loginPage("/login").permitAll()
                .and()
                .logout().permitAll();

    }




    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build) throws Exception  {

            build.userDetailsService(userDetailsService)
            .passwordEncoder(paswordEncoder());

    }
}
