package com.escobar.chirpy.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.escobar.chirpy.models.dao.ImageDao;
import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.entity.Image;
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.miscellaneous.ImageResizer;

@Controller
public class AdministrationController {

	@Autowired
	private MessageSource messages;
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private PublicationDao publicationDao;
	
	@Autowired
	private ImageDao imageDao;
	
	@Bean
    public BCryptPasswordEncoder paswordcoder() {
            return new BCryptPasswordEncoder();
    }
	
    //TODO Explorer metodo get
    @RequestMapping(value={"/administration"}, method = RequestMethod.GET)
    public String explorer(Model model,
    		Principal principal) {

    	//introducimos el titulo
    	model.addAttribute("title", messages.getMessage("text.administration.main.tittle", null, LocaleContextHolder.getLocale()));
    	
        return "administration/home";
    }
	
    
  //TODO Explorer metodo get
    @RequestMapping(value={"/administration/users"}, method = RequestMethod.GET)
    public String users(Model model,
    		Principal principal,
    		@RequestParam(value = "page", required = false) String page) {

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
    	
    	
        return "administration/usersadministration";
    }
    
    //TODO Explorer metodo get
    @RequestMapping(value={"/administration/userimages/{id}"}, method = RequestMethod.GET)
    public String userimages(Model model,
    		Principal principal,
    		@PathVariable Long id) {
    	
    	User userc = userDao.findById(id);
    	
    	model.addAttribute("images", imageDao.findByUserAdmin(userc));
    	model.addAttribute("userc", userc);

    	
    	return "imagesuseradministration";
    	
    }
  
    
  //TODO Explorer metodo get
    @RequestMapping(value="/administration/userpost/{id}", method = RequestMethod.GET)
    public String userpost(Model model,
    		Principal principal,
    		@PathVariable Long id) {
    	
    	User userc = userDao.findById(id);
    	
    	model.addAttribute("publications", publicationDao.findByUserAdmin(userc));
    	model.addAttribute("userc", userc);
    	model.addAttribute("imageDao", imageDao);
    	
    	
    	return "publicationsuseradministration";
    	
    }
    
    
    
    //TODO Explorer metodo get
    @RequestMapping(value="/administration/posts", method = RequestMethod.GET)
    public String adminpost(Model model, Principal principal) {
    	
    	model.addAttribute("publications", publicationDao.findAll());
    	model.addAttribute("imageDao", imageDao);
    	
    	
    	return "publicationsadministration";
    	
    }
    
    
  //TODO Explorer metodo get
    @RequestMapping(value="/administration/images", method = RequestMethod.GET)
    public String adminimages(Model model, Principal principal) {
    	
    	model.addAttribute("images", imageDao.findAllAdmin());
    	
    	
    	return "imagesadministration";
    	
    }
    
    
    //TODO Explorer metodo get
      @RequestMapping(value={"/administration/deleteimage"}, method = RequestMethod.POST)
      public String deleteorremoveimages(Model model, Principal principal,
      		@RequestParam(value = "deleteimage", required = false) Long deleteimage, 
      		@RequestParam(value = "removeimage", required = false) Long removeimage,
      		@RequestParam(value = "post", required = false) Long post,
      		@RequestParam(value = "posts", required = false) Long posts,
      		@RequestParam(value = "images", required = false) Long images) {
      	
      	Image image = null;
      	
      	if (deleteimage != null) {
      		image = imageDao.findByIdAdmin(deleteimage);
      		
      		if (image != null) {
          		image.setView(!image.isView());
          		imageDao.save(image);
          		
          		if (post != null) {
          			return "redirect:/administration/userpost/"+image.getUser().getId();       			
          		}else if (posts != null) {
          			return "redirect:/administration/posts";      			
              	}else if (images != null) {
          			return "redirect:/administration/images";      			
              	}  else {
              		return "redirect:/administration/userimages/"+image.getUser().getId(); 
          		}
          		
          		
          		
          		
          	}
      	} else if (removeimage != null) {
  			image = imageDao.findByIdAdmin(removeimage);
      		
      		if (image != null) {
          		imageDao.remove(image);
          		if (post != null) {
          			return "redirect:/administration/userpost/"+image.getUser().getId();       			
          		}else if (posts != null) {
          			return "redirect:/administration/posts";      			
              	}else if (images != null) {
          			return "redirect:/administration/images";      			
              	} else {
              		return "redirect:/administration/userimages/"+image.getUser().getId(); 
          		}
          	}
      	}
      	return "redirect:/administration";
      	
      }
    
