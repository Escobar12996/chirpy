/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.ImageDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.entity.Image;
import com.escobar.chirpy.models.entity.User;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @RequestMapping(value={"/image/{tipo}/{id}"}, method = RequestMethod.GET)
    public void showImage(@PathVariable String tipo, @PathVariable Long id, HttpServletResponse response) 
	          throws ServletException, IOException{
        
        if (tipo.equals("user")){
            User user = userDao.findById(id);
            
		    if (user != null && user.getImageperf() != null) {
		    	response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			    response.getOutputStream().write(user.getImageperf());
			    response.getOutputStream().close();
		    } else {
		    	response.getOutputStream().close();
		    }
            
        } else if (tipo.equals("imagesu")){
            User user = userDao.findById(id);
            
		    if (user != null && user.getImagesu() != null) {
		    	response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			    response.getOutputStream().write(user.getImagesu());
			    response.getOutputStream().close();
		    } else {
		    	response.getOutputStream().close();
		    }
        } else if (tipo.equals("image")) {
        	
        	Image v = imageDao.findById(id);
        	
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
