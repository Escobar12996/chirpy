package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.FollowDao;
import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.entity.Follow;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.Publication;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PublicationController {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private PublicationDao publicationDao;
    
    @Autowired
    private FollowDao followDao;
    
    @RequestMapping(value={"/", "/home"}, method = RequestMethod.GET)
    public String principalzone(Model model, Principal principal) {
        model.addAttribute("publication", new Publication());
        model.addAttribute("title", "Principal");
        List<User> followsthisuser = followDao.getUserFollow(userDao.findByUserName(principal.getName()));
        List<Publication> publi = publicationDao.findByUsers(followsthisuser);
        model.addAttribute("publications", publi);
        
        return "home";
    }
    
    @RequestMapping(value={"/home"}, method = RequestMethod.POST)
    public String principalzonesendpublication(@Valid Publication publication, BindingResult result, Model model, Principal principal) {
        
        if (result.hasErrors()){
            model.addAttribute("title", "Principal");
            return "home";
        } else {
            publication.setDateOfSend(new Date());
            publication.setUser(userDao.findByUserName(principal.getName()));
            publication.setView(true);
            publicationDao.save(publication);
            return "redirect:/home";
        }
    }
}
