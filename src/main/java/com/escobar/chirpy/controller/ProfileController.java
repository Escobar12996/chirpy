package com.escobar.chirpy.controller;

import java.io.IOException;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
import com.escobar.chirpy.models.dao.FollowDao;
import com.escobar.chirpy.models.dao.HashtagDao;
import com.escobar.chirpy.models.dao.ImageDao;
import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserBanDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.dao.UserQuotePublicationDao;
import com.escobar.chirpy.models.entity.Follow;
import com.escobar.chirpy.models.entity.Image;
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.UserBan;
import com.escobar.chirpy.models.listener.UserBanEvent;
import com.escobar.chirpy.models.miscellaneous.ImageResizer;

@Controller
public class ProfileController {

	@Autowired
    private PublicationDao publicationDao;
    
    @Autowired
	ApplicationEventPublisher eventPublisher;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private UserQuotePublicationDao userQuotePublicationDao;
    
    @Autowired
    private ImageDao imageDao;
    
    @Autowired
    private MessageSource messages;
    
    @Autowired
    private HashtagDao hashtagDao;
    
    @Autowired
    private UserBanDao userBanDao;

    @Autowired
    private FollowDao followDao;
    
    @Bean
    public BCryptPasswordEncoder pasworcoder() {
            return new BCryptPasswordEncoder();
    }
	
	//································································
    //TODO Perfiles
    //································································
    
    //TODO Ver Perfil
    @RequestMapping(value={"/profile/{id}"}, method = RequestMethod.GET)
    public String userdetalis(Model model,
    		Principal principal,
    		@PathVariable Long id,
    		HttpServletRequest request,
    		HttpServletResponse response) {
    	
        User user = null;
        
    	if (id != null) {
    		
    		//cargamos el titulo
            model.addAttribute("title", messages.getMessage("text.userdetails.tittle", null, LocaleContextHolder.getLocale()));
            
            //cargamos el usaurio registrado
            if (principal != null){

            	user = userDao.findByUserName(principal.getName());
            	
                model.addAttribute("user", user);
                
                if (!user.getNotLocker()) {
                	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    new SecurityContextLogoutHandler().logout(request, response, auth);
                }
            	
            }
            
            //cargamos el usuario buscado y las publicaciones del usuario
            User userc = userDao.findById(id);
            
            if (userc != null) {
            	
            	model.addAttribute("userc", userc);
                model.addAttribute("publications", publicationDao.findByUser(userc));

                //cargamos las tendencias
                model.addAttribute("trends", hashtagDao.findUp());
                
                //cargamos si esta seguido o no por mi
                if (user != null){
                    Follow fo = new Follow(user, userc);
                    if (followDao.findFollow(fo) != null){
                        model.addAttribute("followedformy", true);
                    } else {
                        model.addAttribute("followedformy", false);
                    }
                }
                
                //introducimos el dao de las imagenes para poder cargar imagenes y el de las publicaciones
                model.addAttribute("imageDao", imageDao);
                model.addAttribute("publicationDao", publicationDao);
                model.addAttribute("publication", new Publication());
                return "aplication/profile";
            	
            }
    	}
    	
    	return "redirect:/home";
    	
    }
    
