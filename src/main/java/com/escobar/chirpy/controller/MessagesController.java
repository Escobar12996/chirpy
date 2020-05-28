package com.escobar.chirpy.controller;

import com.escobar.chirpy.models.dao.ChatDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.dao.MessageDao;
import com.escobar.chirpy.models.entity.Message;
import com.escobar.chirpy.models.entity.Chat;
import com.escobar.chirpy.models.entity.ChatMessage;
import com.escobar.chirpy.models.entity.User;
import java.security.Principal;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class MessagesController {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private MessageDao messagesDao;
    
    @Autowired
    private ChatDao chatDao;
    
    @Autowired
    private SimpMessagingTemplate messageSender;
    
    @RequestMapping(value={"/messages"}, method = RequestMethod.GET)
    public String messages(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        
        if (principal != null){
        	
            User user = userDao.findByUserName(principal.getName());
        	
            model.addAttribute("user", user);
            
            if (!user.getNotLocker()) {
            	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            
            model.addAttribute("chats", chatDao.findChats(user));
            model.addAttribute("messagesDao", messagesDao);
            return "aplication/chats";
        }
        
        return "redirect:/home";
    }
    
    @RequestMapping(value={"/messages/{id}"}, method = RequestMethod.GET)
    public String chat(Model model, @PathVariable Long id, Principal principal, HttpSession session) {

        User user_received = userDao.findById(id);
        User user = userDao.findByUserName(principal.getName());
        
        if (user_received != null && principal != null){
            
            model.addAttribute("user", user);
            model.addAttribute("userReceived", user_received);
            model.addAttribute("messages", messagesDao.findMessages(user, user_received));

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
            
            Chat chat = chatDao.findChat(user, user_received);
            
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
    
    
    @MessageMapping("/message")
    public void receivedMessage(ChatMessage chatmessage, Principal principal){

        User user = userDao.findByUserName(principal.getName());
        User userReceived = userDao.findById(chatmessage.getIdReceived());
        
        Message message = new Message();
        System.out.println("algo");
        System.out.println(userReceived.getName());
        System.out.println(user.getName());
        System.out.println(chatmessage.getContent());
        if (userReceived != null && user != null && chatmessage.getContent().length() > 1){
            
            message.setText(chatmessage.getContent());
            
            message.setDateOfSend(new Date());
            message.setSend(user);
            
            Chat chat = chatDao.findChat(user, userReceived);
            
            if (chat == null){
                chat = new Chat();
                chat.setUserone(user);
                chat.setUsertwo(userReceived);
                chatDao.save(chat);
            }
            
            message.setChat(chat);
            messagesDao.save(message);
            
            chatmessage.setSender(user.getName());
            chatmessage.setIdSender(user.getId());
            
            messageSender.convertAndSendToUser(userReceived.getUsername(), "/queue/"+user.getId()+"/messages", chatmessage);
            messageSender.convertAndSendToUser(user.getUsername(), "/queue/"+userReceived.getId()+"/messages", chatmessage);
        }
        
    }
    
}
