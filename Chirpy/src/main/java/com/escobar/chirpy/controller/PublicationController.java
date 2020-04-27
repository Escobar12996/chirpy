package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.FollowDao;
import com.escobar.chirpy.models.dao.HashtagDao;
import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.dao.VidmaDao;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.services.PublicationService;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PublicationController {
    
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
    
    @RequestMapping(value={"/home"}, method = RequestMethod.GET)
    public String principalzone(Model model, Principal principal) {
        model.addAttribute("user", userDao.findByUserName(principal.getName()));
        model.addAttribute("publication", new Publication());
        model.addAttribute("title", "Principal");
        List<User> followsthisuser = followDao.getUserFollow(userDao.findByUserName(principal.getName()));
        List<Publication> publi = publicationDao.findByUsers(followsthisuser);
        model.addAttribute("publications", publi);
        model.addAttribute("tendencias", hashtagDao.findUp());
        model.addAttribute("vidmaDao", vidmaDao);
        return "home";
    }
    
    @RequestMapping(value={"/home"}, method = RequestMethod.POST)
    public String principalzonesendpublication(@Valid Publication publication, BindingResult result, Model model, Principal principal, @RequestParam(value = "image[]", required = false) MultipartFile file[]) {
        
        model.addAttribute("user", userDao.findByUserName(principal.getName()));
        
        if (result.hasErrors()){
            model.addAttribute("title", "Principal");
            List<User> followsthisuser = followDao.getUserFollow(userDao.findByUserName(principal.getName()));
            List<Publication> publi = publicationDao.findByUsers(followsthisuser);
            model.addAttribute("publications", publi);
            model.addAttribute("tendencias", hashtagDao.findUp());
            return "home";
        } else if (publication.getPublication().length() > maxletter) {
            model.addAttribute("title", "Principal");
            List<User> followsthisuser = followDao.getUserFollow(userDao.findByUserName(principal.getName()));
            List<Publication> publi = publicationDao.findByUsers(followsthisuser);
            model.addAttribute("errorpublication", "El limite maximo de palabras en la publicacion es de " + maxletter);
            model.addAttribute("publications", publi);
            model.addAttribute("tendencias", hashtagDao.findUp());
            return "home";
        }else {
        	
        	if (file != null) {
	        	for(int i = 0; i < file.length; i++) {
	            	MultipartFile fi = file[i];
	            	
	            	if(fi != null && !fi.isEmpty()) {
	            		if (!fi.getContentType().contains("image/png") && !fi.getContentType().contains("image/jpeg") && !fi.getContentType().contains("image/gif")) {
	            			model.addAttribute("title", "Principal");
	                        List<User> followsthisuser = followDao.getUserFollow(userDao.findByUserName(principal.getName()));
	                        List<Publication> publi = publicationDao.findByUsers(followsthisuser);
	                        model.addAttribute("errorimage", "Una imagen no es valida");
	                        model.addAttribute("publications", publi);
	                        model.addAttribute("tendencias", hashtagDao.findUp());
	                        return "home";
	                	}
	            	}
	            }
        	}
        	
            publication.setUser(userDao.findByUserName(principal.getName()));
            publicationService.formatedAndSave(publication, file);
            return "redirect:/home";
        }
    }

}