  //TODO Explorer metodo post
    @RequestMapping(value={"/administration/deletepost"}, method = RequestMethod.POST)
    public String deleteorremovepost(Model model, Principal principal,
    		@RequestParam(value = "deletepost", required = false) Long deletepost,
    		@RequestParam(value = "removepost", required = false) Long removepost,
    		@RequestParam(value = "removeallpost", required = false) Long removeallpost,
    		@RequestParam(value = "posts", required = false) Long posts) {
    	
    	Publication publi = null;
    	
    	if (deletepost != null) {
    		publi = publicationDao.findByIdAdmin(deletepost);
    		
    		if (publi != null) {
    			publi.setView(!publi.isView());
    			publicationDao.save(publi);
    			
    			if (posts == null) {
            		return "redirect:/administration/userpost/"+publi.getUser().getId();
    			} else {
    				return "redirect:/administration/posts";
    			}
        	}
    	} else if (removepost != null) {
    		publi = publicationDao.findByIdAdmin(removepost);
    		
    		if (publi != null) {
    			
    			List<Image> images = imageDao.findByPubliAdmin(publi);
    			
    			for (Image image : images) {
    				image.setPubli(null);
    				imageDao.update(image);
    			}
    			
				List<Publication> publis = publicationDao.findResponseAdmin(publi);
    			
    			for (Publication pu : publis) {
    				pu.setPubli(null);
    				publicationDao.update(pu);
    			}
    			
    			
    			publicationDao.remove(publi);
    			

    			if (posts == null) {
            		return "redirect:/administration/userpost/"+publi.getUser().getId();
    			} else {
    				return "redirect:/administration/posts";
    			}
        	}
    	} else if (removeallpost != null) {
    		publi = publicationDao.findByIdAdmin(removeallpost);
    		
    		if (publi != null) {
    			
    			List<Image> images = imageDao.findByPubliAdmin(publi);
    			
    			for (Image image : images) {
    				imageDao.remove(image);
    			}
    			
    			deletepublication(publicationDao.findResponseAdmin(publi));
    			
    			
    			
    			
    			publicationDao.remove(publi);


    			if (posts != null) {
    				return "redirect:/administration/posts";
    			} else {
    				return "redirect:/administration/userpost/"+publi.getUser().getId();
    			}
    			
        	}
    	}
    	return "redirect:/administration";
    	
    }
    
  //TODO Explorer metodo post
    @RequestMapping(value={"/administration/edituser/{id}"}, method = RequestMethod.GET)
    public String editusers(Model model, Principal principal,
    		@PathVariable Long id, HttpSession session) {
    	
    	User userc = userDao.findById(id);
    	
    	model.addAttribute("user", userc);
    	session.setAttribute("useredit", userc);
    	
    	return "usereditadministration";
    }
    
    
    //TODO Explorer metodo post
    @RequestMapping(value={"/activatedisableuser"}, method = RequestMethod.POST)
    public String activateuser(Model model, Principal principal,
    		@RequestParam(value = "id") Long id) {
    	
    	User us = userDao.findById(id);
    	
    	if (us != null) {
    		us.setEnabled(!us.getEnabled());
    		userDao.update(us);
    	}
    	
    	return "redirect:/administration/users";
    }
    
//TODO editar perfil metodo post
    
    @RequestMapping(value={"/administration/edituser"}, method = RequestMethod.POST)
    public String editeditImageProfilePOST(@Valid User user, BindingResult result, Principal principal, Model model, HttpSession session) {
        
        //introducimos el titulo
        model.addAttribute("title", messages.getMessage("text.edituser.tittle", null, LocaleContextHolder.getLocale()));
        
        //cargamos usuario de la base de datos
        User userc = userDao.findByUserName(((User) session.getAttribute("useredit")).getUsername());
        
        //si hay algun error, volvemos para atras
        if ( result.hasFieldErrors("username") || 
        		result.hasFieldErrors("name") || 
        		result.hasFieldErrors("email")){
        	
            return "usereditadministration";
        }    
           
        //guardamos la descripcion y el nombre
        userc.setDescription(user.getDescription());
        userc.setName(user.getName());
        
        //comprobamos si el nombre de usuario ha cambiado
        if (!userc.getUsername().equalsIgnoreCase(user.getUsername())){

            //comprobamos que no tenga otro usuario el mismo nombre
            if (userDao.findByUserName(user.getUsername()) == null){
            
                //introduzco el nuevo usuario
                userc.setUsername(user.getUsername());
                
            //Si ya existe un usuario
            } else if (userDao.findByUserName(user.getUsername()) != null){
                model.addAttribute("error", messages.getMessage("text.edituser.error.accountexists", null, LocaleContextHolder.getLocale()));
            }
        }
            
        //Guardo el usuario
        userDao.update(userc);
        
        model.addAttribute("user", userc);
    	session.setAttribute("useredit", userc);
        
        return "usereditadministration";
        
    }
    
    
    //TODO Explorer metodo post
    @RequestMapping(value={"/blockunblockuser"}, method = RequestMethod.POST)
    public String blockunblockuser(Model model, Principal principal,
    		@RequestParam(value = "id") Long id) {
    	
    	User us = userDao.findById(id);
    	
    	if (us != null) {
    		us.setNotLocker(!us.getNotLocker());
    		userDao.update(us);
    	}
    	
    	return "redirect:/administration/users";
    }
    
