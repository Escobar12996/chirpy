/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.FollowDao;
import com.escobar.chirpy.models.dao.HashtagDao;
import com.escobar.chirpy.models.dao.ImageDao;
import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.entity.Follow;
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.entity.User;
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
import org.springframework.web.bind.annotation.CookieValue;
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
    private MessageSource messages;
    
    
    //TODO Follow
    @ResponseBody
    @RequestMapping(value={"/follow/{id}"}, method = RequestMethod.POST)
    public String follow(Principal principal, @PathVariable("id") Long id) {
    	
    	//usuario logeado
        User user = userDao.findByUserName(principal.getName());
        
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
    
    //TODO unFollow
    @ResponseBody
    @RequestMapping(value= {"/unfollow/{id}"}, method = RequestMethod.POST)
    public String unfollow(Principal principal, @PathVariable("id") Long id) {
    	
    	//usuario logueado
        User user = userDao.findByUserName(principal.getName());
        
      //usuario que vas a seguir
        User followed = new User();
        followed.setId(id);
        
        //creamos el follow
        Follow follow = new Follow();
        follow.setUser(user);
        follow.setFollowed(followed);
        
        //buscamos el follow
        follow = followDao.findFollow(follow);
        
        //si existe lo borramos
        if (follow != null){
            followDao.delete(follow);
            return "true";
        } else {
            return "false";
        }
    }
    
    //TODO devuelve los follows
    @ResponseBody
    @JsonProperty("jsonData")
    @RequestMapping(value= {"/getfollows"}, method = RequestMethod.POST)
    public List<User> getfollows(Principal principal) {
    	
    	//cargamos el usuario registrado
        User user = userDao.findByUserName(principal.getName());
       
        //creamos la lista con las personas a las que sigue, pero le quitamos las imagenes por que no las necesitamos
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
    
    //TODO Borrar Posts
    @ResponseBody
    @RequestMapping(value={"/deletepost/{id}"}, method = RequestMethod.POST)
    public String deletepost(Principal principal, @PathVariable("id") Long id) {
    	
    	//Cargamos el usuario registrado
        User user = userDao.findByUserName(principal.getName());
        
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
    
    
    @ResponseBody
    @RequestMapping(value={"/home/response"}, method = RequestMethod.POST)
    public String principalzone(@Valid Publication publication, BindingResult result, Model model, Principal principal, @RequestParam(value = "image[]", required = false) MultipartFile file[], HttpServletRequest request, HttpServletResponse response) {

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
        	response.addCookie(cookieresp);
        	
        	//le introducimos a la publicacion el usuario y la publicacion a la cual se responde y lo formateamos
        	publication.setPubli(publicationDao.findById(publiresp));
            publication.setUser(userDao.findByUserName(principal.getName()));
            publicationService.formatedAndSave(publication, file);
            return "true";
        }
    }
    
}
