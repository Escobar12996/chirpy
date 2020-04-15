package com.escobar.chirpy.models.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import com.escobar.chirpy.models.entity.User;
import com.escobar.chirpy.models.entity.UserQuotePublication;
import java.util.List;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserQuotePublicationDao {
	
    @PersistenceContext
    private EntityManager em;
    
    @Transactional
    public void save(UserQuotePublication userQuotePublication) {
        em.persist(userQuotePublication);	
    }
        
    @Transactional
    public List<UserQuotePublication> findByUser(User user) {
        TypedQuery<UserQuotePublication> query = em.createQuery("SELECT u FROM UserQuotePublication u WHERE u.user = :user", UserQuotePublication.class); 
        query.setParameter("user", user);
        
        return query.getResultList();
    }
    
    @Transactional
    public UserQuotePublication findPublication(UserQuotePublication userQuotePublication) {
        TypedQuery<UserQuotePublication> query = em.createQuery("SELECT u FROM UserQuotePublication u WHERE u.user = :user and u.publication = :publication", UserQuotePublication.class); 
        query.setParameter("user", userQuotePublication.getUser());
        query.setParameter("publication", userQuotePublication.getPublication());
        
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
        
}
