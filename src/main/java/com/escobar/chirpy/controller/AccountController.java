/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.AuthorityDao;
import com.escobar.chirpy.models.dao.ConfirmationTokenDao;
import com.escobar.chirpy.models.dao.FollowDao;
import com.escobar.chirpy.models.dao.UserAuthorityDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.entity.ConfirmationToken;
import com.escobar.chirpy.models.entity.Follow;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.UserAuthority;
import com.escobar.chirpy.models.listener.OnRegistrationCompleteEvent;
import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author escob
 */

@Controller
public class AccountController {
    
    @Autowired
	ApplicationEventPublisher eventPublisher;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private FollowDao followDao;
    
    @Autowired
    private AuthorityDao authorityDao;
    
    @Autowired
    private UserAuthorityDao userAuthorityDao;
    
    @Autowired
    private ConfirmationTokenDao confirmationTokenDao;
    
    @Autowired
    private MessageSource messages;
    
    @Bean
    public BCryptPasswordEncoder paswordncoder() {
            return new BCryptPasswordEncoder();
    }
    
    //····················································································
    //TODO Zona de login de la aplicacion
    //····················································································
    
    //TODO Login
	@RequestMapping(value={"/login"}, method = RequestMethod.GET)
	public String loginPanel(Model model,
			Principal principal,
			@RequestParam(value = "register", required = false) String register,
			@RequestParam(value = "logout", required = false) String logout,
			@RequestParam(value = "resend", required = false) String resend) {
	
		
		//Si ya existe un usuario logeado, reenvia a home
		if (principal != null){
			return "redirect:/home";
		}
		
		
		//si has cerrado sesion añade sesion cerrada
		if(logout != null) {
			model.addAttribute("success", messages.getMessage("text.login.sesclo", null, LocaleContextHolder.getLocale()));
		}
		
		//si te acabas de registrar
		if(register != null) {
			model.addAttribute("success", messages.getMessage("text.accountdisabled.disabled", null, LocaleContextHolder.getLocale()));
		}
		
		//si has reenviado el codigo
		if(resend != null) {
			model.addAttribute("success", messages.getMessage("text.accountdisabled.disabled", null, LocaleContextHolder.getLocale()));
		}
		
		//titulo de la pagina
		model.addAttribute("title", messages.getMessage("text.login.tittle", null, LocaleContextHolder.getLocale()));
		
		return "aplication/login";
	}

