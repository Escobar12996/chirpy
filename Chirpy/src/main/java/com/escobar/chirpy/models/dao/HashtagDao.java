package com.escobar.chirpy.models.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;

import com.escobar.chirpy.models.entity.Hashtag;

@Repository
public class HashtagDao {
	
    @PersistenceContext
    private EntityManager em;
    
    @Transactional
    public void save(Hashtag hashtag) {
        em.persist(hashtag);	
    }
    
    @Transactional
    public void update(Hashtag hashtag) {
        em.merge(hashtag);	
    }
        
    public Hashtag findByHashtagName(String name) {
        TypedQuery<Hashtag> query = em.createQuery("SELECT h FROM Hashtag h WHERE h.hashtagname = :hashtagname", Hashtag.class); 
        query.setParameter("hashtagname", name);
        
        try {
        	return query.getSingleResult();
        } catch (Exception e) {
			return null;
		}
        

    }
        
}
