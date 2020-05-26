package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.ChatDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.dao.MessageDao;
import com.escobar.chirpy.models.entity.Message;
import com.escobar.chirpy.models.entity.Chat;
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
    private MessageDao messagesDao;
    
    @Autowired
    private ChatDao chatDao;
    
    @RequestMapping(value={"/messages"}, method = RequestMethod.GET)
    public String messages(Model model, Principal principal) {
        
        
        return "redirect:/home";
    }
    
    @RequestMapping(value={"/messages/{id}"}, method = RequestMethod.GET)
    public String chat(Model model, @PathVariable Long id, Principal principal, HttpSession session) {

        User user_received = userDao.findById(id);
        User user = userDao.findByUserName(principal.getName());
        
        if (user_received != null && principal != null){
            
            model.addAttribute("user", user);
            model.addAttribute("messages", messagesDao.findMessages(user, user_received));
            model.addAttribute("message", new Message());
            session.setAttribute("user_received", user_received);
            
            return "aplication/messages";
            
        }
        
        
        
        return "redirect:/home";
    }
    
    @RequestMapping(value={"/messages/send"}, method = RequestMethod.POST)
    public String senmessage( @Valid Message message,
    		BindingResult result, Model model, HttpSession session, Principal principal) {
        
        User user_received = (User) session.getAttribute("user_received");
        User user = userDao.findByUserName(principal.getName());
        
        if (user_received != null && user != null){
            if (result.hasErrors()){
                model.addAttribute("user", user);
                model.addAttribute("messages", messagesDao.findMessages(user, user_received));
                session.setAttribute("user_received", user_received);
                return "aplication/messages";
            }
            
            message.setDateOfSend(new Date());
            message.setSend(user);
            
            Chat chat = chatDao.findMessages(user, user_received);
            
            if (chat == null){
                chat = new Chat();
                chat.setUserone(user);
                chat.setUsertwo(user_received);
                chatDao.save(chat);
            }
            
            message.setChat(chat);
            messagesDao.save(message);
            
            return "redirect:/messages/"+user_received.getId();
        }
        
        
        
        
        return "redirect:/home";
    }
    
}
