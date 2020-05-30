package com.escobar.chirpy.models.listener;

import com.escobar.chirpy.models.dao.UserAuthorityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.escobar.chirpy.models.dao.UserBanDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.entity.User;


@Component
public class UserBanListener implements
  ApplicationListener<UserBanEvent> {
 
        @Autowired
	private UserAuthorityDao userAuthDao;
    
	@Autowired
	private UserDao userdao;
	
	@Autowired
	private UserBanDao userBanDao;
	
    @Override
    public void onApplicationEvent(UserBanEvent event) {
        this.confirmEvent(event);
    }
 
    private void confirmEvent(UserBanEvent event) {
        User user = event.getUser();
        
        if (userBanDao.findByUserBan(user).size() > 10 && !userAuthDao.isAdmin(user)) {
        	
        	user.setNotLocker(false);
        	user.setSystenBan(true);
        	userdao.update(user);
        }
        
    }
}
