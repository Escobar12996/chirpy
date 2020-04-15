/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserDao;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @RequestMapping(value={"/", "/explorer"}, method = RequestMethod.GET)
    public String explorer(Model model, Principal principal) {
        model.addAttribute("title", "Explorador");
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
            System.out.println(find);
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
}
