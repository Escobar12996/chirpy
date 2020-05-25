/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.FollowDao;
import com.escobar.chirpy.models.dao.ImageDao;
import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserAuthorityDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.dao.UserQuotePublicationDao;
import com.escobar.chirpy.models.entity.Follow;
import com.escobar.chirpy.models.entity.Image;
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.UserAuthority;
import com.escobar.chirpy.models.entity.UserQuotePublication;
import com.escobar.chirpy.models.services.PublicationService;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

/**
 *
 * @author escob
 */

@Controller
public class ApyController {
	
	@Autowired
    private UserDao userDao;
    
    @Autowired
    private PublicationDao publicationDao;
    
    @Autowired
    private PublicationService publicationService;

    @Autowired
    private FollowDao followDao;
    
    @Autowired
    private ImageDao imageDao;
    
    @Autowired
    private UserAuthorityDao userAuthorityDao;
    
    @Autowired
    private UserQuotePublicationDao userQuotePublicationDao;
    
    @Autowired
    private MessageSource messages;
    
    
    //TODO Follow
    @ResponseBody
    @RequestMapping(value={"/follow/{id}"}, method = RequestMethod.POST)
    public String follow(Principal principal,
    		@PathVariable("id") Long id) {
    	
    	//usuario logeado
        User user = userDao.findByUserName(principal.getName());
        
        //si hay usuario logeado, y si se ha introducido una id
        if (user != null && id != null) {
        
	        //usuario que vas a seguir
	        User followed = new User();
	        followed.setId(id);
	        
	        //creamos el follow
	        Follow follow = new Follow();
	        follow.setUser(user);
	        follow.setFollowed(followed);
	
	        //lo intentamos guardar
	        if (followDao.findFollow(follow) == null){
	            try {
	                followDao.save(follow);
	                return "true";
	            } catch (Exception e) {
	                return "false";
	            }
	        } else {
	            return "true";
	        } 
        }
        
        return "false";
    }
    
    //TODO unFollow
    @ResponseBody
    @RequestMapping(value= {"/unfollow/{id}"}, method = RequestMethod.POST)
    public String unfollow(Principal principal,
    		@PathVariable("id") Long id) {
    	
    	//usuario logeado
        User user = userDao.findByUserName(principal.getName());
        
        //si hay usuario logeado, y si se ha introducido una id
        if (user != null && id != null) {
        
        	//usuario que vas a dejar seguir
	        User followed = new User();
	        followed.setId(id);
	        
	        //creamos el follow
	        Follow follow = new Follow();
	        follow.setUser(user);
	        follow.setFollowed(followed);
	        
	        //buscamos el follow
	        follow = followDao.findFollow(follow);
	        
	        //si existe y no estas intentando dejar de seguirte a ti mismo
	        if (follow != null && follow.getFollowed().getId() != user.getId()){
	            followDao.delete(follow);
	            return "true";
	        } else {
	            return "false";
	        }
        } 
        
        return "false";
    }
    
    //TODO devuelve los follows
    @ResponseBody
    @JsonProperty("jsonData")
    @RequestMapping(value= {"/getfollows"}, method = RequestMethod.POST)
    public List<User> getfollows(Principal principal) {
    	
    	//cargamos el usuario registrado
        User user = userDao.findByUserName(principal.getName());
        
        
        if (user != null) {
        	//creamos la lista con las personas a las que sigue, pero le quitamos las imagenes
        	List<User> list = new ArrayList<User>();
        
        
        	for (User fo : followDao.getUserFollow(user)){
                fo.setPassword("");
                fo.setImageperf(null);
                fo.setImagesu(null);
                list.add(fo);
            }
        	
        	//devolvemos la lista
        	return list;
        }
        
        
        return null;
    }
    