  //TODO Editar Contraseña metodo get
    @RequestMapping(value={"/administration/editpass"}, method = RequestMethod.POST)
    public String editpass(Model model, Principal principal, @RequestParam(value = "password", required = true) String password, HttpSession session) {
        
        User userc = userDao.findByUserName(((User) session.getAttribute("useredit")).getUsername());

        if (password.length() < 7) {
            model.addAttribute("error", messages.getMessage("Size.user.password", null, LocaleContextHolder.getLocale()));
        } else {
            model.addAttribute("success",  messages.getMessage("text.edituser.passwordupdate", null, LocaleContextHolder.getLocale()));
        	PasswordEncoder encode = paswordcoder();
            userc.setPassword(encode.encode(password));
            userDao.update(userc);
        }
        
        
        model.addAttribute("user", userc);
        return "usereditadministration";
    }
    
//TODO Editar imagen metodo post
    
    @RequestMapping(value={"/administration/editImageprofile"}, method = RequestMethod.POST)
    public String editImageProfile(@RequestParam(value = "delete", required = false) String delete, @RequestParam(value = "ima", required = false) MultipartFile file, Model model, Principal principal, HttpSession session) {
        
        //cargamos usuario de la base de datos
        User userc = userDao.findByUserName(((User) session.getAttribute("useredit")).getUsername());

        //si el fichero no es nulo y no esta vacio, cambiamos la imagen de perfil 
        if (delete == null && file != null && !file.isEmpty()) {

            //si el fichero es de tipo png o jpeg
            if (file.getContentType().contains("image/png") || file.getContentType().contains("image/jpeg")){
                try {
                    byte[] imagefile = file.getBytes();
                    userc.setImageperf(ImageResizer.imageResizeFromUser(imagefile, 100, 100));
                    userDao.save(userc);
                } catch (IOException e) {}

            //si no es una imagen de esos tipos
            } else {
                model.addAttribute("error", messages.getMessage("text.edituser.error.imageerror", null, LocaleContextHolder.getLocale()));

                model.addAttribute("user", userc);
                return "usereditadministration"; 
            }
        } else if ( delete != null && delete.contains("delete") ){
            userc.setImageperf(null);
            userDao.save(userc);
        }
        
        return "redirect:/administration/edituser/"+userc.getId();
    }
    
    //TODO Editar imagen metodo get
    
    @RequestMapping(value={"/administration/editImageprofile"}, method = RequestMethod.GET)
    public String editImageProfile() {
        return "redirect:/administration";
    }
    
  //TODO Editar imagen metodo post
    
    @RequestMapping(value={"/administration/imagesu"}, method = RequestMethod.POST)
    public String editImageSu(@RequestParam(value = "delete", required = false) String delete, @RequestParam(value = "ima", required = false) MultipartFile file, Model model, Principal principal, HttpSession session) {
        
        //cargamos usuario de la base de datos
        User userc = userDao.findByUserName(((User) session.getAttribute("useredit")).getUsername());


        //si el fichero no es nulo y no esta vacio, cambiamos la imagen de perfil 
        if (delete == null && file != null && !file.isEmpty()) {

            //si el fichero es de tipo png o jpeg
            if (file.getContentType().contains("image/png") || file.getContentType().contains("image/jpeg")){
                try {
                    byte[] imagefile = file.getBytes();
                    userc.setImagesu(ImageResizer.imageResizeFromImages(imagefile, 800));
                    userDao.save(userc);
                } catch (IOException e) {}

            //si no es una imagen de esos tipos
            } else {
            	model.addAttribute("error", messages.getMessage("text.edituser.error.imageerror", null, LocaleContextHolder.getLocale()));
                model.addAttribute("user", userc);
                return "usereditadministration"; 
            }
        } else if ( delete != null && delete.contains("delete") ){
            userc.setImagesu(null);
            userDao.save(userc);
        }
        
        return "redirect:/administration/edituser/"+userc.getId();
    }
    
    //TODO Editar imagen metodo get
    
    @RequestMapping(value={"/administration/imagesu"}, method = RequestMethod.GET)
    public String editImageSu() {
        return "redirect:/administration";
    }
    
    private void deletepublication(List<Publication> publis) {
    	for (Publication pu : publis) {
    		List<Publication> publiscas = publicationDao.findResponseAdmin(pu);
			deletepublication(publiscas);
			publicationDao.remove(pu);
		}
    }
    
}
