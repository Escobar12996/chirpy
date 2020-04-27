/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.FollowDao;
import com.escobar.chirpy.models.dao.HashtagDao;
import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.dao.VidmaDao;
import com.escobar.chirpy.models.entity.Follow;
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.services.PublicationService;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

/**
 *
 * @author escob
 */

@Controller
public class ApyController {
    
	private final int maxletter = 300;
	
	@Autowired
    private UserDao userDao;
    
    @Autowired
    private VidmaDao vidmaDao;
    
    @Autowired
    private PublicationDao publicationDao;
    
    @Autowired
    private PublicationService publicationService;
    
    @Autowired
    private HashtagDao hashtagDao;

    @Autowired
    private FollowDao followDao;
    
    
    
    @RequestMapping(value={"/follow/{id}"}, method = RequestMethod.POST)
    @ResponseBody
    public String follow(Principal principal, @PathVariable("id") Long id) {
        User user = userDao.findByUserName(principal.getName());
        
        User followed = new User();
        followed.setId(id);
        
        Follow follow = new Follow();
        follow.setUser(user);
        follow.setFollowed(followed);

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
    
    @RequestMapping(value= {"/unfollow/{id}"}, method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(Principal principal, @PathVariable("id") Long id) {
        User user = userDao.findByUserName(principal.getName());
        
        User followed = new User();
        followed.setId(id);
        
        Follow follow = new Follow();
        follow.setUser(user);
        follow.setFollowed(followed);
        follow = followDao.findFollow(follow);
        
        if (follow != null){
            followDao.delete(follow);
            return "true";
        } else {
            return "false";
        }
    }
    
    @RequestMapping(value= {"/getfollows"}, method = RequestMethod.POST)
    @JsonProperty("jsonData")
    @ResponseBody
    public List<User> getfollows(Principal principal) {
        User user = userDao.findByUserName(principal.getName());
       
        List<User> list = new ArrayList<User>();
        for (User fo : followDao.getUserFollow(user)){
            fo.setPassword("");
            fo.setImageperf(null);
            fo.setImagesu(null);
            list.add(fo);
        }
        return list;
    }
    
    @RequestMapping(value={"/deletepost/{id}"}, method = RequestMethod.POST)
    @ResponseBody
    public String deletepost(Principal principal, @PathVariable("id") Long id) {
        User user = userDao.findByUserName(principal.getName());
        Publication pu = new Publication();
        pu.setUser(user);
        pu.setId(id);
        pu = publicationDao.findByUserAndId(pu);
        
        if (pu != null){
            pu.setView(false);
            publicationDao.update(pu);
            return "true";
        } else {
            return "false";
        }
        
        
    }
    
    @RequestMapping(value={"/home/response"}, method = RequestMethod.POST)
    @ResponseBody
    public String principalzone(@Valid Publication publication, BindingResult result, Model model, Principal principal, @RequestParam(value = "image[]", required = false) MultipartFile file[], @CookieValue(value = "resp") Long id) {
    	model.addAttribute("user", userDao.findByUserName(principal.getName()));
        
        if (result.hasErrors()){
            return "El texto debe de tener entre 3 y 300 caracteres";
            
        } else if (publication.getPublication().length() > maxletter) {
            return "El limite maximo de palabras en la publicacion es de " + maxletter;
            
        }else {
        	
        	if (file != null) {
	        	for(int i = 0; i < file.length; i++) {
	            	MultipartFile fi = file[i];
	            	
	            	if(fi != null && !fi.isEmpty()) {
	            		if (!fi.getContentType().contains("image/png") && !fi.getContentType().contains("image/jpeg") && !fi.getContentType().contains("image/gif")) {
	                        return "Una imagen no es valida";
	                	}
	            	}
	            }
        	}
        	
        	publication.setPubli(publicationDao.findById(id));
            publication.setUser(userDao.findByUserName(principal.getName()));
            publicationService.formatedAndSave(publication, file);
            return "<span class='bg-primary'>Hello <b>Again</b></span>";
        }
    }
    
}
