/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.EmoticonDao;
import com.escobar.chirpy.models.dao.HashtagDao;
import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.entity.User;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    
    @Autowired
    private EmoticonDao emoticonDao;
    
  //TODO Explorer metodo post
    @RequestMapping(value={"/explorer", "/"}, method = RequestMethod.GET)
    public String explorersend(@RequestParam(value = "find", required = false) String find,
    		Model model,
    		Principal principal,
    		HttpServletRequest request,
    		HttpServletResponse response,
                HttpSession session) {
        
    	//introducimos el titulo y las tendencias
    	model.addAttribute("title", messages.getMessage("text.explorer.tittle", null, LocaleContextHolder.getLocale()));
    	model.addAttribute("trends", hashtagDao.findUp());
    	
    	//si existe usuario logeado, lo introducimos
        if (principal != null){
        	
    		User user = userDao.findByUserName(principal.getName());
        	
            model.addAttribute("user", user);
            
            if (!user.getNotLocker()) {
            	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
        }
        
        //si no a buscado con mas de 3 letras, devuelve error
        if (find != null) {
            System.out.println(find);
        	if(find.length() < 3){
                model.addAttribute("error", messages.getMessage("text.explorer.find.error", null, LocaleContextHolder.getLocale()));
                    
            //si la busqueda contiene un @ busca solo usuarios
            } else if (find.contains("@")){
                
                session.setAttribute("find", find);
                find = find.replace("@", "");
                model.addAttribute("users", userDao.findUsersOnlyUsernameFirst(find));
                
                
            //sino lo busca todo
            } else {
                
                session.setAttribute("find", find);
                model.addAttribute("users", userDao.findUsersNameUsernameFirst(find));
                model.addAttribute("publications", publicationDao.findText(find));
                
            }
        }
        
        
        return "aplication/explorer";
    }
    
    
    
    @RequestMapping(value={"/viewemoticons"}, method = RequestMethod.GET)
    public String emoticons(Model model, Principal principal) {
		model.addAttribute("title" , messages.getMessage("text.administration.emoticon.tittle", null, LocaleContextHolder.getLocale()));
    	model.addAttribute("emoticons", emoticonDao.findall());
    	
		if (principal != null){
        	
			model.addAttribute("user", userDao.findByUserName(principal.getName()));
		}
    	
        return "aplication/emoticons";
    }
    
}
