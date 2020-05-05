/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.AuthorityDao;
import com.escobar.chirpy.models.dao.FollowDao;
import com.escobar.chirpy.models.dao.HashtagDao;
import com.escobar.chirpy.models.dao.ImageDao;
import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserAuthorityDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.dao.UserQuotePublicationDao;
import com.escobar.chirpy.models.entity.Follow;
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.UserAuthority;
import com.escobar.chirpy.models.entity.UserQuotePublication;
import com.escobar.chirpy.models.miscellaneous.ImageResizer;
import com.escobar.chirpy.models.services.JpaUserDetailsService;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private ImageDao imageDao;
    
    @Autowired
    private JpaUserDetailsService jpaUserDetailsService;
    
    @Autowired
    private MessageSource messages;
    
    @Autowired
    private HashtagDao hashtagDao;
    
    @Bean
    public BCryptPasswordEncoder paswordncoder() {
            return new BCryptPasswordEncoder();
    }
    
    //TODO Login de la aplicacion
    
	@RequestMapping(value={"/login"}, method = RequestMethod.GET)
	public String loginPanel(Model model, Principal principal, @RequestParam(value = "logout", required = false) String logout) {
	
		//Si ya existe un usuario logeado, reenvia a home
		if (principal != null){
			return "redirect:/home";
		}
		
		//si has cerrado sesion añade sesion cerrada
		if(logout != null) {
			model.addAttribute("success", messages.getMessage("text.login.sesclo", null, LocaleContextHolder.getLocale()));
		}
		
		//titulo de la pagina
		model.addAttribute("title", messages.getMessage("text.login.tittle", null, LocaleContextHolder.getLocale()));
		return "login";
	}
    
     
	//TODO Metodo get de la ventana de registro
	
	@RequestMapping(value= {"/register"}, method = RequestMethod.GET)
		public String register(Model model) {
		
		//Crea el usuario y lo añade como atributo al modelo y ademas el titulo de la pagina
		model.addAttribute("user", new User());
		model.addAttribute("title", messages.getMessage("text.register.tittle", null, LocaleContextHolder.getLocale()));
		return "register";
	}
	
	//TODO Metodo Post de la ventana de registro
	
    @RequestMapping(value={"/register"}, method = RequestMethod.POST)
    public String chech(@Valid User user, BindingResult result, Model model) {

    	//titulo de la ventana
    	model.addAttribute("title", messages.getMessage("text.register.tittle", null, LocaleContextHolder.getLocale()));
    	
    	//si hay un error al validar
        if (result.hasErrors()) {
	        return "register";
	        
        //si la cuenta ya existe
        } else if (userDao.findByUserName(user.getUsername()) != null){
	        model.addAttribute("error", messages.getMessage("text.register.error.accountexists", null, LocaleContextHolder.getLocale()));
	        return "register";
                
        //si el correo ya existe
        } else if (userDao.findEmail(user.getEmail()) != null) {
            model.addAttribute("error", messages.getMessage("text.register.error.emailexists", null, LocaleContextHolder.getLocale()));
            return "register";
                
        //si el correo no es valido
        } else if (!esEmailCorrecto(user.getEmail())) {
            model.addAttribute("error", messages.getMessage("text.register.error.emailinvalid", null, LocaleContextHolder.getLocale()));
            return "register";
         
        //si tiene mas de x caracteres la contraseña
        } else if (user.getPassword().length() > User.passwordmax) {
        	model.addAttribute("error", messages.getMessage("text.register.error.passwordmaxone", null, LocaleContextHolder.getLocale()) + " " + User.passwordmax + " " + messages.getMessage("text.register.error.passwordmaxtwo", null, LocaleContextHolder.getLocale()));
        	return "register";
        	
        //si todo es valido
        }else {
        	
        	//extraemos la contraseña para el autologin
        	String pass = user.getPassword();
        	
        	//codificamos la contraseña
            PasswordEncoder encode = paswordncoder();
            user.setPassword(encode.encode(user.getPassword()));
            
            //activamos la cuenta y la guardamos
            user.setEnabled(true);
            user.setNotLocker(true);
            userDao.save(user);
            
            //hacemos que se siga a si mismo
            followDao.save(new Follow(user, user));
            
            //le añadimos su rol
            UserAuthority au = new UserAuthority();
            au.setUser(user);
            au.setAuthority(authorityDao.findByName("user"));
            userAuthorityDao.save(au);
            
            //hacemos autologin
            jpaUserDetailsService.autoLogin(user.getUsername(), pass);
            
            
            return "redirect:/login"; 
        }
    }
    
    //TODO ver perfiles
    
    @RequestMapping(value={"/userdetails/{id}"}, method = RequestMethod.GET)
    public String userdetalis(Model model, Principal principal, @PathVariable Long id) {
    	
    	//cargamos el titulo
        model.addAttribute("title", messages.getMessage("text.userdetails.tittle", null, LocaleContextHolder.getLocale()));
        
        //cargamos el usaurio registrado
        if (principal != null){
            model.addAttribute("user", userDao.findByUserName(principal.getName()));
        }
        
        //cargamos el usuario buscado y las publicaciones del usuario
        User userc = userDao.findById(id);
        model.addAttribute("userc", userc);
        model.addAttribute("publications", publicationDao.findByUser(userc));

        //cargamos las tendencias
        model.addAttribute("trends", hashtagDao.findUp());
        
        
        //introducimos el dao de las imagenes para poder cargar imagenes y el de las publicaciones
        model.addAttribute("imageDao", imageDao);
        model.addAttribute("publicationDao", publicationDao);
        model.addAttribute("publication", new Publication());
        return "userdetails";
    }
    
    @RequestMapping(value={"/userimages/{id}"}, method = RequestMethod.GET)
    public String userimages(Model model, Principal principal, @PathVariable Long id) {
    	
    	//cargamos el titulo
        model.addAttribute("title", messages.getMessage("text.userimages.tittle", null, LocaleContextHolder.getLocale()));
        
        if (principal != null){
            model.addAttribute("user", userDao.findByUserName(principal.getName()));
        }
        
        model.addAttribute("userc", userDao.findById(id));
        model.addAttribute("images", imageDao.findByUser(userDao.findById(id)));
        
        //cargamos las tendencias
        model.addAttribute("trends", hashtagDao.findUp());
        
        return "userimages";
    }

    
    
    
    
    @RequestMapping(value={"/editprofile"}, method = RequestMethod.GET)
    public String editperfil(Model model, Principal principal) {
        model.addAttribute("title", "Editar Perfil");
        model.addAttribute("user", userDao.findByUserName(principal.getName()));
        return "useredit";
    }
    
    @RequestMapping(value={"/editprofile"}, method = RequestMethod.POST)
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
            return "redirect:/login";
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
    
    @RequestMapping(value={"/editImageprofile"}, method = RequestMethod.POST)
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
        
        return "redirect:/editprofile";
    }
    
    @RequestMapping(value={"/editImageprofile"}, method = RequestMethod.GET)
    public String editImagePerfil() {
        return "redirect:/editprofile";
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
        
        return "redirect:/editprofile";
    }
    
    @RequestMapping(value={"/imagesu"}, method = RequestMethod.GET)
    public String editImageSu() {
        return "redirect:/editprofile";
    }
    
    //TODO quotes
    @RequestMapping(value={"/quotes"}, method = RequestMethod.GET)
    public String principalzone(Model model, Principal principal) {
    	
    	//usuario logeado
    	User userprin = userDao.findByUserName(principal.getName());
    	
    	//introducimos el usuario, el titulo y las tendencias
        model.addAttribute("user", userprin);
        model.addAttribute("title", messages.getMessage("text.quotes.tittle", null, LocaleContextHolder.getLocale()));
        model.addAttribute("trends", hashtagDao.findUp());
        
        //extraemos el listado de las publicaciones a partir de usequote y los introducimos
        List<Publication> pu = new ArrayList<Publication>();
        for (UserQuotePublication uq : userQuotePublicationDao.findByUser(userprin)){
            pu.add(uq.getPublication());
        }
        model.addAttribute("publications", pu);
        
        //le quitamos las menciones al usuario y devolvemos la pestaña
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
