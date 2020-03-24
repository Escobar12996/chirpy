package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.Publication;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PublicationController {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private PublicationDao publicationDao;
    
    
    @RequestMapping(value={"/", "/home"}, method = RequestMethod.GET)
    public String principalzone(Model model) {
        model.addAttribute("publication", new Publication());
        model.addAttribute("title", "Principal");
        return "home";
    }
    
    @RequestMapping(value={"/home"}, method = RequestMethod.POST)
    public String principalzonesendpublication(@Valid Publication publication, BindingResult result, Model model, Principal principal) {
        
        if (result.hasErrors()){
            model.addAttribute("title", "Principal");
            return "home";
        } else {
            publication.setUser(userDao.findByUserName(principal.getName()));
            publication.setView(true);
            publicationDao.save(publication);
            return "redirect:/home";
        }
    }
    
    @RequestMapping(value={"/mypublication"}, method = RequestMethod.GET)
    public String mypublication(Model model, Principal principal) {
        model.addAttribute("title", "Mis Publicaciones");
        model.addAttribute("publication", new Publication());
        model.addAttribute("publications", publicationDao.findByUser(userDao.findByUserName(principal.getName())));

        return "mypublication";
    }
    
    @RequestMapping(value={"/mypublication"}, method = RequestMethod.POST)
    public String mypublicationnew(@Valid Publication publication, BindingResult result, Model model, Principal principal) {
        
        if (result.hasErrors()){
            model.addAttribute("title", "Principal");
            return "mypublication";
        } else {
            publication.setUser(userDao.findByUserName(principal.getName()));
            publication.setView(true);
            publicationDao.save(publication);
            return "redirect:/mypublication";
        }
    }
    
    @RequestMapping(value={"/explorer"}, method = RequestMethod.GET)
    public String explorer(Model model) {
        return "explorer";
    }
    
    @RequestMapping(value={"/explorer"}, method = RequestMethod.POST)
    public String explorersend(@RequestParam("find") String find, Model model) {
        List<User> users = userDao.findUsers(find);
        System.out.println(users.size());
        return "explorer";
    }
}