    //TODO Ver imagenes del perfil
    @RequestMapping(value={"/profileimages/{id}"}, method = RequestMethod.GET)
    public String userimages(Model model,
    		Principal principal,
    		@PathVariable Long id,
    		HttpServletRequest request,
    		HttpServletResponse response) {
    	
    	//cargamos el titulo
        model.addAttribute("title", messages.getMessage("text.userimages.tittle", null, LocaleContextHolder.getLocale()));
        
        User user = null;
        
        if (principal != null){
        	user = userDao.findByUserName(principal.getName());
        	
            model.addAttribute("user", user);
            
            if (!user.getNotLocker()) {
            	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
        }
        
        if (id != null) {
        	
        	User userc = userDao.findById(id);
        	
        	if (userc != null) {
        		model.addAttribute("userc", userDao.findById(id));
                model.addAttribute("images", imageDao.findByUser(userDao.findById(id)));
                
                //cargamos si esta seguido o no por mi
                if (user != null){
                    Follow fo = new Follow(user, userc);
                    if (followDao.findFollow(fo) != null){
                        model.addAttribute("followedformy", true);
                    } else {
                        model.addAttribute("followedformy", false);
                    }
                }
                
                //cargamos las tendencias
                model.addAttribute("trends", hashtagDao.findUp());
                
                return "aplication/profileimages";
        	}
        }
        
        return "redirect:/home";
    }
    
    //TODO Ver imagenes del perfil
    @RequestMapping(value={"/profileimages"}, method = RequestMethod.POST)
    public String saveuserimages(Model model,
    		Principal principal,
    		@PathVariable Long id,
    		HttpServletRequest request,
                @RequestParam(value = "image[]", required = false) MultipartFile file[],
    		HttpServletResponse response) {
    	
    	//cargamos el titulo
        model.addAttribute("title", messages.getMessage("text.userimages.tittle", null, LocaleContextHolder.getLocale()));
        
        if (principal != null){
            
            User user = userDao.findByUserName(principal.getName());
        	
            model.addAttribute("user", user);
            
            if (!user.getNotLocker()) {
            	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
        
        
            if (file != null) {
                for (MultipartFile fi : file) {
                    //si no es nulo y no esta vacio, comprobamos que NO es una imagen valida para lanzar error
                    if(fi != null && !fi.isEmpty() && !fi.getContentType().contains("image/png") && !fi.getContentType().contains("image/jpeg") && !fi.getContentType().contains("image/gif")) {

                        model.addAttribute("errorimage", messages.getMessage("text.home.error.imageerror", null, LocaleContextHolder.getLocale()));

                        //mostramos las tendencias de la ultima hora
                        model.addAttribute("trends", hashtagDao.findUp());

                        return "aplicacion/main";
                    }

                }

                for(int i = 0; i < file.length; i++) {

                    MultipartFile fi = file[i];
                    if (fi != null && !fi.isEmpty()) {

                        try {
                            Image v = new Image(fi.getBytes());
                            v.setView(true);
                            v.setUser(user);
                            imageDao.save(v);
                        } catch (IOException e) {}

                    }
                }
                
                return "redirect:/profileimages"+user.getId();
            }
            
            
        }
        
        return "redirect:/home";
    }
    

  //TODO Ver seguidos del perfil
    @RequestMapping(value={"/profilefollower/{id}"}, method = RequestMethod.GET)
    public String profilefollower(Model model,
    		Principal principal,
    		@PathVariable Long id,
    		HttpServletRequest request,
    		HttpServletResponse response) {
    	
    	//cargamos el titulo
        model.addAttribute("title", messages.getMessage("text.userdetails.follower", null, LocaleContextHolder.getLocale()));
        
        User user = null;
        
        if (principal != null){
        	user = userDao.findByUserName(principal.getName());
        	
            model.addAttribute("user", user);
            
            if (!user.getNotLocker()) {
            	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
        }
        
        if (id != null) {
        	
        	User userc = userDao.findById(id);
        	
        	if (userc != null) {
                    
                    //cargamos si esta seguido o no por mi
                if (user != null){
                    Follow fo = new Follow(user, userc);
                    if (followDao.findFollow(fo) != null){
                        model.addAttribute("followedformy", true);
                    } else {
                        model.addAttribute("followedformy", false);
                    }
                }
                    
                    
        		model.addAttribute("userc", userc);
                model.addAttribute("users", followDao.getUserFollow(userc));
                //cargamos las tendencias
                model.addAttribute("trends", hashtagDao.findUp());
                
                return "aplication/profilefollower";
        	}
        }
        
        return "redirect:/home";
    }
    
    
    //TODO Ver seguidos del perfil
    @RequestMapping(value={"/profilefollowers/{id}"}, method = RequestMethod.GET)
    public String profilefollowers(Model model,
    		Principal principal,
    		@PathVariable Long id,
    		HttpServletRequest request,
    		HttpServletResponse response) {
    	
    	//cargamos el titulo
        model.addAttribute("title", messages.getMessage("text.userdetails.follower", null, LocaleContextHolder.getLocale()));
        User user = null;
        
        if (principal != null){
        	user = userDao.findByUserName(principal.getName());
        	
            model.addAttribute("user", user);
            
            if (!user.getNotLocker()) {
            	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
        }
        
        if (id != null) {
        	
        	User userc = userDao.findById(id);
        	
        	if (userc != null) {
                    
                    //cargamos si esta seguido o no por mi
                if (user != null){
                    Follow fo = new Follow(user, userc);
                    System.out.println(fo.getFollowed());
                    if (followDao.findFollow(fo) != null){
                        model.addAttribute("followedformy", true);
                    } else {
                        model.addAttribute("followedformy", false);
                    }
                }
                    
        		model.addAttribute("userc", userc);
                model.addAttribute("users", followDao.getUserFollowers(userc));
                
                //cargamos las tendencias
                model.addAttribute("trends", hashtagDao.findUp());
                
                return "aplication/profilefollowers";
        	}
        }
        
        return "redirect:/home";
    }
    
    //··································································
    //TODO Editar perfil
    //··································································
    
    
    //TODO Editar perfil
    @RequestMapping(value={"/editprofile"}, method = RequestMethod.GET)
    public String editeditImageProfile(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("title", messages.getMessage("text.edituser.tittle", null, LocaleContextHolder.getLocale()));

        User user = userDao.findByUserName(principal.getName());
    	
        model.addAttribute("user", user);
        
        if (!user.getNotLocker()) {
        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        
        
        return "aplication/profileedit";
    }
    
    //TODO Editar perfil metodo post
    @RequestMapping(value={"/editprofile"}, method = RequestMethod.POST)
    public String editeditImageProfilePOST(@Valid User user, 
    		BindingResult result, 
    		Principal principal,
    		Model model,
    		HttpServletRequest request, 
    		HttpServletResponse response) {
        
        //bandera para detectar si se a cambiado el nombre
        boolean chna = false;
        
        //introducimos el titulo
        model.addAttribute("title", messages.getMessage("text.edituser.tittle", null, LocaleContextHolder.getLocale()));
        
        //cargamos usuario de la base de datos
        User userc = userDao.findByUserName(principal.getName());
        
        //si hay algun error, volvemos para atras
        if (result.hasErrors()){
            return "aplication/profileedit";
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
                    model.addAttribute("error", messages.getMessage("text.edituser.error.accountexists", null, LocaleContextHolder.getLocale()));
                }
            }
            
            //comprobamos si el correo a cambiado
            if (!userc.getEmail().equalsIgnoreCase(user.getEmail())){
                
                //comprobamos que no tenga otro usuario el mismo correo
                if (userDao.findEmail(user.getEmail()) == null){
                    
                    //Comprobamos que sea valido el correo
                    if (AccountController.esEmailCorrecto(user.getEmail())){
                        userc.setEmail(user.getEmail());
                        
                    //si no es valido el correo
                    } else {
                        model.addAttribute("error", messages.getMessage("text.edituser.error.emailinvalid", null, LocaleContextHolder.getLocale()));
                    }
                    
                //El email ya existe
                }else {
                    model.addAttribute("error", messages.getMessage("text.edituser.error.emailexists", null, LocaleContextHolder.getLocale()));
                }
                
                
            }
            
            //Guardo el usuario
            userDao.update(userc);
            
            
        //contraseña no valida
        } else {
            model.addAttribute("error", messages.getMessage("text.edituser.error.passwordinvalid", null, LocaleContextHolder.getLocale()));
        }
        
        //reenvio si se a cambiado el nombre, o simplemente muestro el template
        if (chna){
            return "redirect:/login";
        } else {
            model.addAttribute("user", userc);
            return "aplication/profileedit";
        }
    }
    
    //TODO Editar Contraseña metodo get
    
    @RequestMapping(value={"/editpass"}, method = RequestMethod.GET)
    public String editpass() {
        return "redirect:/editprofile";
    }
    
    //TODO Editar Contraseña metodo get
    @RequestMapping(value={"/editpass"}, method = RequestMethod.POST)
    public String editpass(Model model,
    		Principal principal,
    		@RequestParam(value = "password", required = true) String password,
            @RequestParam(value = "password1", required = true) String password1,
            @RequestParam(value = "passwordold", required = true) String passwordold) {
        
        //cargamos usuario de la base de datos
        User userc = userDao.findByUserName(principal.getName());
        
        //si la contraseña es valida
        if (passwordValidate(passwordold, userc)){
            
            //si las contraseñas son iguales
            if (password.equals(password1) && password.length() > 7){
                PasswordEncoder encode = pasworcoder();
                userc.setPassword(encode.encode(password));
                userDao.update(userc);
                model.addAttribute("success",  messages.getMessage("text.edituser.passwordupdate", null, LocaleContextHolder.getLocale()));
            //si las contraseñas son menores de 7 letras
            } else if (password.equals(password1) && password.length() < 7) {
                model.addAttribute("error", messages.getMessage("Size.user.password", null, LocaleContextHolder.getLocale()));
            //Si las contraseñas no son iguales 
            } else {
                model.addAttribute("error",  messages.getMessage("text.edituser.passwordiferent", null, LocaleContextHolder.getLocale()));
            }
            
            //si la contraseña no es valida
        } else {
            model.addAttribute("error", messages.getMessage("text.edituser.error.passwordinvalid", null, LocaleContextHolder.getLocale()));
        }
        
        
        //introducimos el titulo
        model.addAttribute("title", messages.getMessage("text.edituser.tittle", null, LocaleContextHolder.getLocale()));
        
        model.addAttribute("user", userc);
        return "aplication/profileedit";
    }
    
    //TODO Editar imagen metodo post
    @RequestMapping(value={"/editimageprofile"}, method = RequestMethod.POST)
    public String editImageProfile(@RequestParam(value = "delete", required = false) String delete, 
    		@RequestParam(value = "ima", required = false) MultipartFile file,
    		Model model, 
    		Principal principal) {
        
        //cargamos usuario de la base de datos
        User userc = userDao.findByUserName(principal.getName());
        
        //introducimos el titulo
        model.addAttribute("title", messages.getMessage("text.edituser.tittle", null, LocaleContextHolder.getLocale()));

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
                model.addAttribute("error", messages.getMessage("text.edituser.error.imageerror", null, LocaleContextHolder.getLocale()));

                model.addAttribute("user", userc);
                return "aplication/profileedit"; 
            }
        } else if ( delete != null && delete.contains("delete") ){
            userc.setImageperf(null);
            userDao.save(userc);
        }
        
        return "redirect:/editprofile";
    }
    
    //TODO Editar imagen metodo get
    @RequestMapping(value={"/editimageprofile"}, method = RequestMethod.GET)
    public String editImageProfile() {
        return "redirect:/editprofile";
    }
    
    //TODO Editar imagen metodo post
    @RequestMapping(value={"/imagesu"}, method = RequestMethod.POST)
    public String editImageSu(@RequestParam(value = "delete", required = false) String delete,
    		@RequestParam(value = "ima", required = false) MultipartFile file,
    		Model model, 
    		Principal principal) {
        
        //cargamos usuario de la base de datos
        User userc = userDao.findByUserName(principal.getName());
        model.addAttribute("user", userc);
        
      //introducimos el titulo
        model.addAttribute("title", messages.getMessage("text.edituser.tittle", null, LocaleContextHolder.getLocale()));
        
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
            	model.addAttribute("error", messages.getMessage("text.edituser.error.imageerror", null, LocaleContextHolder.getLocale()));
                model.addAttribute("user", userc);
                return "aplication/profileedit"; 
            }
        } else if ( delete != null && delete.contains("delete") ){
            userc.setImagesu(null);
            userDao.save(userc);
        }
        
