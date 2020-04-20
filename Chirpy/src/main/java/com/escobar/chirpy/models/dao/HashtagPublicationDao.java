package com.escobar.chirpy.models.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;

import com.escobar.chirpy.models.entity.Hashtag;
import com.escobar.chirpy.models.entity.HashtagPublication;

@Repository
public class HashtagPublicationDao {
	
    @PersistenceContext
    private EntityManager em;
    
    @Transactional
    public void save(HashtagPublication hashtag) {
        em.persist(hashtag);	
    }
        
    @Transactional
    public List<HashtagPublication> findByHashtag(Hashtag hashtag) {
        TypedQuery<HashtagPublication> query = em.createQuery("SELECT h FROM HashtagPublication h WHERE h.hashtag = :hashtag", HashtagPublication.class); 
        query.setParameter("hashtag", hashtag);
        
        return query.getResultList();
    }  
}
