package com.escobar.chirpy.models.dao;

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
        
    public List<UserAuthority> findByUser(User user) {
        TypedQuery<UserAuthority> query = em.createQuery("SELECT u FROM UserAuthority u WHERE u.user = :user", UserAuthority.class); 
        query.setParameter("user", user);
        
        return query.getResultList();

    }
        
}
