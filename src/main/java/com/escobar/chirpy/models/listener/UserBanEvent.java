package com.escobar.chirpy.models.listener;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import com.escobar.chirpy.models.entity.User;

public class UserBanEvent extends ApplicationEvent {
    private User user;
 
    public UserBanEvent(User user) {
        super(user);
         
        this.user = user;
    }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
     
    // standard getters and setters
    
}
