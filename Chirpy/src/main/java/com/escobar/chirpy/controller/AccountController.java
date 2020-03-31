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
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.UserAuthority;
import java.security.Principal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
    private PublicationDao publicationDao;
    
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
    
    @RequestMapping(value={"/userdetails"}, method = RequestMethod.GET)
    public String userdetalis(Model model, Principal principal) {
        model.addAttribute("title", "Mis Publicaciones");
        model.addAttribute("publication", new Publication());
        model.addAttribute("publications", publicationDao.findByUser(userDao.findByUserName(principal.getName())));

        return "userdetails";
        
    }
    
    @RequestMapping(value={"/userdetails"}, method = RequestMethod.POST)
    public String userdetalis(@Valid Publication publication, BindingResult result, Model model, Principal principal) {
        
        if (result.hasErrors()){
            model.addAttribute("title", "Principal");
            model.addAttribute("publications", publicationDao.findByUser(userDao.findByUserName(principal.getName())));
            return "mypublication";
        } else {
            publication.setDateOfSend(new Date());
            publication.setUser(userDao.findByUserName(principal.getName()));
            publication.setView(true);
            publicationDao.save(publication);
            return "redirect:/userdetails";
        }
    }
    
    @RequestMapping(value={"/editperfil"}, method = RequestMethod.GET)
    public String editperfil(Model model, Principal principal) {
        model.addAttribute("title", "Editar Perfil");
        model.addAttribute("user", userDao.findByUserName(principal.getName()));
        return "useredit";
    }
    
    @RequestMapping(value={"/editperfil"}, method = RequestMethod.POST)
    public String editperfilPOST(@RequestParam String username, @RequestParam String email,@RequestParam String function, Principal principal, Model model, HttpServletRequest request, HttpServletResponse response) {
        
        User user = userDao.findByUserName(principal.getName());
        
        if (function.equals("1")){
            
            boolean pass = true;
            boolean usernamechange = false;
            String message = "";
            
            if (!username.equals(user.getUsername())){
                if (username.length() > 3 && username.length() < 30){
                    if (userDao.findByUserName(username) == null){
                        user.setUsername(username);
                        usernamechange = true;
                    } else {
                        user.setUsername(username);
                        pass = false;
                        message += "Este nombre de usuario ya existe. ";
                    }
                } else {
                    user.setUsername(username);
                    pass = false;
                    message += "El tamano del usuario no es correcto, debe de ser mayor de 3 y menor de 30. ";
                }
            }
                
            if (!email.equals(user.getEmail())){
                if (esEmailCorrecto(email)){
                    if (userDao.findEmail(email) == null){
                        user.setEmail(email);
                    } else {
                        user.setEmail(email);
                        pass = false;
                        message += "Este correo ya existe. ";
                    }
                } else {
                    user.setEmail(email);
                    pass = false;
                    message += "El correo no es correcto. ";
                }
            }
            
            if (!pass){
                user.setEmail(email);
                model.addAttribute("error", message);
                model.addAttribute("user", user);
                return "useredit";
            } else {
                userDao.save(user);
                
                if (usernamechange){
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null) {
                        new SecurityContextLogoutHandler().logout(request, response, auth);
                    }
                    return "redirect:/login?logout";
                }
            }
        }
        
        return "redirect:/editperfil";
    }
    
    protected static boolean esEmailCorrecto(String email) {
       
        boolean valid = false;
       
        Pattern p = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
   
        Matcher mE = p.matcher(email.toLowerCase());
        if (mE.matches()){
           valid = true; 
        }
        return valid;
    }
}
