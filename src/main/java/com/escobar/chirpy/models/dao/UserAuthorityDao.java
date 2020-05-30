package com.escobar.chirpy.models.dao;

import com.escobar.chirpy.models.entity.Authority;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import com.escobar.chirpy.models.entity.UserAuthority;
import com.escobar.chirpy.models.entity.User;
import java.util.List;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserAuthorityDao {
	
    @PersistenceContext
    private EntityManager em;
    
    @Transactional
    public void save(UserAuthority userauthority) {
        em.persist(userauthority);	
    }
    
    @Transactional
    public void remove(UserAuthority userauthority) {
        em.remove(userauthority);	
    }
        
    public List<Authority> findByUser(User user) {
        TypedQuery<Authority> query = em.createQuery("SELECT u.authority FROM UserAuthority u WHERE u.user = :user", Authority.class); 
        query.setParameter("user", user);
        
        return query.getResultList();

    }
    
    public UserAuthority findUserAuthority(UserAuthority userAuthority) {
        TypedQuery<UserAuthority> query = em.createQuery("SELECT u FROM UserAuthority u WHERE u.user = :user and u.authority = :authority", UserAuthority.class); 
        query.setParameter("user", userAuthority.getUser());
        query.setParameter("authority", userAuthority.getAuthority());
        
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
        

    }
    
    public boolean isAdmin(User user) {
        TypedQuery<UserAuthority> query = em.createQuery("SELECT u FROM UserAuthority u WHERE u.user = :user and u.authority = (select a from Authority a where a.authority = 'admin')", UserAuthority.class); 
        query.setParameter("user", user);
        
        try {
            if (query.getSingleResult() != null){
                return true;
            }  
        } catch (Exception e) {}

        return false;


    }
        
}
