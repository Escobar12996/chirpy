package com.escobar.chirpy.models.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;

import com.escobar.chirpy.models.entity.Hashtag;
import com.escobar.chirpy.models.entity.HashtagPublication;
import com.escobar.chirpy.models.entity.Publication;

@Repository
public class HashtagPublicationDao {
	
    @PersistenceContext
    private EntityManager em;
    
    
        
    @Transactional
    public List<HashtagPublication> findByHashtag(Hashtag hashtag) {
        TypedQuery<HashtagPublication> query = em.createQuery("SELECT h FROM HashtagPublication h WHERE h.hashtag = :hashtag", HashtagPublication.class); 
        query.setParameter("hashtag", hashtag);
        
        return query.getResultList();
    }  
    
    
    public List<HashtagPublication> findHastagsPublication(Publication publi) {
        TypedQuery<HashtagPublication> query = em.createQuery("SELECT h FROM HashtagPublication h WHERE h.publication = :publi", HashtagPublication.class); 
        query.setParameter("publi", publi);
        return query.getResultList();

    }
    
    @Transactional
    public void remove(HashtagPublication hashtag) {
        em.remove(hashtag);	
    }
    
    @Transactional
    public void save(HashtagPublication hashtag) {
        em.persist(hashtag);	
    }
}