	//TODO Metodo reenviar correo
	@RequestMapping(value= {"/resendemail"}, method = RequestMethod.GET)
	public String resendemail(Model model,
			HttpSession session,
			HttpServletRequest request) {
		
		
		//recogemos el correo del usuario y lo borramos de la sesion
		String correo = (String) session.getAttribute("email");
		session.removeAttribute("email");
		
		//buscamos el usuario
		User user = userDao.findEmail(correo);
		
		//enviamos el correo, usando el listener
		eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, 
        		LocaleContextHolder.getLocale(), request.getContextPath()));
		
		//redirigimos al login
		return "redirect:/login?resend"; 
	}
	
	//····················································································
    //TODO Zona de login de la aplicacion
    //····················································································
	
	
	//TODO Metodo get de la ventana de registro
	@RequestMapping(value= {"/register"}, method = RequestMethod.GET)
	public String register(Model model) {
		
		//Crea el usuario y lo añade como atributo al modelo y ademas el titulo de la pagina
		model.addAttribute("user", new User());
		model.addAttribute("title", messages.getMessage("text.register.tittle", null, LocaleContextHolder.getLocale()));
		return "aplication/register";
	}
	
	//TODO Metodo Post de la ventana de registro
    @RequestMapping(value={"/register"}, method = RequestMethod.POST)
    public String registeraccount(@Valid User user,
    		BindingResult result,
    		Model model,
    		HttpServletRequest request) {

    	//titulo de la ventana
    	model.addAttribute("title", messages.getMessage("text.register.tittle", null, LocaleContextHolder.getLocale()));
    	
    	//si hay un error al validar
        if (result.hasErrors()) {
	        return "aplication/register";
	        
        //si la cuenta ya existe
        } else if (userDao.findByUserName(user.getUsername()) != null){
	        model.addAttribute("error", messages.getMessage("text.register.error.accountexists", null, LocaleContextHolder.getLocale()));
	        return "aplication/register";
                
        //si el correo ya existe
        } else if (userDao.findEmail(user.getEmail()) != null) {
            model.addAttribute("error", messages.getMessage("text.register.error.emailexists", null, LocaleContextHolder.getLocale()));
            return "aplication/register";
                
        //si el correo no es valido
        } else if (!esEmailCorrecto(user.getEmail())) {
            model.addAttribute("error", messages.getMessage("text.register.error.emailinvalid", null, LocaleContextHolder.getLocale()));
            return "aplication/register";
         
        //si tiene mas de x caracteres la contraseña
        } else if (user.getPassword().length() > User.passwordmax) {
        	model.addAttribute("error", messages.getMessage("text.register.error.passwordmaxone", null, LocaleContextHolder.getLocale()) + " " + User.passwordmax + " " + messages.getMessage("text.register.error.passwordmaxtwo", null, LocaleContextHolder.getLocale()));
        	return "aplication/register";
        	
        //si todo es valido
        }else {
        	
        	//codificamos la contraseña
            PasswordEncoder encode = paswordncoder();
            user.setPassword(encode.encode(user.getPassword()));
            
            //configuramos la cuenta
            user.setEnabled(false);
            user.setNotLocker(true);
            user.setSystenBan(false);
            
            userDao.save(user);
            
            
            
            //hacemos que se siga a si mismo
            followDao.save(new Follow(user, user));
            
            //le añadimos su rol
            UserAuthority au = new UserAuthority();
            au.setUser(user);
            au.setAuthority(authorityDao.findByName("user"));
            userAuthorityDao.save(au);
            
            //enviamos el correo de activar cuenta
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, 
            		LocaleContextHolder.getLocale(), request.getContextPath()));
            
            return "redirect:/login?register"; 
        }
    }

    //TODO Metodo Get de confirmacion de cuenta
    @RequestMapping(value={"/confirmaccount"}, method = RequestMethod.GET)
    public String confirmAccount (Model model,
    		@RequestParam(value = "token", required = false) String token) {
        
        model.addAttribute("title", messages.getMessage("text.login.tittle", null, LocaleContextHolder.getLocale()));
    	//si el token no es nulo
        if (token != null) {
        	
        	//Buscamos el token
        	ConfirmationToken confirm = confirmationTokenDao.findConfirmationToken(token);
        	
        	//si no existe el token, enviamos que no existe
            if (confirm == null) {
                model.addAttribute("error", messages.getMessage("auth.message.invalidToken", null, LocaleContextHolder.getLocale()));
                return "aplication/login";
                
              //si existe el token
            } else {
            	
            	//extraemos el usuario y lo activamos
            	User user = confirm.getUser();
            	user.setEnabled(true);
            	userDao.update(user);
            	
            	//borramos el token y reenviamos a login con un mensaje de correcto
        		confirmationTokenDao.remove(confirm);
        		
            	model.addAttribute("success", messages.getMessage("auth.message.useractivated", null, LocaleContextHolder.getLocale()));
            	
                return "aplication/login";
            }
            
        //si el token es nulo
        } else {
        	return "redirect:/login?lang=" + LocaleContextHolder.getLocale().getLanguage();
        }
        
    } 
    
    //TODO comprueba que el email sea correcto
    public static boolean esEmailCorrecto(String email) {
       
        boolean valid = false;
       
        Pattern p = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
   
        Matcher mE = p.matcher(email.toLowerCase());
        if (mE.matches()){
           valid = true; 
        }
        return valid;
    }
}
