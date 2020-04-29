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
import com.escobar.chirpy.models.dao.UserQuotePublicationDao;
import com.escobar.chirpy.models.dao.VidmaDao;
import com.escobar.chirpy.models.entity.Authority;
import com.escobar.chirpy.models.entity.Follow;
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.UserAuthority;
import com.escobar.chirpy.models.entity.UserQuotePublication;
import com.escobar.chirpy.models.miscellaneous.ImageResizer;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
    private UserQuotePublicationDao userQuotePublicationDao;
    
    @Autowired
    private UserAuthorityDao userAuthorityDao;
    
    @Autowired
    private VidmaDao vidmaDao;
    
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
        } else if (userDao.findEmail(user.getEmail()) != null) {
                model.addAttribute("title", "Registrar Cuenta");
                model.addAttribute("error", "El correo ya existe");
                return "register";
        } else if (!esEmailCorrecto(user.getEmail())) {
                model.addAttribute("title", "Registrar Cuenta");
                model.addAttribute("error", "El correo no es valido");
                return "register";
        }else {
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
    
    @RequestMapping(value={"/userdetails/{id}"}, method = RequestMethod.GET)
    public String userdetalis(Model model, Principal principal, @PathVariable Long id) {
        model.addAttribute("title", "Detalles del usuario");
        if (principal != null){
            model.addAttribute("user", userDao.findByUserName(principal.getName()));
        }
        model.addAttribute("userc", userDao.findById(id));
        model.addAttribute("publications", publicationDao.findByUser(userDao.findById(id)));
        return "userdetails";
    }
    
    @RequestMapping(value={"/userimages/{id}"}, method = RequestMethod.GET)
    public String userimages(Model model, Principal principal, @PathVariable Long id) {
        model.addAttribute("title", "Detalles del usuario");
        if (principal != null){
            model.addAttribute("user", userDao.findByUserName(principal.getName()));
        }
        model.addAttribute("userc", userDao.findById(id));
        model.addAttribute("images", vidmaDao.findByUser(id));
        return "userimages";
    }

    
    
    
    
    @RequestMapping(value={"/editperfil"}, method = RequestMethod.GET)
    public String editperfil(Model model, Principal principal) {
        model.addAttribute("title", "Editar Perfil");
        model.addAttribute("user", userDao.findByUserName(principal.getName()));
        return "useredit";
    }
    
    @RequestMapping(value={"/editperfil"}, method = RequestMethod.POST)
    public String editperfilPOST(@Valid User user, BindingResult result, Principal principal, Model model, HttpServletRequest request, HttpServletResponse response) {
        
        //bandera para detectar si se a cambiado el nombre
        boolean chna = false;
        
        //cargamos usuario de la base de datos
        User userc = userDao.findByUserName(principal.getName());
        
        //si hay algun error, volvemos para atras
        if (result.hasErrors()){
            return "useredit";
        }    
        
        //si la contraseña es valida
        if (passwordValidate(user.getPassword(), userc)){
            
            //guardamos la descripcion y el nombre
            userc.setDescription(user.getDescription());
            userc.setName(user.getName());
            
            //comprobamos si el nombre de usuario ha cambiado
            if (!userc.getUsername().equalsIgnoreCase(user.getUsername())){

                //comprobamos que no tenga otro usuario el mismo nombre
                if (userDao.findByUserName(user.getUsername()) == null){
                
                    //introduzco el nuevo usuario
                    userc.setUsername(user.getUsername());
                    
                    //cierro la sesion, para evitar problemas con el autenticador, y activo la bandera de cambio de nombre de usuario
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    new SecurityContextLogoutHandler().logout(request, response, auth);
                    chna = true;
                    
                //Si ya existe un usuario
                } else if (userDao.findByUserName(user.getUsername()) != null){
                    model.addAttribute("error", "El nombre de usuario ya existe");
                }
            }
                
            //Guardo el usuario
            userDao.update(userc);
            
            
        //contraseña no valida
        } else {
            model.addAttribute("error", "Contraseña no valida");
        }
        
        //reenvio si se a cambiado el nombre, o simplemente muestro el template
        if (chna){
            return "redirect:/userdetails";
        } else {
            model.addAttribute("user", userc);
            return "useredit";
        }
    }
    
    @RequestMapping(value={"/editpass"}, method = RequestMethod.GET)
    public String editpass() {
        return "redirect:/editperfil";
    }
    
    @RequestMapping(value={"/editpass"}, method = RequestMethod.POST)
    public String editpass(Model model, Principal principal, @RequestParam(value = "password", required = true) String password,
            @RequestParam(value = "password1", required = true) String password1, @RequestParam(value = "passwordold", required = true) String passwordold) {
        
        //cargamos usuario de la base de datos
        User userc = userDao.findByUserName(principal.getName());
        
        //si la contraseña es valida
        if (passwordValidate(passwordold, userc)){
            
            //si las contraseñas son iguales
            if (password.equals(password1) && password.length() > 7){
                PasswordEncoder encode = paswordncoder();
                userc.setPassword(encode.encode(password));
                userDao.update(userc);
                model.addAttribute("success", "Contraseña actualizada");
            //si las contraseñas son menores de 7 letras
            } else if (password.equals(password1) && password.length() < 7) {
                model.addAttribute("error", "La contraseña tiene que ser mayor de 8 letras");
            //Si las contraseñas no son iguales
            } else {
                model.addAttribute("error", "Las contraseñas no son identicas");
            }
            
            //si la contraseña no es valida
        } else {
            model.addAttribute("error", "Contraseña no valida");
        }
        
        
        model.addAttribute("title", "Editar Perfil");
        model.addAttribute("user", userc);
        return "useredit";
    }
    
    @RequestMapping(value={"/editImagePerfil"}, method = RequestMethod.POST)
    public String editImagePerfil(@RequestParam(value = "delete", required = false) String delete, @RequestParam(value = "ima", required = false) MultipartFile file, Model model, Principal principal) {
        
        //cargamos usuario de la base de datos
        User userc = userDao.findByUserName(principal.getName());
        
        //si el fichero no es nulo y no esta vacio, cambiamos la imagen de perfil 
        if (delete == null && file != null && !file.isEmpty()) {

            //si el fichero es de tipo png o jpeg
            if (file.getContentType().contains("image/png") || file.getContentType().contains("image/jpeg")){
                try {
                    byte[] imagefile = file.getBytes();
                    userc.setImageperf(ImageResizer.imageResizeFromUser(imagefile, 100, 100));
                    userDao.save(userc);
                } catch (IOException e) {}

            //si no es una imagen de esos tipos
            } else {
                model.addAttribute("error", "No has subido una imagen valida");
                model.addAttribute("title", "Editar Perfil");
                model.addAttribute("user", userDao.findByUserName(principal.getName()));
                return "useredit"; 
            }
        } else if ( delete != null && delete.contains("delete") ){
            userc.setImageperf(null);
            userDao.save(userc);
        }
        
        return "redirect:/editperfil";
    }
    
    @RequestMapping(value={"/editImagePerfil"}, method = RequestMethod.GET)
    public String editImagePerfil() {
        return "redirect:/editperfil";
    }
    
    @RequestMapping(value={"/imagesu"}, method = RequestMethod.POST)
    public String editImageSu(@RequestParam(value = "delete", required = false) String delete, @RequestParam(value = "ima", required = false) MultipartFile file, Model model, Principal principal) {
        
        //cargamos usuario de la base de datos
        User userc = userDao.findByUserName(principal.getName());
        
        //si el fichero no es nulo y no esta vacio, cambiamos la imagen de perfil 
        if (delete == null && file != null && !file.isEmpty()) {

            //si el fichero es de tipo png o jpeg
            if (file.getContentType().contains("image/png") || file.getContentType().contains("image/jpeg")){
                try {
                    byte[] imagefile = file.getBytes();
                    userc.setImagesu(ImageResizer.imageResizeFromImages(imagefile, 800));
                    userDao.save(userc);
                } catch (IOException e) {}

            //si no es una imagen de esos tipos
            } else {
                model.addAttribute("error", "No has subido una imagen valida");
                model.addAttribute("title", "Editar Perfil");
                model.addAttribute("user", userDao.findByUserName(principal.getName()));
                return "useredit"; 
            }
        } else if ( delete != null && delete.contains("delete") ){
            userc.setImagesu(null);
            userDao.save(userc);
        }
        
        return "redirect:/editperfil";
    }
    
    @RequestMapping(value={"/imagesu"}, method = RequestMethod.GET)
    public String editImageSu() {
        return "redirect:/editperfil";
    }
    
    
    @RequestMapping(value={"/quotes"}, method = RequestMethod.GET)
    public String principalzone(Model model, Principal principal) {
        model.addAttribute("user", userDao.findByUserName(principal.getName()));
        model.addAttribute("title", "Quotes");
        List<UserQuotePublication> uqps = userQuotePublicationDao.findByUser(userDao.findByUserName(principal.getName()));
        List<Publication> pu = new ArrayList<Publication>();
        for (UserQuotePublication uq : uqps){
            pu.add(uq.getPublication());
        }
        model.addAttribute("publications", pu);
        
        User u = userDao.findByUserName(principal.getName());
        u.setQuotes(0);
        userDao.update(u);
        
        return "quotes";
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
    
    protected boolean passwordValidate(String password, User user){
        
        //validacion contraseña
        PasswordEncoder encode = paswordncoder();
        return encode.matches(password, user.getPassword());
        
    }
}
