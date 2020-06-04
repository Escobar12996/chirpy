package com.escobar.chirpy.models.listener;

import com.escobar.chirpy.models.dao.ImageDao;
import com.escobar.chirpy.models.dao.PublicationDao;
import com.escobar.chirpy.models.dao.UserAuthorityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.escobar.chirpy.models.dao.UserBanDao;
import com.escobar.chirpy.models.dao.UserDao;
import com.escobar.chirpy.models.entity.Image;
import com.escobar.chirpy.models.entity.Publication;
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
        
        @Autowired
        private PublicationDao publicationDao;
        
        @Autowired
        private ImageDao imagedao;
	
    @Override
    public void onApplicationEvent(UserBanEvent event) {
        this.confirmEvent(event);
    }
 
    private void confirmEvent(UserBanEvent event) {
        User user = event.getUser();
        
        if (userBanDao.findByUserBan(user).size() > 9 && !userAuthDao.isAdmin(user)) {
        	
        	user.setNotLocker(false);
        	user.setSystenBan(true);
        	userdao.update(user);
                
                for(Publication pu : publicationDao.findUser(user)){
                    pu.setView(false);
                    publicationDao.update(pu);
                }
                
                for(Image i : imagedao.findUser(user)){
                    i.setView(false);
                    imagedao.update(i);
                }
                
                user.setNotLocker(false);
        	user.setSystenBan(true);
        	userdao.update(user);
        }
        
    }
}
