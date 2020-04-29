/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.HashtagDao;
import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserDao;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author escob
 */

@Controller
public class ExplorerController {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private PublicationDao publicationDao;
    
    @Autowired
    private HashtagDao hashtagDao;
    
    @RequestMapping(value={"/", "/explorer"}, method = RequestMethod.GET)
    public String explorer(Model model, Principal principal) {
    	model.addAttribute("title", "Explorador");
        System.out.println(hashtagDao.findUp().size());
        model.addAttribute("tendencias", hashtagDao.findUp());
        if (principal != null){
            model.addAttribute("user", userDao.findByUserName(principal.getName()));
        }
        return "explorer";
    }
    
    @RequestMapping(value={"/explorer"}, method = RequestMethod.POST)
    public String explorersend(@RequestParam("find") String find, Model model, Principal principal) {
        
        if (principal != null){
            model.addAttribute("user", userDao.findByUserName(principal.getName()));
        }
        
        if(find.length() < 3){
            model.addAttribute("title", "Explorador");
            model.addAttribute("error", "Debes de escribir mas de 3 caracteres");
            return "explorer";
        } else if (find.contains("@")){
            find = find.replace("@", "");
            model.addAttribute("title", "Explorador");
            model.addAttribute("users", userDao.findUsersOnlyUsername(find));
            return "explorer";
        } else {
            model.addAttribute("title", "Explorador");
            model.addAttribute("users", userDao.findUsersNameUsername(find));
            model.addAttribute("publications", publicationDao.findText(find));
            return "explorer";
        }
    }
    
    
    
    @RequestMapping(value={"/explorer/{hashtag}"}, method = RequestMethod.GET)
    public String hastags(@PathVariable String hashtag, Model model, Principal principal){
    	System.out.println(hashtag);
    	if (principal != null){
            model.addAttribute("user", userDao.findByUserName(principal.getName()));
        }
    	
    	model.addAttribute("title", "Explorador");
        model.addAttribute("publications", publicationDao.findText(hashtag));
        return "explorer";
    	
    }
    
    
    
    
    
    
    
    
    
}
