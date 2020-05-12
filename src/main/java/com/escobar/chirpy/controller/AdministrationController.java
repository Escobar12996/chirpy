package com.escobar.chirpy.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.escobar.chirpy.models.dao.ImageDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.entity.User;

@Controller
public class AdministrationController {

	@Autowired
	private MessageSource messages;
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private ImageDao imageDao;
	
    //TODO Explorer metodo get
    @RequestMapping(value={"/administration"}, method = RequestMethod.GET)
    public String explorer(Model model, Principal principal) {

    	//introducimos el titulo
    	model.addAttribute("title", messages.getMessage("text.administration.main.tittle", null, LocaleContextHolder.getLocale()));
    	
        return "layout/administrationLayout";
    }
	
    
  //TODO Explorer metodo get
    @RequestMapping(value={"/administration/users"}, method = RequestMethod.GET)
    public String users(Model model, Principal principal, @RequestParam(value = "page", required = false) String page) {

    	//introducimos el titulo
    	model.addAttribute("title", messages.getMessage("text.administration.user.tittle", null, LocaleContextHolder.getLocale()));
    	
    	int numuser = userDao.userCount();
    	int numpag = 1;
    	
    	
    	if (numuser > 10) {
    		numpag = numuser/10;
    		if (numuser % (float) 10 != 0) {
    			numpag++;
    		}
    	}
    	
    	if(page == null) {
    		model.addAttribute("users", userDao.findUsers(new Long(1), new Long(10)));
    		model.addAttribute("page", 1);
    		if (numpag >= 2)
    		model.addAttribute("pagenext", 2);
    		if (numpag >= 3)
    		model.addAttribute("pagenextnext", 3);
    		
    		model.addAttribute("numpag", numpag);
    	} else {
    		
    		
    		model.addAttribute("users", userDao.findUsers(Long.parseLong(page), new Long(10)));
    		
    		if (Integer.parseInt(page) > 2)
        		model.addAttribute("pagepreviousprevious", Integer.parseInt(page) - 2);
    		
    		if ( Integer.parseInt(page) > 1)
        		model.addAttribute("pageprevious", Integer.parseInt(page) - 1);
        	
    		
    		model.addAttribute("page", Integer.parseInt(page));
    		
    		if (numpag >= (Integer.parseInt(page) + 1))
    			model.addAttribute("pagenext", Integer.parseInt(page) + 1);
    		if (numpag  >= (Integer.parseInt(page) + 2))
    			model.addAttribute("pagenextnext", Integer.parseInt(page) + 2);
    		
    		model.addAttribute("numpag", numpag);
    	}
    	
    	
        return "usersadministration";
    }
    
    //TODO Explorer metodo get
    @RequestMapping(value={"/administration/userimages/{id}"}, method = RequestMethod.GET)
    public String userimages(Model model, Principal principal, @PathVariable Long id) {
    	
    	User userc = userDao.findById(id);
    	
    	model.addAttribute("images", imageDao.findByUserAdmin(userc));
    	model.addAttribute("userc", userc);

    	
    	return "imagesadministration";
    	
    }
    
    
}
