package com.escobar.chirpy.models.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.escobar.chirpy.models.dao.ConfirmationTokenDao;
import com.escobar.chirpy.models.entity.ConfirmationToken;
import com.escobar.chirpy.models.entity.User;

@Component
public class RegistrationListener implements
  ApplicationListener<OnRegistrationCompleteEvent> {
  
    @Autowired
    private MessageSource messages;
  
    @Autowired
    private ConfirmationTokenDao confirmationTokenDao;
    
    @Autowired
    private JavaMailSender mailSender;
 
    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }
 
    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        
        ConfirmationToken confirmation = new ConfirmationToken(user);
        
        if (confirmationTokenDao.findConfirmationUser(user) != null) {
        	confirmationTokenDao.update(confirmation);
        } else {
        	confirmationTokenDao.save(confirmation);
        }
        
        String recipientAddress = user.getEmail();
        String subject = messages.getMessage("text.message.confirmregister", null, event.getLocale());
        String confirmationUrl 
          = event.getAppUrl() + "/confirmaccount?token=" + confirmation.getConfirmationToken();
        String message = messages.getMessage("text.message.regSucc", null, event.getLocale());
         
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + " http://escobar.asuscomm.com" + confirmationUrl);
        mailSender.send(email);
    }
}
