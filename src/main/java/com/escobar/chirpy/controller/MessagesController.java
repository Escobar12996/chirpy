package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.dao.MessagesDao;
import com.escobar.chirpy.models.entity.Message;
import com.escobar.chirpy.models.entity.Publication;
import com.escobar.chirpy.models.entity.User;
import java.security.Principal;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MessagesController {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private MessagesDao messagesDao;
    
    @RequestMapping(value={"/messages"}, method = RequestMethod.GET)
    public String userimages(Model model, Principal principal) {
        
            User user = userDao.findByUserName(principal.getName());
            
            model.addAttribute("user", user);
            return "aplication/messages";
    }
    
    @RequestMapping(value={"/messages/{id}"}, method = RequestMethod.GET)
    public String userimages(Model model, @PathVariable Long id, Principal principal, HttpSession session) {
            
        User user = userDao.findByUserName(principal.getName());
        User userReceived = userDao.findById(id);
        
        if (userReceived != null){
           model.addAttribute("user", user);
           model.addAttribute("userr", userReceived);
           
           model.addAttribute("messages", messagesDao.findChat(user, userReceived));

           model.addAttribute("message", new Message());
           session.setAttribute("userreceived", userReceived);
           return "aplication/chat"; 
        }
        
        
        return "redirect:/home";
    }
    
    @RequestMapping(value={"/messages/send"}, method = RequestMethod.POST)
    public String senmessage( @Valid Message message,
    		BindingResult result, Model model, HttpSession session, Principal principal) {
        
        if (result.hasErrors()){
                return "redirect:/home";
        }
        
        message.setSend(userDao.findByUserName(principal.getName()));
        message.setDateOfSend(new Date());
        message.setReceive( (User) session.getAttribute("userreceived"));
        session.removeAttribute("userreceived");
            messagesDao.save(message);
        
           return "redirect:/messages/" + message.getReceive().getId();
    }
    
}
