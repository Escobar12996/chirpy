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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    
    @Autowired
    private MessageSource messages;
    
    //TODO Explorer metodo get
    @RequestMapping(value={"/", "/explorer"}, method = RequestMethod.GET)
    public String explorer(Model model, Principal principal) {
    	
    	//introducimos el titulo y las tendencias
    	model.addAttribute("title", messages.getMessage("text.explorer.tittle", null, LocaleContextHolder.getLocale()));
        model.addAttribute("trends", hashtagDao.findUp());
        
        //si hay algun usuario logeado, lo introducimos
        if (principal != null){
            model.addAttribute("user", userDao.findByUserName(principal.getName()));
        }
        
        return "aplication/explorer";
    }
    
  //TODO Explorer metodo post
    @RequestMapping(value={"/explorer"}, method = RequestMethod.POST)
    public String explorersend(@RequestParam("find") String find, Model model, Principal principal) {
        
    	//introducimos el titulo y las tendencias
    	model.addAttribute("title", messages.getMessage("text.explorer.tittle", null, LocaleContextHolder.getLocale()));
    	model.addAttribute("trends", hashtagDao.findUp());
    	
    	//si existe usuario logeado, lo introducimos
        if (principal != null){
            model.addAttribute("user", userDao.findByUserName(principal.getName()));
        }
        
        //si no a buscado con mas de 3 letras, devuelve error
        if(find.length() < 3){
            model.addAttribute("error", messages.getMessage("text.explorer.find.error", null, LocaleContextHolder.getLocale()));
            return "explorer";
            
        //si la busqueda contiene un @ busca solo usuarios
        } else if (find.contains("@")){
            find = find.replace("@", "");
            model.addAttribute("users", userDao.findUsersOnlyUsername(find));
            return "explorer";
            
        //sino lo busca todo
        } else {
            model.addAttribute("users", userDao.findUsersNameUsername(find));
            model.addAttribute("publications", publicationDao.findText(find));
            return "explorer";
        }
    }
    
    
    //este metodo, es para cuando se hace click en un hastag
    @RequestMapping(value={"/explorer/{hashtag}"}, method = RequestMethod.GET)
    public String hastags(@PathVariable String hashtag, Model model, Principal principal){

    	//introducimos el titulo y las tendencias
    	model.addAttribute("title", messages.getMessage("text.explorer.tittle", null, LocaleContextHolder.getLocale()));
    	model.addAttribute("trends", hashtagDao.findUp());
    	
    	//introducimos la busqueda
    	model.addAttribute("publications", publicationDao.findText(hashtag));

        
        
        //si existe el usuario logeado se carga
        if (principal != null){
            model.addAttribute("user", userDao.findByUserName(principal.getName()));
        }
    	
        return "explorer";
    	
    }
    
    
    
    
    
    
    
    
    
}