    //TODO Borrar Posts
    @ResponseBody
    @RequestMapping(value={"/deletepost/{id}"}, method = RequestMethod.POST)
    public String deletepost(Principal principal,
    		@PathVariable("id") Long id) {
    	
    	//Cargamos el usuario registrado
        User user = userDao.findByUserName(principal.getName());
        
        if (user != null && id != null) {
        	
        	//cargamos la publicacion 
            Publication pu = new Publication();
            pu.setUser(user);
            pu.setId(id);
            
            //cargamos la publicacion de la base de datos, para que no de error
            pu = publicationDao.findByUserAndId(pu);
            
            //si es posible la borramos
            if (pu != null){
                pu.setView(false);
                publicationDao.update(pu);
                
                return "true";
            } else {
                return "false";
            }
        	
        }
        
        return "false";
    }
    
    
    @ResponseBody
    @RequestMapping(value={"/home/response"}, method = RequestMethod.POST)
    public String principalzone(@Valid Publication publication,
    		BindingResult result,
    		Model model,
    		Principal principal,
    		@RequestParam(value = "image[]", required = false) MultipartFile file[],
    		HttpServletRequest request,
    		HttpServletResponse response) {

    	User user = userDao.findByUserName(principal.getName());
    	
    	
    	//si existe usuario
    	if (user != null) {
    		
    		//si tiene algun error, devuelve el error
            if (result.hasErrors()){
                return messages.getMessage("Size.publication.publication", null, LocaleContextHolder.getLocale());
                
            //si tiene mas caracteres devuelvo el error
            } else if (publication.getPublication().length() > Publication.maxletter) {
                return messages.getMessage("Size.publication.maxone", null, LocaleContextHolder.getLocale()) + " " + Publication.maxletter + " " + messages.getMessage("Size.publication.maxtwo", null, LocaleContextHolder.getLocale());
              
            
            }else {
            	
            	//si file no es nulo
            	if (file != null) {
    	        	for(int i = 0; i < file.length; i++) {
    	            	MultipartFile fi = file[i];
    	            	
    	            	//si no es nulo y no esta vacio, comprobamos que NO sea imagen para devolver error
    	            	if(fi != null && !fi.isEmpty()) {
    	            		if (!fi.getContentType().contains("image/png") && !fi.getContentType().contains("image/jpeg") && !fi.getContentType().contains("image/gif")) {
    	                        return messages.getMessage("text.apipublications.error.imageerror", null, LocaleContextHolder.getLocale());
    	                	}
    	            	}
    	            }
            	}
            	
            	//extraemos a que post va la respuesta gracias a la cookie y la borramos
            	Cookie cookieresp = WebUtils.getCookie(request, "resp");
            	Long publiresp = Long.parseLong(cookieresp.getValue());
            	cookieresp.setMaxAge(-1);
            	cookieresp.setPath("/");
            	cookieresp.setComment("EXPIRING COOKIE at " + System.currentTimeMillis());
            	response.addCookie(cookieresp);
            	
            	//le introducimos a la publicacion el usuario y la publicacion a la cual se responde y lo formateamos
            	publication.setPubli(publicationDao.findById(publiresp));
                publication.setUser(user);
                
                publicationService.formatedAndSave(publication, file);
                return "true";
            }
    		
    	}
    	
    	return "false";
    }
    
    //TODO Borrar Posts
    @ResponseBody
    @RequestMapping(value={"/deleteimage/{id}"}, method = RequestMethod.POST)
    public String deleteimage(Principal principal,
    		@PathVariable("id") Long id) {
    	
    	//Cargamos el usuario registrado
        User user = userDao.findByUserName(principal.getName());
        
        //si el usuario no es nulo
        if (user != null && id != null) {
        	
        	//Cargamos la imagen de la base de datos
            Image image = imageDao.findById(id);
            
            //si es posible la borramos
            if (image != null && image.getUser().getId() == user.getId()){
                image.setView(false);
                imageDao.update(image);
                
                return "true";
            } else {
                return "false";
            }
        	
        }
        
        return "false";
        
    }
    
