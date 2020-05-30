/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.EmoticonDao;
import com.escobar.chirpy.models.dao.ImageDao;
import com.escobar.chirpy.models.dao.UserAuthorityDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.entity.Authority;
import com.escobar.chirpy.models.entity.Emoticon;
import com.escobar.chirpy.models.entity.Image;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.UserAuthority;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author escob
 */
@Controller
public class ImageController {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private ImageDao imageDao;
    
    @Autowired
    private EmoticonDao emoticonDao;
    
    
    
    @Autowired
    private UserAuthorityDao userAuthorityDao;
    
    @RequestMapping(value={"/image/{tipo}/{id}"}, method = RequestMethod.GET)
    public void showImage(@PathVariable String tipo,
    		@PathVariable Long id,
    		HttpServletResponse response,
    		Principal principal) throws ServletException, IOException{
        
    	//si estas buscando imagenes de perfil
        if (tipo.equals("user")){
            User user = userDao.findById(id);
            
		    if (user != null && user.getImageperf() != null) {
		    	response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			    response.getOutputStream().write(user.getImageperf());
			    response.getOutputStream().close();
		    } else {
		    	response.getOutputStream().close();
		    }
            
		  //si estas buscando imagenes "superiores"
        } else if (tipo.equals("imagesu")){
            User user = userDao.findById(id);
            
		    if (user != null && user.getImagesu() != null) {
		    	response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			    response.getOutputStream().write(user.getImagesu());
			    response.getOutputStream().close();
		    } else {
		    	response.getOutputStream().close();
		    }
		    
	    //si estas buscando imagenes
        } else if (tipo.equals("emoji")){
            Emoticon emo = emoticonDao.findemoticonId(id);
            
		    if (emo != null) {
		    	response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			    response.getOutputStream().write(emo.getImage());
			    response.getOutputStream().close();
		    } else {
		    	response.getOutputStream().close();
		    }
		    
	    //si estas buscando imagenes
        } else if (tipo.equals("image")) {
        	
        	Image v = imageDao.findById(id);
        	
        	if (v != null && v.getImages() != null) {
		    	response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			    response.getOutputStream().write(v.getImages());
			    response.getOutputStream().close();
		    } else {
		    	response.getOutputStream().close();
		    }
        	
        	//si estas buscando imagenes como administrador
        } else if(tipo.equals("admin")) {
        	
            User user = userDao.findByUserName(principal.getName());

            if (user != null && userAuthorityDao.isAdmin(user)) {

                Image v = imageDao.findByIdAdmin(id);

                if (v != null && v.getImages() != null) {
                    response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
                    response.getOutputStream().write(v.getImages());
                    response.getOutputStream().close();
                } else {
                    response.getOutputStream().close();
                }
            }
        } 
    }
}