        return "aplication/profileedit";
    }
    
    //TODO Editar imagen metodo get
    @RequestMapping(value={"/imagesu"}, method = RequestMethod.GET)
    public String editImageSu() {
        return "redirect:/editprofile";
    }
    
    //··································································
    //TODO quotes
    //··································································
    @RequestMapping(value={"/quotes"}, method = RequestMethod.GET)
    public String principalzone(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
    	
    	//usuario logeado
    	User userprin = userDao.findByUserName(principal.getName());
    	
    	//introducimos el usuario, el titulo y las tendencias    	
        model.addAttribute("user", userprin);
        
        if (!userprin.getNotLocker()) {
        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        
        
        
        model.addAttribute("title", messages.getMessage("text.quotes.tittle", null, LocaleContextHolder.getLocale()));
        model.addAttribute("trends", hashtagDao.findUp());
        model.addAttribute("quotepublications", userQuotePublicationDao.findByUser(userprin));
        
        //le quitamos las menciones al usuario y devolvemos la pestaña
        User u = userDao.findByUserName(principal.getName());
        u.setQuotes(0);
        userDao.update(u);
        
        return "aplication/quotes";
    }
    
  //··································································
    //TODO Reports
    //··································································
    @RequestMapping(value={"/report/{id}"}, method = RequestMethod.GET)
    public String userban(Model model, Principal principal, @PathVariable Long id) {
    	
    	Publication pu = publicationDao.findById(id);
    	
    	if (pu != null) {
    		
    		User u = userDao.findByUserName(principal.getName());
        	
        	UserBan usb = new UserBan(u, pu.getUser(), pu);
        	
        	try {
    			userBanDao.save(usb);
    		} catch (Exception e) {}

        	eventPublisher.publishEvent(new UserBanEvent(pu.getUser()));

        	return "redirect:/home?report";
    		
    	}
    	return "redirect:/home";
    }

    protected boolean passwordValidate(String password, User user){
        
        //validacion contraseña
        PasswordEncoder encode = pasworcoder();
        return encode.matches(password, user.getPassword());
        
    }
	
}
