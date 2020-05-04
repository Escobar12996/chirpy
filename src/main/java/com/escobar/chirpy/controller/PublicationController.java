package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.FollowDao;
import com.escobar.chirpy.models.dao.HashtagDao;
import com.escobar.chirpy.models.dao.ImageDao;
import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.services.PublicationService;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PublicationController {
    
    
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private ImageDao imageDao;
    
    @Autowired
    private PublicationDao publicationDao;
    
    @Autowired
    private PublicationService publicationService;
    
    @Autowired
    private HashtagDao hashtagDao;

    @Autowired
    private FollowDao followDao;
    
    @Autowired
    private MessageSource messages;
    
    //TODO Zona Principal metodo get
    
    @RequestMapping(value={"/home"}, method = RequestMethod.GET)
    public String home(Model model, Principal principal) {
    	
    	//guardamos el usuario el cual esta almacenado en la sesion
    	User userpri = userDao.findByUserName(principal.getName());
    	
    	//pasamos el titulo, el usuario actual y una publicacion para el formulario
    	model.addAttribute("title", messages.getMessage("text.home.tittle", null, LocaleContextHolder.getLocale()));
        model.addAttribute("user", userpri);
        model.addAttribute("publication", new Publication());
        
        //mostramos las publicacion de la gente a la que sigue el usuario
        model.addAttribute("publications", publicationDao.findByUsers(followDao.getUserFollow(userpri)));
        
        //mostramos las tendencias de la ultima hora
        model.addAttribute("trends", hashtagDao.findUp());
        
        //introducimos el dao de las imagenes para poder cargar imagenes y el de las publicaciones
        model.addAttribute("imageDao", imageDao);
        model.addAttribute("publicationDao", publicationDao);
        
        return "home";
    }
    
    
  //TODO Zona Principal metodo Post envio de publicacion
    
    @RequestMapping(value={"/home"}, method = RequestMethod.POST)
    public String principalzonesendpublication(@Valid Publication publication, BindingResult result, Model model, Principal principal, @RequestParam(value = "image[]", required = false) MultipartFile file[]) {
        
    	//guardamos el usuario el cual esta almacenado en la sesion
    	User userpri = userDao.findByUserName(principal.getName());
    	
    	//añadimos el usuario y el titulo al modelo
        model.addAttribute("user", userpri);
        model.addAttribute("title", messages.getMessage("text.home.tittle", null, LocaleContextHolder.getLocale()));
        
        //si hay algun error, lo mostramos y cargamos la informacion de la pagina
        if (result.hasErrors()){
            
        	//mostramos las publicacion de la gente a la que sigue el usuario
            model.addAttribute("publications", publicationDao.findByUsers(followDao.getUserFollow(userpri)));
            
            //mostramos las tendencias de la ultima hora
            model.addAttribute("trends", hashtagDao.findUp());
            
            //introducimos el dao de las imagenes para poder cargar imagenes y el de las publicaciones
            model.addAttribute("imageDao", imageDao);
            model.addAttribute("publicationDao", publicationDao);

            return "home";
            
        //si ha escrito mas palabras de las necesarias en la publicacion, mostramos el error
        } else if (publication.getPublication().length() > Publication.maxletter) {
        	
        	//devolvemos la publicacion y mostramos el error
            model.addAttribute("publication", publication);
            model.addAttribute("errorpublication", messages.getMessage("text.home.error.maxletterpubli", null, LocaleContextHolder.getLocale()) + " " + Publication.maxletter);

            //mostramos las publicacion de la gente a la que sigue el usuario
            model.addAttribute("publications", publicationDao.findByUsers(followDao.getUserFollow(userpri)));
            
            //mostramos las tendencias de la ultima hora
            model.addAttribute("trends", hashtagDao.findUp());
            
            //introducimos el dao de las imagenes para poder cargar imagenes y el de las publicaciones
            model.addAttribute("imageDao", imageDao);
            model.addAttribute("publicationDao", publicationDao);
            
            return "home";
            
        //si no hay ningun error
        }else {
        	
        	//si file no es nulo, comprobamos todas las imagenes
        	if (file != null) {
	        	for(int i = 0; i < file.length; i++) {
	            	MultipartFile fi = file[i];
	            	
	            	//si no es nulo y no esta vacio, comprobamos que NO es una imagen valida para lanzar error
	            	if(fi != null && !fi.isEmpty()) {
	            		if (!fi.getContentType().contains("image/png") && !fi.getContentType().contains("image/jpeg") && !fi.getContentType().contains("image/gif")) {

	                        
	                        
	                        
	                      //devolvemos la publicacion y mostramos el error
	                        model.addAttribute("publication", publication);
	                        model.addAttribute("errorimage", messages.getMessage("text.home.error.imageerror", null, LocaleContextHolder.getLocale()));
	                        
	                        //mostramos las publicacion de la gente a la que sigue el usuario
	                        model.addAttribute("publications", publicationDao.findByUsers(followDao.getUserFollow(userpri)));
	                        
	                        //mostramos las tendencias de la ultima hora
	                        model.addAttribute("tendencias", hashtagDao.findUp());
	                        
	                        //introducimos el dao de las imagenes para poder cargar imagenes y el de las publicaciones
	                        model.addAttribute("imageDao", imageDao);
	                        model.addAttribute("publicationDao", publicationDao);
	                        
	                        return "home";
	                	}
	            	}
	            }
        	}
        	
        	//si no hay ningun error, procedemos a formatearlo y guardarlo
            publication.setUser(userpri);
            publicationService.formatedAndSave(publication, file);
            
            return "redirect:/home";
        }
    }

    
    
    
    @RequestMapping(value={"/viewpublication/{id}"}, method = RequestMethod.GET)
    public String viewpublication(Model model, Principal principal, @PathVariable Long id) {
        model.addAttribute("user", userDao.findByUserName(principal.getName()));
        model.addAttribute("title", "Ver Publicacion");
        model.addAttribute("publicationview", publicationDao.findById(id));
        model.addAttribute("tendencias", hashtagDao.findUp());
        model.addAttribute("imageDao", imageDao);
        List<Publication> publi = publicationDao.findResponse(publicationDao.findById(id));
        model.addAttribute("publications", publi);
        model.addAttribute("publicationDao", publicationDao);
        model.addAttribute("publication", new Publication());
        return "viewpublication";
    }
}
