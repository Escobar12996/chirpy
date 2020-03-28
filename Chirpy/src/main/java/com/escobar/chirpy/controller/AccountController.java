/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.AuthorityDao;
import com.escobar.chirpy.models.dao.FollowDao;
import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserAuthorityDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.entity.Authority;
import com.escobar.chirpy.models.entity.Follow;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.UserAuthority;
import java.security.Principal;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author escob
 */

@Controller
public class AccountController {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private FollowDao followDao;
    
    @Autowired
    private AuthorityDao authorityDao;
    
    @Autowired
    private UserAuthorityDao userAuthorityDao;
    
    @Bean
    public BCryptPasswordEncoder paswordncoder() {
            return new BCryptPasswordEncoder();
    }
    
     @RequestMapping(value={"/login"}, method = RequestMethod.GET)
    public String loginPanel(@RequestParam(value="error", required = false) String error,
            Model model, Principal principal, @RequestParam(value = "logout", required = false) String logout) {
        
        if (principal != null){
            return "redirect:/home";
        }
        
        if (error != null){
            model.addAttribute("error", error);
            System.out.println(error);
        }
        
        if(logout != null) {
            model.addAttribute("success", "Sesion Cerrada");
        }
        
        model.addAttribute("title", "Iniciar Sesion");
        return "login";
    }
    
    @RequestMapping(value= {"/register"}, method = RequestMethod.GET)
    public String register(Model model) {

            model.addAttribute("user", new User());
            model.addAttribute("title", "Registrar Cuenta");
            return "register";
    }
	
    @RequestMapping(value={"/register"}, method = RequestMethod.POST)
    public String chech(@Valid User user, BindingResult result, Model model) {

        if (result.hasErrors()) {
                model.addAttribute("title", "Registrar Cuenta");
                return "register";
        } else if (userDao.findByUserName(user.getUsername()) != null){
                model.addAttribute("title", "Registrar Cuenta");
                model.addAttribute("error", "La cuenta ya existe");
                return "register";
        } else {
            PasswordEncoder encode = paswordncoder();
            user.setPassword(encode.encode(user.getPassword()));
            user.setEnabled(true);
            user.setNotLocker(true);
            userDao.save(user);
            followDao.save(new Follow(user, user));
            UserAuthority au = new UserAuthority();
            au.setUser(user);
            au.setAuthority(authorityDao.findByName("user"));
            userAuthorityDao.save(au);
            return "redirect:/"; 
        }
    }
}
