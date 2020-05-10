package com.escobar.chirpy.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdministrationController {

	@Autowired
	private MessageSource messages;
	
	
    //TODO Explorer metodo get
    @RequestMapping(value={"/administration"}, method = RequestMethod.GET)
    public String explorer(Model model, Principal principal) {

    	//introducimos el titulo y las tendencias
    	model.addAttribute("title", messages.getMessage("text.administration.main.tittle", null, LocaleContextHolder.getLocale()));
    	
        return "layout/administrationLayout";
    }
	
    
  //TODO Explorer metodo get
    @RequestMapping(value={"/administration/users"}, method = RequestMethod.GET)
    public String users(Model model, Principal principal) {

    	//introducimos el titulo y las tendencias
    	model.addAttribute("title", messages.getMessage("text.administration.user.tittle", null, LocaleContextHolder.getLocale()));
    	
        return "layout/administrationLayout";
    }
    
    
    
    
}