  //TODO Recargar main
    @RequestMapping(value={"/refill/{page}"}, method = RequestMethod.POST)
    public String refill(Principal principal, Model model,
    		@RequestParam(value = "find", required = true) Long find,
    		@RequestParam(value = "last", required = true) Long last,
    		@PathVariable("page") String page) {
    	
    	if (page.contains("main")) {
    		//Cargamos el usuario registrado
            User user = userDao.findByUserName(principal.getName());
            
            //si el usuario no es nulo
            if (user != null) {
            	model.addAttribute("publications", publicationDao.findByUsersNext(followDao.getUserFollow(user), last));
            	model.addAttribute("imageDao", imageDao);
            	model.addAttribute("publicationDao", publicationDao);
            	
            	return "aplication/apy/refill";
            }
    	} else if (page.contains("view")) {
    		//Cargamos la publicacion buscada
            Publication pu = publicationDao.findById(find);
            
            //si el usuario no es nulo
            if (pu != null) {
            	System.out.println(last);
            	model.addAttribute("publications", publicationDao.findResponseNext(pu, last));
            	System.out.println(publicationDao.findResponseNext(pu, last).size());
            	model.addAttribute("imageDao", imageDao);
            	model.addAttribute("publicationDao", publicationDao);
            	
            	return "aplication/apy/refillview";
            }
            
    	} else if (page.contains("profile")) {

    		//Cargamos el usuario buscado
            User user = userDao.findById(find);
            
            //si el usuario no es nulo
            if (user != null) {
            	model.addAttribute("publications", publicationDao.findByUserNext(user, last));
            	model.addAttribute("imageDao", imageDao);
            	model.addAttribute("publicationDao", publicationDao);
            	
            	return "aplication/apy/refillview";
            }
    		
    	} else if (page.contains("quotes")) {

    		//Cargamos el usuario
            User user = userDao.findByUserName(principal.getName());
            
            //si el usuario no es nulo
            if (user != null) {

                model.addAttribute("quotepublications", userQuotePublicationDao.findByUserNext(user, last));
            	
            	return "aplication/apy/refillquotes";
            }
    		
    	} else if (page.contains("adminprof")) {

    		//Cargamos el usuario buscado
			User user = userDao.findByUserName(principal.getName());
        	
        	if (user != null) {
        		List<UserAuthority> ua = userAuthorityDao.findByUser(user);
            	boolean flag = false;
            	
            	for (UserAuthority au :ua) {
            		if (au.getAuthority().getAuthority().equals("admin"))
            			flag = true;
            	}
            
            	User u = userDao.findById(find);
	            //si el usuario es admin
	            if (flag) {
	            	System.out.println(last);
	            	model.addAttribute("publications", publicationDao.findByUserAdminNext(u, last));
	            	model.addAttribute("imageDao", imageDao);
	            	model.addAttribute("publicationDao", publicationDao);
	            	
	            	return "administration/apy/refillprofile";
	            }
        	}
    		
    	} else if (page.contains("allpublicationsadmin")) {

    		//Cargamos el usuario buscado
			User user = userDao.findByUserName(principal.getName());
        	
        	if (user != null) {
        		List<UserAuthority> ua = userAuthorityDao.findByUser(user);
            	boolean flag = false;
            	
            	for (UserAuthority au :ua) {
            		if (au.getAuthority().getAuthority().equals("admin"))
            			flag = true;
            	}
            
	            //si el usuario es admin
	            if (flag) {
            	model.addAttribute("publications", publicationDao.findAdminNext(last));
            	model.addAttribute("imageDao", imageDao);
            	model.addAttribute("publicationDao", publicationDao);
            	
            	return "administration/apy/refillpublications";
	            }
        	}
    	} else if (page.contains("reloadnew")) {

    		User user = userDao.findByUserName(principal.getName());
            
            //si el usuario no es nulo
            if (user != null) {
                
                System.out.println(last);
                List<Publication> pu =  publicationDao.findByUserNew(followDao.getUserFollow(user), last);
                List<Publication> inverso = new ArrayList<>();
                
                
                for (int i = pu.size()-1; i >= 0; i--){
                    inverso.add(pu.get(i));
                }
                
            	model.addAttribute("publications", inverso);
            	model.addAttribute("imageDao", imageDao);
            	model.addAttribute("publicationDao", publicationDao);
            	
            	return "aplication/apy/refill";
            }
    		
    	} else if (page.contains("refillimages")) {

    		User user = userDao.findById(find);
            
            //si el usuario no es nulo
            if (user != null) {
                
                if (principal != null){
                    model.addAttribute("user", userDao.findByUserName(principal.getName()));
                }
                
                model.addAttribute("userc", user);
            	model.addAttribute("images", imageDao.findByUserNext(user, last));
            	
            	return "aplication/apy/refillimages";
            }
    		
    	} else if (page.contains("adminfimapro")) {

    		//Cargamos el usuario buscado
                User user = userDao.findByUserName(principal.getName());
        	
        	if (user != null) {
        		List<UserAuthority> ua = userAuthorityDao.findByUser(user);
            	boolean flag = false;
            	
            	for (UserAuthority au :ua) {
            		if (au.getAuthority().getAuthority().equals("admin"))
            			flag = true;
            	}
            
                User u = userDao.findById(find);
                //si el usuario es admin
                if (flag) {
                    model.addAttribute("images", imageDao.findByUserAdminNext(u, last));

                    return "administration/apy/refillprofileimages";
                }
            }
    	} else if (page.contains("adminfimaall")) {

    		//Cargamos el usuario buscado
                User user = userDao.findByUserName(principal.getName());
        	
        	if (user != null) {
                    List<UserAuthority> ua = userAuthorityDao.findByUser(user);
                    boolean flag = false;
            	
            	for (UserAuthority au :ua) {
            		if (au.getAuthority().getAuthority().equals("admin"))
            			flag = true;
            	}

                //si el usuario es admin
                if (flag) {
                    model.addAttribute("images", imageDao.findAllAdminNext(last));

                    return "administration/apy/refillprofileimages";
                }
            }
    	}
    	
    	
        
        return null;
    }
}
